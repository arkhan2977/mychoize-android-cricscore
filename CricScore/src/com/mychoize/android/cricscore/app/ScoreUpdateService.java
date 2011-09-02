package com.mychoize.android.cricscore.app;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class ScoreUpdateService extends Service {

	private ScoreUpdate oldScoreUpdate;
	private ScoreUpdate newScoreUpdate;
	private OptionValues values;
	private DataProvider provider;
	private Timer timer;
	private int matchId;

	// For Toast
	private Handler handler;
	private Toast toast;
	private long lastToast;

	// For Notification
	private NotificationManager notifyManager;
	private Intent notificationIntent;
	private static final int NOTIFY_SCORE = 1;

	public ScoreUpdateService() {
		this.values = new OptionValues();
		this.timer = new Timer("scoreUpdateTimer");
		this.provider = new DataProvider();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(GenericProperties.TAG, "On Create ScoreUpdateService");
		readInstanceState(getApplicationContext());
		this.handler = new Handler(Looper.getMainLooper());
		// this.toast = new Toast(getApplicationContext());
		this.toast = Toast.makeText(getApplicationContext(), "",
				Toast.LENGTH_LONG);
		this.toast.setGravity(Gravity.CENTER_VERTICAL
				| Gravity.CENTER_HORIZONTAL, 0, 0);
		this.notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		this.notificationIntent = new Intent(this, LiveScore.class);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		if (intent == null) {
			Log.i(GenericProperties.TAG, "Invalid Intent stopping service");
			stopSelf();
		} else if (GenericProperties.INTENT_START_UPDATE.equals(intent
				.getAction())) {
			this.matchId = intent.getIntExtra("matchId", 0);
			Log.i(GenericProperties.TAG, "Start for match id " + matchId);
			if (this.matchId != 0) {
				notifyManager.cancel(NOTIFY_SCORE);
				doBackgroundProcess();
			}

		} else if (GenericProperties.INTENT_UPDATE_SP
				.equals(intent.getAction())) {
			readInstanceState(getApplicationContext());
		}

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(GenericProperties.TAG, "Service Destroyed");
		notifyManager.cancel(NOTIFY_SCORE);
		timer.cancel();
	}

	private void doBackgroundProcess() {
		this.oldScoreUpdate = null;
		this.newScoreUpdate = null;
		this.timer.cancel();
		this.timer = new Timer(true);
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				updateScore();
			}
		};
		timer.scheduleAtFixedRate(task, 500, values.getUpdateInterval());
	}

	private void updateScore() {

		Log.i(GenericProperties.TAG, "Start Background process");
		int version = 0;
		CricScoreDBAdapter dbAdapter = new CricScoreDBAdapter(
				getApplicationContext()).open();

		boolean scoreUpdated = false;
		try {

			SimpleScore simpleScore = dbAdapter.getScore(matchId);
			if (simpleScore != null) {
				version = simpleScore.getVersion();
			}
			Log.i(GenericProperties.TAG, "Start fetching from server");
			SimpleScore score = provider.getLiveScore(matchId, version, getApplicationContext());
			Log.i(GenericProperties.TAG, "End fetching from server");

			if (score.getVersion() == GenericProperties.INT_NO_UPDATE) {
				Log.v(GenericProperties.TAG, "Updating Timestamp");
				dbAdapter.updateScoreTS(score);
				CricScoreApplication.getInstance().setScoreUpdateState(
						ScoreUpdateState.NO_UPDATE);
			} else if (score.getVersion() == GenericProperties.INT_INVALID_MATCH) {
				Log.v(GenericProperties.TAG, "Invalid Match Id");
				this.timer.cancel();
				CricScoreApplication.getInstance().setScoreUpdateState(
						ScoreUpdateState.NO_MATCH);
			} else if (score.getVersion() == GenericProperties.INT_ERROR) {
				Log.v(GenericProperties.TAG, "Error processing the xml");
				this.timer.cancel();
				CricScoreApplication.getInstance().setScoreUpdateState(
						ScoreUpdateState.APPERROR);
			} else if (score.getVersion() == GenericProperties.INT_NO_INTERNET) {
				Log.v(GenericProperties.TAG, "No internet Connection");
				this.timer.cancel();
				CricScoreApplication.getInstance().setScoreUpdateState(
						ScoreUpdateState.NO_INTERNET);
			} else if (score.getVersion() != version) {
				dbAdapter.insertOrUpdateScore(score);
				scoreUpdated = true;
				Log.v(GenericProperties.TAG, "Inserting/Updating data");
				CricScoreApplication.getInstance().setScoreUpdateState(
						ScoreUpdateState.NEW_SCORE);
			}

			if (scoreUpdated) {
				this.oldScoreUpdate = this.newScoreUpdate;
				this.newScoreUpdate = DataConvertor.getScoreUpdate(score);

				Log.v(GenericProperties.TAG,
						"contains " + TextProcessor.containsMatchOver(score));
				Log.v(GenericProperties.TAG, "not current "
						+ this.newScoreUpdate.isNotCurrent());
				Log.v(GenericProperties.TAG,
						"is error " + this.newScoreUpdate.isError());

				if (TextProcessor.containsMatchOver(score)
						|| this.newScoreUpdate.isNotCurrent()
						|| this.newScoreUpdate.isError()) {
					this.timer.cancel();
					Log.i(GenericProperties.TAG, "Future timers cancelled");
				}

				
				final String message = buildMessage();
				if (message != null
						&& this.newScoreUpdate != null
						&& !this.newScoreUpdate.isError()) {
					if (this.values.isToastB() && !CricScoreApplication.getInstance().isLiveOn()) {
						callToast(message);
					}
					if (this.values.isNotifyB()) {
						callNotification(message);
					}
					if(this.values.isSendSMS() && sendSms()){
						sendSms(message);
					}
				}

			}
			sendBroadcast(new Intent(GenericProperties.INTENT_LS_UPDATE));
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			dbAdapter.close();
		}

		Log.i(GenericProperties.TAG, "End Background process");
	}

	private void callNotification(final String message) {
		final String title = this.newScoreUpdate.getTeam1() + " v "
				+ this.newScoreUpdate.getTeam2();
		notificationIntent.removeExtra(GenericProperties.EXTRA_MATCHID);
		notificationIntent.putExtra(GenericProperties.EXTRA_MATCHID, matchId);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		System.out.println(matchId);
		Notification notification = new Notification(R.drawable.icon, message,
				System.currentTimeMillis());
		notification.setLatestEventInfo(getApplicationContext(), message,
				title,
				PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT));
		notification.flags = Notification.FLAG_ONGOING_EVENT;

		if ((this.values.isNotifyWicketFallB() && DataComparator
				.isWicketFalled(oldScoreUpdate, newScoreUpdate))
				|| (this.values.isNotifyEvery5OverB() && DataComparator
						.isDifference5Overs(oldScoreUpdate, newScoreUpdate))) {
			notification.defaults = Notification.DEFAULT_LIGHTS
					| Notification.DEFAULT_SOUND;
		}
		notifyManager.notify(NOTIFY_SCORE, notification);
	}

	private void callToast(final String message) {
		Log.i(GenericProperties.TAG, "show toast");
		if ((this.values.isToastEveryOverB() && DataComparator.isOverChanged(
				oldScoreUpdate, newScoreUpdate))
				|| (this.values.isToastRunsScoredB() && DataComparator
						.isRunsScored(oldScoreUpdate, newScoreUpdate))
				|| (this.values.isToastWicketFallB() && DataComparator
						.isWicketFalled(oldScoreUpdate, newScoreUpdate))
				|| (this.values.isToastEvery5MinB() && System
						.currentTimeMillis() - lastToast > 300000 /* 5 minutes */)) {
			handler.post(new Runnable() {
				public void run() {
					if (message != null) {
						toast.setText(message);
						toast.show();
						lastToast = System.currentTimeMillis();
					}
				}
			});
		}

	}

	private String buildMessage() {
		String message = null;
		if (!this.newScoreUpdate.isError()
				&& !this.newScoreUpdate.isNotCurrent()
				&& "".equals(this.newScoreUpdate.getStatus())) {
			message = this.newScoreUpdate.getPlayingTeam() + " "
					+ this.newScoreUpdate.getPlayingScore() + " "
					+ this.newScoreUpdate.getPlayingOver();
		}
		return message;
	}

	private void readInstanceState(Context c) {
		Log.i(GenericProperties.TAG, "Reading preference values");
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		boolean checkboxNotify = prefs.getBoolean("checkboxNotify", true);
		boolean checkboxToast = prefs.getBoolean("checkboxToast", true);
		String updateInterval = prefs.getString("refreshTime", "20000");
		String smsUpdateMode = prefs.getString("smsUpdate", "2");
		boolean checkboxSMS = prefs.getBoolean("checkboxSMS", false);

		this.values.setUpdateInterval(Integer.parseInt(updateInterval));
		this.values.setNotifyB(checkboxNotify);
		this.values.setToastB(checkboxToast);
		this.values.setSendSMS(checkboxSMS);
		
		if(checkboxSMS){
			this.values.setSmsUpdateMode(Integer.parseInt(smsUpdateMode));
		}

		if(checkboxToast){
			this.values.setToastEvery5MinB(true);
			this.values.setToastEveryOverB(true);
			this.values.setToastRunsScoredB(true);
			this.values.setToastWicketFallB(true);
		}
		
		if(checkboxNotify){
			this.values.setNotifyEvery5OverB(true);
			this.values.setNotifyWicketFallB(true);	
		} else if(notifyManager != null){
			notifyManager.cancel(NOTIFY_SCORE);
		}

		Log.i(GenericProperties.TAG, "Update Interval " + this.values.getUpdateInterval());
	}
	
	private void sendSms(String message){

		Log.i(GenericProperties.TAG, "Sending SMS");
		CricScoreDBAdapter dbAdapter = new CricScoreDBAdapter(
				getApplicationContext()).open();
		List<String> phoneNumbers = dbAdapter.getConnectedNumbers(); 
		dbAdapter.close();
		int counter = 0;
		for(String phoneNumber: phoneNumbers){
			PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this,
					ScoreUpdateService.class), 0);
			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage(phoneNumber, null, message, pi, null);	
			counter++;
		}
		RunningInfo.addSmsCount(counter, getApplicationContext());
	}
	
	private boolean sendSms(){
		switch(this.values.getSmsUpdateMode()){
		case 1:
			return true;
		case 2:
			if(DataComparator.isRunsScored(oldScoreUpdate, newScoreUpdate)){
				return true;
			}
			break;
		case 3:
			if(DataComparator.isOverChanged(oldScoreUpdate, newScoreUpdate)){
				return true;
			}
			break;
		case 4:
			if(DataComparator.isWicketFalled(oldScoreUpdate, newScoreUpdate)){
				return true;
			}
		}
		return false;
	}

}
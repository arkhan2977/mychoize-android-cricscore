package com.mychoize.android.cricscore.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.mychoize.android.cricscore.app.activity.SmsFeature;
import com.mychoize.android.cricscore.app.activity.UserPreference;

public class LiveScore extends Activity {

	private Animation blink;

	private int matchId;
	private Match match;
	private ScoreUpdate scoreUpdate;
	private SimpleScore simpleScore;
	private LiveScoreState state;

	private LiveScoreReceiver receiver;
	private IntentFilter filter;
	
	private long lastUpdate;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		blink = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.blink);
		this.state = LiveScoreState.LOADING;
		this.scoreUpdate = new ScoreUpdate();
		this.receiver = new LiveScoreReceiver();
		this.filter = new IntentFilter(GenericProperties.INTENT_LS_UPDATE);
		Log.i(GenericProperties.TAG, "LiveScore Instance created");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.livescore_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.reload:
			Intent serviceIntent = new Intent(
					GenericProperties.INTENT_START_UPDATE);
			serviceIntent.putExtra("matchId", matchId);
			startService(serviceIntent);
			return true;
		case R.id.share:
			if(simpleScore == null){
				Toast.makeText(getApplicationContext(), "Sharing Failed", Toast.LENGTH_SHORT);
				return false;
			}
			final Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_SUBJECT, simpleScore.getSimple());
			intent.putExtra(Intent.EXTRA_TEXT, simpleScore.getDetail());
			startActivity(Intent.createChooser(intent,
					getString(R.string.share)));
			return true;
		case R.id.internet:
			String url = "http://www.cricinfo.com/ci/engine/match/"+matchId+".html";
			Intent browserIntent = new Intent("android.intent.action.VIEW",
					Uri.parse(url));
			startActivity(browserIntent);
			return true;
		case R.id.options:
			Intent myIntent = new Intent(LiveScore.this, UserPreference.class);
			startActivity(myIntent);
			return true;
		case R.id.sms:
			Intent smsIntent = new Intent(LiveScore.this, SmsFeature.class);
			startActivity(smsIntent);
			return true;
		case R.id.hide:
			moveTaskToBack(true);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(receiver, filter);
		this.state = null;
		this.scoreUpdate = null;
		int id = getIntent().getIntExtra(GenericProperties.EXTRA_MATCHID, 0);
		
		if (matchId != id) {
			matchId = id;
			updateMatch();
		}
		//doMainUpdate();
		updateScreen();
		CricScoreApplication.getInstance().setLiveOn(true);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
		CricScoreApplication.getInstance().setLiveOn(false);
	}

	private void showLoading(final Match match) {
		LiveScore.this.runOnUiThread(new Runnable() {
			public void run() {
				setContentView(R.layout.livescore_loading);
				
				TextView title = (TextView) findViewById(R.id.title);
				if(title!=null){
					title.setText(Html.fromHtml(match.getTeamOne()
							+ " <small><i>vs</i></small> " + match.getTeamTwo()));
					title.setSelected(true);	
				}
				
			}
		});

	}

	private void updateFieldAndBlink(int id, String value) {
		TextView updateField = (TextView) findViewById(id);
		if (updateField != null && !updateField.getText().equals(value)) {
			updateField.startAnimation(blink);
			updateField.setText(value);
		}
	}

	private void updateField(int id, String value) {
		TextView updateField = (TextView) findViewById(id);
		if (updateField != null) {
			updateField.setText(value);
		}
	}

	private void updateScoreFields(ScoreUpdate scoreUpdate) {

		updateField(R.id.team1s1, scoreUpdate.getScoret1s1());
		updateField(R.id.team1s2, scoreUpdate.getScoret1s2());
		updateField(R.id.team2s1, scoreUpdate.getScoret2s1());
		updateField(R.id.team2s2, scoreUpdate.getScoret2s2());

		updateFieldAndBlink(R.id.teamshort, scoreUpdate.getPlayingTeam());
		updateFieldAndBlink(R.id.teamruns, scoreUpdate.getPlayingScore());
		updateFieldAndBlink(R.id.teamovers, scoreUpdate.getPlayingOver());

		updateField(R.id.bat1, scoreUpdate.getBatsman1());
		updateField(R.id.bat1Score, scoreUpdate.getBatsman1score());
		updateField(R.id.bat2, scoreUpdate.getBatsman2());
		updateField(R.id.bat2Score, scoreUpdate.getBatsman2score());
		updateField(R.id.blower, scoreUpdate.getBowler());
		updateField(R.id.blowerEco, scoreUpdate.getBowlerEco());

		updateField(R.id.match_status, scoreUpdate.getStatus());
		updateField(R.id.last_update,
				scoreUpdate.getUpdateTimeString(getApplicationContext()));

	}

	private void showLiveScore(final ScoreUpdate scoreUpdate,
			final boolean loadScreen) {

		LiveScore.this.runOnUiThread(new Runnable() {
			public void run() {
				if (loadScreen) {
					setContentView(R.layout.livescore);

					TextView title = (TextView) findViewById(R.id.title);
					title.setText(Html.fromHtml(scoreUpdate.getTeam1()
							+ " <small><i>vs</i></small> "
							+ scoreUpdate.getTeam2()));
					title.setSelected(true);

					TextView team11 = (TextView) findViewById(R.id.team11);
					team11.setText(scoreUpdate.getTeam1());
					TextView team21 = (TextView) findViewById(R.id.team21);
					team21.setText(scoreUpdate.getTeam2());
				}
				updateScoreFields(scoreUpdate);
			}
		});
	}

	private void updateScreen() {

		updateScore();
		Log.i(GenericProperties.TAG, "Updating the screen");
		if (this.scoreUpdate == null) {
			Log.i(GenericProperties.TAG, "Inside loading");
			if (!LiveScoreState.LOADING.equals(state)) {
				showLoading(this.match);
			}
			state = LiveScoreState.LOADING;
		} else if (this.scoreUpdate.isError()) {
			Log.i(GenericProperties.TAG, "Inside dataerror");
			showRawScore(!LiveScoreState.DATAERROR.equals(state));
			state = LiveScoreState.DATAERROR;
		} else if (this.scoreUpdate.isNotCurrent()) {
			Log.i(GenericProperties.TAG, "Inside not current");
			showMatchInfo(!LiveScoreState.NOTCURRENT.equals(state));
			state = LiveScoreState.NOTCURRENT;
		} else {
			Log.i(GenericProperties.TAG, "Inside livescore");
			showLiveScore(this.scoreUpdate,
					!LiveScoreState.LIVESCORE.equals(state));
			state = LiveScoreState.LIVESCORE;
		}
	}

	private void showMatchInfo(final boolean loadScreen) {

		LiveScore.this.runOnUiThread(new Runnable() {
			public void run() {
				if (loadScreen) {
					setContentView(R.layout.livescore_pause);

					TextView title = (TextView) findViewById(R.id.title);
					title.setText(Html.fromHtml(match.getTeamOne()
							+ " <small><i>vs</i></small> " + match.getTeamTwo()));
					title.setSelected(true);
					updateField(R.id.game_name,
							LiveScore.this.scoreUpdate.getMatchInfo());
					updateField(R.id.place,
							LiveScore.this.scoreUpdate.getPlace());
					updateField(R.id.date, LiveScore.this.scoreUpdate.getDate());
				}

				updateFieldAndBlink(R.id.last_update,
						LiveScore.this.scoreUpdate
								.getUpdateTimeString(getApplicationContext()));
			}
		});

	}

	private void showRawScore(final boolean loadScreen) {
		LiveScore.this.runOnUiThread(new Runnable() {
			public void run() {
				if (loadScreen) {
					setContentView(R.layout.livescore_error);

					TextView title = (TextView) findViewById(R.id.title);
					title.setText(Html.fromHtml(match.getTeamOne()
							+ " <small><i>vs</i></small> " + match.getTeamTwo()));
					title.setSelected(true);
					updateField(R.id.simple,
							LiveScore.this.simpleScore.getSimple());
					updateField(R.id.detail,
							LiveScore.this.simpleScore.getDetail());
				}

				updateFieldAndBlink(R.id.last_update,
						LiveScore.this.scoreUpdate
								.getUpdateTimeString(getApplicationContext()));
			}
		});
	}

	private void updateScore() {
		Log.i(GenericProperties.TAG, "Updating the score");
		long start = System.currentTimeMillis();
		CricScoreDBAdapter adapter = new CricScoreDBAdapter(
				getApplicationContext());
		adapter.open();
		this.simpleScore = adapter.getScore(matchId);
		adapter.close();
		this.scoreUpdate = DataConvertor.getScoreUpdate(this.simpleScore);
		// this.scoreUpdate = DataConvertor.getScore();
		// this.simpleScore = DataConvertor.getSimple();
		long time = System.currentTimeMillis() - start;
		Log.i(GenericProperties.TAG, "Updating the score - Time : " + time
				+ "ms");
	}

	private void updateMatch() {
		CricScoreDBAdapter adapter = new CricScoreDBAdapter(
				getApplicationContext());
		adapter.open();
		this.match = adapter.getMatch(matchId);
		adapter.close();
	}

	

	private class LiveScoreReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			long start = System.currentTimeMillis();	
			if (GenericProperties.INTENT_LS_UPDATE.equals(intent.getAction())) {
				doMainUpdate();
			} 
			long time = System.currentTimeMillis() - start;
			Log.i(GenericProperties.TAG, "On Receive Time Taken : " + time
					+ " ms");
		}

	}
	


	private void displayClose(String message) {
		Resources resources = getResources();			
		String ok = resources.getString(R.string.ok);
		AlertDialog.Builder builder = new AlertDialog.Builder(
				LiveScore.this);
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setPositiveButton(ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						LiveScore.this.finish();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void doMainUpdate(){
		ScoreUpdateState state = CricScoreApplication.getInstance().getScoreUpdateState();
		long newUpdate = CricScoreApplication.getInstance().getScoreUpdateTime();
		if(newUpdate!=0 && newUpdate != lastUpdate){
			lastUpdate = newUpdate;
			if(ScoreUpdateState.NEW_SCORE.equals(state) || ScoreUpdateState.NO_UPDATE.equals(state)){
				updateScreen();
			} else if(ScoreUpdateState.NO_MATCH.equals(state)){
				CricScoreApplication.getInstance().setMatchListState(MatchListState.LOADING);
				startService(new Intent(this, MatchUpdateService.class));
				displayClose(getResources().getString(R.string.invalid_match));
			} else if(ScoreUpdateState.NO_INTERNET.equals(state)){
				displayClose(getResources().getString(R.string.error_msg_nodata));
			} else if(ScoreUpdateState.APPERROR.equals(state)){
				displayClose(getResources().getString(R.string.data_error));
			}	
		}
		
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		CricScoreApplication.getInstance().clearScoreUpdateState();
		this.finish();
	}
	
	


}
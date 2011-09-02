package com.mychoize.android.cricscore.app;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

public class MatchUpdateService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Timer timer = new Timer(true);
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Log.i(GenericProperties.TAG, "Match Update service started");

				long lastUpdate = CricScoreApplication.getInstance().getMatchListUpdateTime();
				Date date = new Date();
				
				if (date.getTime() - lastUpdate > 2160000) { 
					/* 2160000 - 30 minutes */
					Log.i(GenericProperties.TAG,
							"Updating Match List started");
					
					CricScoreDBAdapter dbAdapter = new CricScoreDBAdapter(
							getApplicationContext());
					List<Match> matches = null;
					SharedPreferences p = getApplicationContext()
					.getSharedPreferences(GenericProperties.APP_STATE_PREF,
							MODE_WORLD_READABLE);
					SharedPreferences.Editor e = p.edit();

					try {
						matches = new DataProvider().getMatches(getApplicationContext());
						Log.i(GenericProperties.TAG,
								matches.size()+" ");
					} catch (DataNotFormedException e1) {
						CricScoreApplication.getInstance().setMatchListState(MatchListState.NODATA);
						e1.printStackTrace();
					} catch (VersionNotSupportedException e1) {
						e.putBoolean(GenericProperties.KEY_NOT_SUPPORTED, true);
						e.commit();
						CricScoreApplication.getInstance().setMatchListState(MatchListState.NOTSUPPORTED);
						e1.printStackTrace();
					} catch (NoMatchesRunningException e1) {
						CricScoreApplication.getInstance().setMatchListState(MatchListState.NOMATCH);
						e1.printStackTrace();
					} catch (Exception e1) {
						CricScoreApplication.getInstance().setMatchListState(MatchListState.APPERROR);
						e1.printStackTrace();
					}
					
					if (matches != null) {
						dbAdapter.open();
						dbAdapter.clearScores();
						dbAdapter.clearMatches();
						dbAdapter.insertMatches(matches);
						dbAdapter.close();
						CricScoreApplication.getInstance().setMatchListState(MatchListState.LOADED);
						CricScoreApplication.getInstance().setMatchListUpdateTime(date.getTime());
					}
					Log.i(GenericProperties.TAG,
					"Updating Match List completed");
				} else{
					CricScoreApplication.getInstance().setMatchListState(MatchListState.LOADED);
				}
				sendBroadcast(new Intent(
						GenericProperties.INTENT_ML_UPDATE));
				stopSelf();
				Log.i(GenericProperties.TAG, "Match Update service ended");
			}
		};
		timer.schedule(task, 500);
		return Service.START_STICKY;
	}

	@Override
	public boolean stopService(Intent intent) {
		return true;
	}

}
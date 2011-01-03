package com.mychoize.android.cricscore;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class MatchService extends Service {

	private List<Match> matches;
	private Timer updateTimer;
	private ScoreUpdate scoreUpdate;
	
	private final IMatchService.Stub matchServiceStub = new IMatchService.Stub() {
		@Override
		public List<Match> getMatches() throws RemoteException {
			return matches;
		}

		@Override
		public ScoreUpdate getScore(int id) throws RemoteException {
			return scoreUpdate;
		}
	};

	public MatchService() {
		this.matches = new ArrayList<Match>();
		this.updateTimer = new Timer("MatchesListUpdate");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return matchServiceStub;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		refreshMatches();
		updateTimer.scheduleAtFixedRate(doRefresh, 0, 21600000);
		return Service.START_STICKY;
	}
	private TimerTask doRefresh = new TimerTask() {
		public void run() {
			refreshMatches();
		}
	};

	private void refreshMatches() {
		this.matches = DataProvider.getMatches();
	}
	@Override
	public boolean stopService(Intent intent){
		updateTimer.cancel();
		return true;
	}
}

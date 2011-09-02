package com.mychoize.android.cricscore.app;

import android.app.Application;

public class CricScoreApplication extends Application {
	
	private static CricScoreApplication singleton;
	
	private MatchListState matchListState;
	private long matchListUpdateTime;
	
	private ScoreUpdateState scoreUpdateState;
	private long scoreUpdateTime;
	
	private boolean liveOn;
	
	public static CricScoreApplication getInstance(){
		return singleton;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		singleton = this;
		this.matchListState = MatchListState.LOADING;
		this.matchListUpdateTime = 0;
	}
	
	public MatchListState getMatchListState() {
		return matchListState;
	}

	public void setMatchListState(MatchListState matchListState) {
		this.matchListState = matchListState;
	}

	public long getMatchListUpdateTime() {
		return matchListUpdateTime;
	}

	public void setMatchListUpdateTime(long matchListUpdateTime) {
		this.matchListUpdateTime = matchListUpdateTime;
	}

	public ScoreUpdateState getScoreUpdateState() {
		return scoreUpdateState;
	}

	public void setScoreUpdateState(ScoreUpdateState scoreUpdateState) {
		this.scoreUpdateState = scoreUpdateState;
		this.scoreUpdateTime = System.currentTimeMillis();
	}

	public long getScoreUpdateTime() {
		return scoreUpdateTime;
	}
	
	public void clearScoreUpdateState(){
		this.scoreUpdateState = null;
		this.scoreUpdateTime = 0L;
	}

	public boolean isLiveOn() {
		//System.out.println("getting value "+liveOn);
		return liveOn;
	}

	public void setLiveOn(boolean liveOn) {
		//System.out.println("setting value " +liveOn);
		this.liveOn = liveOn;
	}
	
	

}

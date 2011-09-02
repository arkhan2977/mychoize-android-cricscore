package com.mychoize.android.cricscore.app;

import android.util.Log;

public class DataComparator {
	
	public static boolean isOverChanged(ScoreUpdate oldScore, ScoreUpdate newScore){
		if(oldScore == null || newScore == null){
			return true;
		}
		if(getOver(oldScore.getPlayingOver()).equals(getOver(newScore.getPlayingOver()))){
			return false; 
		}
		return true;
	}
	
	public static boolean isWicketFalled(ScoreUpdate oldScore, ScoreUpdate newScore){
		if(oldScore == null || newScore == null){
			return true;
		}
		else if(getWicket(oldScore.getPlayingScore()).equals(getWicket(newScore.getPlayingScore()))){
			return false;	
		}
		return true;
	}
	
	public static boolean isRunsScored(ScoreUpdate oldScore, ScoreUpdate newScore){
		if(oldScore == null || newScore == null){
			return true;
		}
		else if(getRun(oldScore.getPlayingScore()).equals(getRun(newScore.getPlayingScore()))){
			return false;
		}
		return true;
	}

	private static String getRun(String score){
		Log.i(GenericProperties.TAG, score);
		if(score == null){
			return "";
		}else if(score.indexOf("/") == -1){
			return score;
		}
		String str = score.substring(0, score.indexOf("/"));
		Log.i(GenericProperties.TAG, str);
		return str;
	}
	
	public static boolean isDifference5Overs(ScoreUpdate oldScoreUpdate, ScoreUpdate newScoreUpdate){
		if(oldScoreUpdate == null || newScoreUpdate == null){
			return false;
		}
		try{
			int old =Integer.parseInt(getOver(oldScoreUpdate.getPlayingOver()));
			int now =Integer.parseInt(getOver(newScoreUpdate.getPlayingOver()));
			Log.v(GenericProperties.TAG, "Old over:"+ old);
			Log.v(GenericProperties.TAG, "New over:"+ now);
			if(now > old && now - old >= 5 ||  now < old){
				return true;
			}
		}
		catch(Exception e){
		}
		return false;
		
	}
	
	private static String getOver(String score){
		Log.i(GenericProperties.TAG, score);
		if(score == null){
			return "";
		}else if(score.indexOf(".") == -1){
			return score;
		}
		String str = score.substring(0, score.indexOf("."));
		Log.i(GenericProperties.TAG, str);
		return str;
	}
	
	private static String getWicket(String score){
		Log.i(GenericProperties.TAG, score);
		if(score == null){
			return "";
		}else if(score.indexOf("/") == -1){
			return score;
		}
		String str = score.substring(score.indexOf("/")+1,score.length());
		Log.i(GenericProperties.TAG, str);
		return str;
	}
	
}
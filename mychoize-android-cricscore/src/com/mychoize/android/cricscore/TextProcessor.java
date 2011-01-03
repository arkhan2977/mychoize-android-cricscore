package com.mychoize.android.cricscore;

import java.util.ArrayList;
import java.util.List;

public class TextProcessor {
	public static void processSimpleTxt(String simple, ScoreUpdate scoreUpdate){
		
		byte innsPlay = MatchStatus.NO_INFO;
		String teams[] = simple.split("v");
		String team1 = teams[0].trim();
		String team2 = teams[1].trim();
		String team1Name = team1.replaceAll("[^a-zA-Z ]", "");
		String team2Name = team2.replaceAll("[^a-zA-Z ]", "");
		String team1Score = team1.replaceAll("[a-zA-Z ]", "");
		String team2Score = team2.replaceAll("[a-zA-Z ]", "");
		
		String t1s1 = "";
		String t1s2 = "";
		String t2s1 = "";
		String t2s2 = "";
		
		if(!"".equals(team1Score)){
			String team1Scores[] = team1Score.split("&");
			t1s1 = team1Scores[0];
			if(t1s1.contains("*")){
				innsPlay =  MatchStatus.TEAM1_INN1;
			}
			if(team1Scores.length>1){
				t1s2 = team1Scores[1];
				if(t1s2.contains("*")){
					innsPlay =  MatchStatus.TEAM1_INN2;
				}
			}
		}
		

		if(!"".equals(team2Score)){
			String team2Scores[] = team2Score.split("&");
			t2s1 = team2Scores[0];
			if(t2s1.contains("*")){
				innsPlay =  MatchStatus.TEAM2_INN1;
			}
			if(team2Scores.length>1){
				t2s2 = team2Scores[1];
				if(t2s2.contains("*")){
					innsPlay =  MatchStatus.TEAM2_INN2;
				}
			}
		}
		scoreUpdate.setScores(team1Name,team2Name,t1s1,t1s2,t2s1,t2s2,innsPlay);
		
	}
	
	public static void processDetailTxt(String detail, ScoreUpdate score){
		//System.out.println(detail);
		String innerTxt = "";
		String playingScore = "";
		String playingTeam = "";
		String playingOver = "";
		String matchStatus = "";
		List<String> scoreList = new ArrayList<String>();

		int statusEnd = detail.length();
		int statusStart = detail.indexOf("-");
		if(statusStart>0){
			matchStatus = detail.substring(statusStart+2, statusEnd);
		}
		
		int openBracket = detail.indexOf("(");
		int closeBracket = detail.indexOf(")");
		if(openBracket > 0 && closeBracket > 0){
			innerTxt = detail.substring(openBracket+1, closeBracket);
			String innerArray[] = innerTxt.split(",");
			playingOver = innerArray[0];
			for(int i=1;i<innerArray.length;i++){
				innerArray[i] = innerArray[i].trim().replaceAll("[*]","");
				int split = innerArray[i].lastIndexOf(" ");
				scoreList.add(innerArray[i].substring(0, split));
				scoreList.add(innerArray[i].substring(split+1, innerArray[i].length()));
				
			}
			
			
		}
		if(openBracket>0){
			int scoreStart = detail.lastIndexOf(" ", openBracket-2);
			playingScore = detail.substring(scoreStart+1, openBracket-1);
			playingTeam = detail.substring(0,scoreStart);
		}
		
		if(detail.contains(":")){
			//TODO - Process future match info
		}
		
		score.setCurrentScore(playingTeam, playingScore, playingOver);
		score.setStatus(matchStatus);
		score.setCurrentInfo(scoreList);
	}

}

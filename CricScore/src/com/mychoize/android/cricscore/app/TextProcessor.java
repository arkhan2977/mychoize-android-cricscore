package com.mychoize.android.cricscore.app;

import java.util.ArrayList;
import java.util.List;

public class TextProcessor {
	public static void processSimpleTxt(String simple, ScoreUpdate scoreUpdate) {
		if (scoreUpdate == null) {
			return;
		}
		if (simple == null || simple.length()<15) {
			scoreUpdate.setError(true);
			return;
		}

		byte innsPlay = MatchStatus.NO_INFO;
		String teams[] = simple.split(" v ");
		String team1 = teams[0].trim();
		String team2 = teams[1].trim();
		String team1Name = team1.replaceAll("[^a-zA-Z- ]", "").trim();
		String team2Name = team2.replaceAll("[^a-zA-Z- ]", "").trim();
		String team1Score = team1.replaceAll("[a-zA-Z-* ]", "");
		String team2Score = team2.replaceAll("[a-zA-Z-* ]", "");

		String t1s1 = "";
		String t1s2 = "";
		String t2s1 = "";
		String t2s2 = "";

		if (team1.contains("*")) {
			innsPlay = MatchStatus.TEAM1;
		} else if (team2.contains("*")) {
			innsPlay = MatchStatus.TEAM2;
		}

		if (!"".equals(team1Score)) {
			String team1Scores[] = team1Score.split("&");
			t1s1 = team1Scores[0];
			if (team1Scores.length > 1) {
				t1s2 = team1Scores[1];
			}
		}

		if (!"".equals(team2Score)) {
			String team2Scores[] = team2Score.split("&");
			t2s1 = team2Scores[0];

			if (team2Scores.length > 1) {
				t2s2 = team2Scores[1];
			}
		}
		scoreUpdate.setScores(team1Name, team2Name, t1s1, t1s2, t2s1, t2s2,
				innsPlay);

	}

	public static void processDetailTxt(String detail, ScoreUpdate score) {
		if (score == null || score.isError()) {
			return;
		}
		if (detail == null || detail.length() < 25) {
			score.setError(true);
			return;
		}

		String innerTxt = "";
		String playingScore = "";
		String playingTeam = "";
		String playingOver = "";
		String matchStatus = "";
		List<String> scoreList = new ArrayList<String>();

		int statusEnd = detail.length();
		
		int openBracket = detail.indexOf("(");
		int closeBracket = detail.indexOf(")");
		if (openBracket > 0 && closeBracket > 0) {
			innerTxt = detail.substring(openBracket + 1, closeBracket);
			String innerArray[] = innerTxt.split(",");
			playingOver = innerArray[0];
			for (int i = 1; i < innerArray.length; i++) {
				innerArray[i] = innerArray[i].trim().replaceAll("[*]", "");
				int split = innerArray[i].lastIndexOf(" ");
				scoreList.add(innerArray[i].substring(0, split));
				scoreList.add(innerArray[i].substring(split + 1,
						innerArray[i].length()));

			}

		}
		if (openBracket > 0) {
			int scoreStart = detail.lastIndexOf(" ", openBracket - 2);
			playingScore = detail.substring(scoreStart + 1, openBracket - 1);
			playingTeam = detail.substring(0, scoreStart);
			
			if(playingTeam != null && playingTeam.length()>8){
				String[] arr = playingTeam.split(" " );
				StringBuilder builder = new StringBuilder();
				if(arr.length >= 2){
					for(int i=0;i<arr.length;i++){
						builder.append(arr[i].charAt(0));	
					}
				playingTeam = builder.toString(); 	
				}else{
					playingTeam = playingTeam.substring(0, 6) + "..";
				}
			}
		}
		
		int statusStart = detail.indexOf("-",closeBracket);
		if (statusStart > 0) {
			matchStatus = detail.substring(statusStart + 2, statusEnd);
		}
		

		if (detail.contains(" at ") || detail.contains(":")) {
			String val = detail.substring(detail.indexOf(" at ") + 4,
					detail.length());

			String place = "";
			String date = "";
			String matchInfo = "";

			int commaIndex = val.indexOf(",");
			int colenIndex = detail.indexOf(":");
			if (commaIndex != -1) {
				place = val.substring(0, commaIndex);
				date = val.substring(commaIndex + 2, val.length());
			}
			if (colenIndex != -1) {
				matchInfo = detail.substring(0, colenIndex);
			}
			score.setFuturePlayInfo(place, matchInfo, date);
		}

		score.setCurrentScore(playingTeam, playingScore, playingOver);
		score.setStatus(matchStatus);
		score.setCurrentInfo(scoreList);
	}

	public static boolean containsMatchOver(SimpleScore score) {
		if (score != null && score.getDetail() != null) {
			String scoreUpperCase = score.getDetail().toUpperCase();
			if (scoreUpperCase.contains("MATCH")
					&& scoreUpperCase.contains("OVER")
					&& scoreUpperCase.indexOf("MATCH") < scoreUpperCase
							.indexOf("OVER")) {
				return true;
			}
		}
		return false;
	}

}

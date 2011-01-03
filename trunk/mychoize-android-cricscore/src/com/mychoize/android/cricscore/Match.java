package com.mychoize.android.cricscore;

import android.os.Parcel;
import android.os.Parcelable;

public class Match implements Parcelable{
	public static final Creator<Match> CREATOR = new Parcelable.Creator<Match>() {
        public Match createFromParcel(Parcel in) {
            return new Match(in);
        }

        public Match[] newArray(int size) {
            return new Match[size];
        }
    };
	private String teamOne;
	private String teamTwo;
	private int matchId;
	
	
	public Match(){
		
	}
	
	
	public Match(String teamOne, String teamTwo, int matchId) {
		this.teamOne = teamOne;
		this.teamTwo = teamTwo;
		this.matchId = matchId;
	}
	public Match(Parcel in) {
		teamOne = in.readString();
		teamTwo = in.readString();
		matchId = in.readInt();
	}
	public String getTeamOne() {
		return teamOne;
	}
	public void setTeamOne(String teamOne) {
		this.teamOne = teamOne;
	}
	public String getTeamTwo() {
		return teamTwo;
	}
	public void setTeamTwo(String teamTwo) {
		this.teamTwo = teamTwo;
	}
	public int getMatchId() {
		return matchId;
	}
	public void setMatchId(int matchId) {
		this.matchId = matchId;
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel out, int arg1) {
		out.writeString(teamOne);
		out.writeString(teamTwo);
		out.writeInt(matchId);
		
	}

}

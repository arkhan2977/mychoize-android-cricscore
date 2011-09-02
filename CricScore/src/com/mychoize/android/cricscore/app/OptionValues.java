package com.mychoize.android.cricscore.app;

public class OptionValues {

    private int updateInterval;
    private boolean notifyB;
    private boolean notifyEvery5OverB;
    private boolean notifyWicketFallB;
    private boolean toastB;
    private boolean toastEvery5MinB;
    private boolean toastEveryOverB;
    private boolean toastRunsScoredB;
    private boolean toastWicketFallB;
    private boolean sendSMS;
    private int smsUpdateMode;
    
	public boolean isSendSMS() {
		return sendSMS;
	}
	public void setSendSMS(boolean sendSMS) {
		this.sendSMS = sendSMS;
	}
	public int getSmsUpdateMode() {
		return smsUpdateMode;
	}
	public void setSmsUpdateMode(int smsUpdateMode) {
		this.smsUpdateMode = smsUpdateMode;
	}
	public int getUpdateInterval() {
		return updateInterval;
	}
	public void setUpdateInterval(int updateInterval) {
		this.updateInterval = updateInterval;
	}
	public boolean isNotifyB() {
		return notifyB;
	}
	public void setNotifyB(boolean notifyB) {
		this.notifyB = notifyB;
	}
	public boolean isNotifyEvery5OverB() {
		return notifyEvery5OverB;
	}
	public void setNotifyEvery5OverB(boolean notifyEvery5OverB) {
		this.notifyEvery5OverB = notifyEvery5OverB;
	}
	public boolean isNotifyWicketFallB() {
		return notifyWicketFallB;
	}
	public void setNotifyWicketFallB(boolean notifyWicketFallB) {
		this.notifyWicketFallB = notifyWicketFallB;
	}
	public boolean isToastB() {
		return toastB;
	}
	public void setToastB(boolean toastB) {
		this.toastB = toastB;
	}
	public boolean isToastEvery5MinB() {
		return toastEvery5MinB;
	}
	public void setToastEvery5MinB(boolean toastEvery5MinB) {
		this.toastEvery5MinB = toastEvery5MinB;
	}
	public boolean isToastEveryOverB() {
		return toastEveryOverB;
	}
	public void setToastEveryOverB(boolean toastEveryOverB) {
		this.toastEveryOverB = toastEveryOverB;
	}
	public boolean isToastRunsScoredB() {
		return toastRunsScoredB;
	}
	public void setToastRunsScoredB(boolean toastRunsScoredB) {
		this.toastRunsScoredB = toastRunsScoredB;
	}
	public boolean isToastWicketFallB() {
		return toastWicketFallB;
	}
	public void setToastWicketFallB(boolean toastWicketFallB) {
		this.toastWicketFallB = toastWicketFallB;
	}
}
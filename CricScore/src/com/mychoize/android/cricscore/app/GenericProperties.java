package com.mychoize.android.cricscore.app;

public class GenericProperties {
	
	public static final String TAG = "CricScore";
	
	private static final String packName = "com.mychoize.android.cricscore.app.";
	
	//public static final String INTENT_UPDATE_LS = packName + "UPDATE_LIVESCORE";
	//public static final String INTENT_CLOSE_LS_ERROR = packName + "CLOSE_LIVESCORE_ERROR";
	//public static final String INTENT_CLOSE_LS_INVALID = packName + "CLOSE_LIVESCORE_INVALID";
	//public static final String INTENT_CLOSE_LS_NONET = packName + "CLOSE_LIVESCORE_NONET";
	
	public static final String INTENT_START_UPDATE = packName + "START_UPDATE";
	public static final String INTENT_UPDATE_SP = packName + "UPDATE_VALUES";
	public static final String INTENT_LS_UPDATE = packName + "LS_UPDATE";
	public static final String INTENT_ML_UPDATE = packName + "ML_UPDATE";
	
	public static final String EXTRA_MATCHID = packName + "matchId";
	
	public static final String BASE_URL = "http://cricscore.mychoize.com/csa";
	
	public static int INT_INVALID_MATCH = -1;
	public static int INT_NO_UPDATE = -2;
	public static int INT_ERROR = -3;
	public static int INT_NO_INTERNET = -4;
	
	public static String APP_STATE_PREF = "ApplicationState";
	public static String KEY_NOT_SUPPORTED = "NotSupported";
	public static boolean VERSION_NOT_SUPPORTED = false;
	
}
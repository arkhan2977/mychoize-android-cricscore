package com.mychoize.android.cricscore.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class RunningInfo {

	private static final String GLOBAL_REQUEST = "gRequest";
	private static final String LOCAL_REQUEST = "lRequest";
	private static final String GLOBAL_USAGE = "gUsage";
	private static final String LOCAL_USAGE = "lUsage";
	private static final String GLOBAL_SMS = "gSms";
	private static final String LOCAL_SMS = "lSms";
	
		
	public static void addRequest(int size, Context c){
		SharedPreferences p =
            c.getSharedPreferences(GenericProperties.APP_STATE_PREF, Activity.MODE_WORLD_READABLE);
        SharedPreferences.Editor e = p.edit();
        e.putLong(GLOBAL_REQUEST, p.getLong(GLOBAL_REQUEST, 0) + 1);
        e.putLong(LOCAL_REQUEST, p.getLong(LOCAL_REQUEST, 0) + 1);
        e.putLong(GLOBAL_USAGE, p.getLong(GLOBAL_USAGE, 0 )+ size);
        e.putLong(LOCAL_USAGE, p.getLong(LOCAL_USAGE, 0) + size);
        e.commit(); 
	}
	
	public static void addSmsCount(int size, Context c){
		SharedPreferences p =
            c.getSharedPreferences(GenericProperties.APP_STATE_PREF, Activity.MODE_WORLD_READABLE);
        SharedPreferences.Editor e = p.edit();
        e.putLong(GLOBAL_SMS, p.getLong(GLOBAL_SMS, 0 )+ size);
        e.putLong(LOCAL_SMS, p.getLong(LOCAL_SMS, 0) + size);
        e.commit();
        
	}
	
	public static void clearSession(Context c){
		SharedPreferences p =
            c.getSharedPreferences(GenericProperties.APP_STATE_PREF, Activity.MODE_WORLD_READABLE);
        SharedPreferences.Editor e = p.edit();
        e.putLong(LOCAL_REQUEST, 0);
        e.putLong(LOCAL_USAGE, 0);
        e.putLong(LOCAL_SMS, 0);
        e.commit();
	}
	
	public static List<String> getRequestDetails(Context c){
		SharedPreferences p =
            c.getSharedPreferences(GenericProperties.APP_STATE_PREF, Activity.MODE_WORLD_READABLE);
		long globalRequest = p.getLong(GLOBAL_REQUEST, 0);
		long globalUsage = p.getLong(GLOBAL_USAGE, 0);
		long globalSMS = p.getLong(GLOBAL_SMS, 0);
		
		long localRequest = p.getLong(LOCAL_REQUEST, 0);
		long localUsage = p.getLong(LOCAL_USAGE, 0);
		long localSMS = p.getLong(LOCAL_SMS, 0);
		
		List<String> list = new ArrayList<String>();
		list.add(String.valueOf(globalRequest));
		list.add(String.valueOf(getInBytes(globalUsage)));
		list.add(String.valueOf(localRequest));
		list.add(String.valueOf(getInBytes(localUsage)));
		list.add(String.valueOf(localSMS));
		list.add(String.valueOf(globalSMS));
		
        return list;
	}
	
	private static String getInBytes(long count){
		if(count<1024){
			return count + " B";
		}
		else if(count < 1048576){
			return count/1024 + " kB"; 
		}
		else if (count < 1073741824){
			return count/1048576 + " MB"; 
		}
		else{
			return count/1073741824 + " GB";
		}
	}

}

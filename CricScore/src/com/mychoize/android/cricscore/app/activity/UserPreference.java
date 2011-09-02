package com.mychoize.android.cricscore.app.activity;

import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.mychoize.android.cricscore.app.GenericProperties;
import com.mychoize.android.cricscore.app.R;
import com.mychoize.android.cricscore.app.RunningInfo;

public class UserPreference extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.user_preference);
		CheckBoxPreference smsPreference = (CheckBoxPreference) findPreference("checkboxSMS");
		smsPreference.setOnPreferenceClickListener(new OnPreferenceClickListener(){
			public boolean onPreferenceClick(Preference arg0) {
				CheckBoxPreference preference = (CheckBoxPreference) arg0;
				ListPreference listPreference = (ListPreference) findPreference("smsUpdate");
				listPreference.setEnabled(preference.isChecked());
				if(preference.isChecked()){
					Toast.makeText(getApplicationContext(), "Your friends will updated via SMS from your phone", Toast.LENGTH_LONG).show();	
				}
				return true;
			}
		});
		
		Preference twitterPreference = (Preference) findPreference("app_twitter_id");
		twitterPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				Intent browserIntent = new Intent("android.intent.action.VIEW",
						Uri.parse(getResources().getString(R.string.info_about_twitter_link)));
				startActivity(browserIntent);
				return true;
			}
		});
		
		
	}

	@Override
	protected void onStart() {
		super.onStart();
		List<String> arg = RunningInfo.getRequestDetails(getApplicationContext());
		
		Preference internetUsage1 = (Preference) findPreference("internetUsage");
		internetUsage1.setSummary(arg.get(3) + " for " + arg.get(2)+ " requests" );
		
		Preference internetUsage2 = (Preference) findPreference("internetUsageTotal");
		internetUsage2.setSummary(arg.get(1) + " for " + arg.get(0)+ " requests" );
		
		CheckBoxPreference smsPreference = (CheckBoxPreference) findPreference("checkboxSMS");
		ListPreference listPreference = (ListPreference) findPreference("smsUpdate");
		listPreference.setEnabled(smsPreference.isChecked());
		
		Preference smsLocalUsage = (Preference) findPreference("smsSent");
		smsLocalUsage.setSummary(arg.get(4));
		
		Preference smsGlobalUsage = (Preference) findPreference("totalSmsSent");
		smsGlobalUsage.setSummary(arg.get(5));
		
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		startService(new Intent(GenericProperties.INTENT_UPDATE_SP));
	}	
	
}

package com.mychoize.android.cricscore;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class LiveScore extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.livescore_pause);

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.livescore_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.matches:
			//showMatches();
			return true;
		case R.id.options:
			options();
			return true;
		case R.id.help:
			showHelp();
			return true;
		case R.id.info:
			showInfo();
			return true;
		case R.id.hide:
			moveTaskToBack(true);
			return true;
		case R.id.exit:
			alertAndClose();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void showHelp(){
		Resources resources = getResources();
		String url = resources.getString(R.string.help_link);
		Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
		startActivity(browserIntent);
	}
	
	private void showInfo() {
		Intent myIntent = new Intent();
		myIntent.setClassName("com.mychoize.android.cricscore", "com.mychoize.android.cricscore.Info");
		startActivity(myIntent);
	}
	
	private void options() {
		Intent myIntent = new Intent();
		myIntent.setClassName("com.mychoize.android.cricscore", "com.mychoize.android.cricscore.Options");
		startActivity(myIntent);
	}
		
	private void alertAndClose() {
		setResult(RESULT_OK);
		finish();
		getParent().finish();
        
	}


}

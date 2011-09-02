package com.mychoize.android.cricscore.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Launching extends Activity {

	private List<Match> matches = new ArrayList<Match>();
	private MatchesReceiver receiver;
	private IntentFilter filter;
	private MatchListState oldState;
	private MatchAdapter matchAdapter;
	private long lastUpdate;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CricScoreApplication.getInstance().setMatchListState(MatchListState.LOADING);
		startService(new Intent(this, MatchUpdateService.class));
		this.receiver = new MatchesReceiver();
		this.filter = new IntentFilter(GenericProperties.INTENT_ML_UPDATE);
		this.matchAdapter = new MatchAdapter(this);
		this.oldState = MatchListState.LOADING;
		RunningInfo.clearSession(getApplicationContext());
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		updateScreen();
		registerReceiver(receiver, filter);
	}

	private void updateScreen() {
		Log.i(GenericProperties.TAG, "updating screen");

		MatchListState state = CricScoreApplication.getInstance()
				.getMatchListState();
		
		Resources resources = getResources(); 

		if (MatchListState.LOADING.equals(state)) {
			setContentView(R.layout.launching_loading);
			Log.i(GenericProperties.TAG, "updating screen - loading");
		} else if (MatchListState.LOADED.equals(state)) { 
			showMatches();
			Log.i(GenericProperties.TAG, "updating screen - show matches");
		} else if (MatchListState.NODATA.equals(state)) {
			showErrorScreen(resources.getString(R.string.error_title_nodata), resources.getString(R.string.error_msg_nodata));
			Log.i(GenericProperties.TAG, "updating screen - show error - nodata");
		} else if (MatchListState.NOMATCH.equals(state)) {
			showErrorScreen(resources.getString(R.string.error_title_nomatch), resources.getString(R.string.error_msg_nomatch));
			Log.i(GenericProperties.TAG, "updating screen - show error - nomatch");
		} else if (MatchListState.NOTSUPPORTED.equals(state)) {
			showErrorScreen(resources.getString(R.string.error_title_nosupport), resources.getString(R.string.error_msg_nosupport));
			Log.i(GenericProperties.TAG, "updating screen - show error - nosupport");
		} else if (MatchListState.APPERROR.equals(state)){
			showErrorScreen(resources.getString(R.string.error_title_apperror), resources.getString(R.string.error_msg_apperror));
			Log.i(GenericProperties.TAG, "updating screen - show error - apperror");
		}
		oldState = state;
		Log.i(GenericProperties.TAG, "updating screen ended");

	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	@Override
	public void onStop() {
		super.onStop();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopService(new Intent(this, ScoreUpdateService.class));
	}

	private class MatchesReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			long start = System.currentTimeMillis();
			if(GenericProperties.INTENT_ML_UPDATE.equals(intent.getAction())){
				updateScreen();
			}
			long time = System.currentTimeMillis() - start;
			Log.i(GenericProperties.TAG, "On Receive Time Taken : " + time
					+ " ms");
		}
	}

	private void showErrorScreen(String title, String message) {
		if(MatchListState.LOADING.equals(oldState) || MatchListState.LOADED.equals(oldState)){
			setContentView(R.layout.launching_error);
		}
		Log.v(GenericProperties.TAG, title + " "+ message);
		TextView tilteTextView = (TextView)findViewById(R.id.title);
		tilteTextView.setText(title);
		TextView messageTextView = (TextView)findViewById(R.id.message_body);
		messageTextView.setText(message);
		
		Log.v(GenericProperties.TAG, " "+ messageTextView.getText());
		Log.v(GenericProperties.TAG, " "+ messageTextView.getTextSize());
		
	}

	private void showMatches() {
		
		if(!oldState.equals(MatchListState.LOADED)){
			setContentView(R.layout.match_list);
			final ListView l1 = (ListView) findViewById(R.id.ListMatches);
			//l1.set
			l1.setAdapter(matchAdapter);
			l1.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View view,
						int position, long id) {
					int mid = ((Match) l1.getItemAtPosition(position)).getMatchId();
					openLiveScore(mid);
					openScoreUpdateService(mid);
					
				}
			});	
		}
		
		long newUpdateTime = CricScoreApplication.getInstance().getMatchListUpdateTime();
		if(newUpdateTime != this.lastUpdate){
			CricScoreDBAdapter adapter = new CricScoreDBAdapter(
					getApplicationContext());
			adapter.open();
			this.matches =  adapter.getMatches(); 
			adapter.close();
			this.matchAdapter.notifyDataSetChanged();
			if(this.lastUpdate!=0){
				Toast.makeText(getApplicationContext(), R.string.toast_update, Toast.LENGTH_SHORT).show();	
			}
			
			this.lastUpdate = newUpdateTime; 
			
		}
		
	}
	
	private void openScoreUpdateService(int mid) {
		Intent serviceIntent = new Intent(
				GenericProperties.INTENT_START_UPDATE);
		serviceIntent.putExtra("matchId", mid);
		startService(serviceIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.launching_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.hide:
			moveTaskToBack(false);
			return true;
		case R.id.exit:
			//alertAndClose();
			Launching.this.finish();
			return true;
		case R.id.feedback:
			Launching.this.feedback();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void feedback() {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("text/plain");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"spriyan@mychoize.com"});
		i.putExtra(Intent.EXTRA_SUBJECT, "MyChoize CricScore: Feedback");
		i.putExtra(Intent.EXTRA_TEXT   , "Please provide your feedback for improvement of CricScore app here.");
		i.setType("message/rfc822");
		startActivity(Intent.createChooser(i, "Send Feedback"));
	}

	private void openLiveScore(int id) {
		Intent myIntent = new Intent(this, LiveScore.class);
		myIntent.putExtra(GenericProperties.EXTRA_MATCHID, id);
		startActivity(myIntent);
	}

	public void alertAndClose() {
		Resources resources = getResources();
		String str = resources.getString(R.string.confirm_exit);
		String exit = resources.getString(R.string.exit);
		String cancel = resources.getString(R.string.cancel);
		String hide = resources.getString(R.string.hide);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(str);
		builder.setCancelable(true);
		builder.setNeutralButton(cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		builder.setPositiveButton(exit, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Launching.this.finish();
			}
		});
		builder.setNegativeButton(hide, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				moveTaskToBack(false);
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public void onBackPressed() {
		alertAndClose();
		return;
	}

	private class MatchAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MatchAdapter(Context context) {
			mInflater = LayoutInflater.from(context);

		}

		public int getCount() {
			return Launching.this.matches.size();
		}

		public Object getItem(int position) {
			return matches.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.matchview, null);
				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(R.id.TeamOne);
				holder.text2 = (TextView) convertView
						.findViewById(R.id.TeamTwo);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			try {
				Match match = matches.get(position);
				holder.text.setText(match.getTeamOne());
				holder.text2.setText(match.getTeamTwo());
			} catch (ArrayIndexOutOfBoundsException e) {

			}

			return convertView;
		}

		class ViewHolder {
			TextView text;
			TextView text2;
		}
	}
}
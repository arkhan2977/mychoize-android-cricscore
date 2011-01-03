package com.mychoize.android.cricscore;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Launching extends Activity {

	// static named integer references
	private static final int LIVE_SCORE = 0;
	private static final List<Match> MATCHES = new ArrayList<Match>();
	
	private ProgressDialog dialog;
	private IMatchService serviceBinder;
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName className) {
			Toast.makeText(getApplicationContext(),
					R.string.local_service_disconnected, Toast.LENGTH_LONG).show();
			setServiceBinder(null);
		}
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			setServiceBinder(IMatchService.Stub.asInterface(service));
			Toast.makeText(getApplicationContext(),
					R.string.local_service_connected, Toast.LENGTH_LONG).show();
			try {
				MATCHES.addAll(serviceBinder.getMatches());
			} catch (RemoteException e) {
				e.printStackTrace();
			}			
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startService(new Intent(this, MatchService.class));
		// openLiveScore();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		updateMatchList();
		if(MATCHES.size() == 0){
			dialog = ProgressDialog.show(Launching.this, "", 
	                "Loading. Please wait..", true, true);
			dialog.show();
			Thread.currentThread().suspend();
		}
		
		setContentView(R.layout.match_list);
		ListView l1 = (ListView) findViewById(R.id.ListMatches);
		l1.setAdapter(new MatchAdapter(Launching.this));
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopService(new Intent(this,MatchService.class));
	}
	
	
	@Override
	public void onNewIntent(Intent intent){
		//intent.get
		Toast.makeText(getApplicationContext(), intent.getAction(), Toast.LENGTH_SHORT);
		
	}
	
	
	private void updateMatchList(){
		final Timer timer = new Timer("matchListTime");
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				bindService(new Intent(Launching.this, MatchService.class),mConnection, BIND_AUTO_CREATE);
				unbindService(mConnection);
				if(MATCHES.size()>0){
					Launching.this.dialog.cancel();
					timer.cancel();
				}
			}
		}; 
		timer.scheduleAtFixedRate(task, 0, 1000);
	}

//	public void showLoading() {
//		setContentView(R.layout.launching_loading);
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.launching_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.info:
			showInfo();
			return true;
		case R.id.hide:
			// options();
			moveTaskToBack(false);
			return true;
		case R.id.exit:
			alertAndClose();
			return true;
		default:
			
			return super.onOptionsItemSelected(item);
		}
	}

	private void showInfo() {
		Intent myIntent = new Intent();
		myIntent.setClassName("com.mychoize.android.cricscore",
				"com.mychoize.android.cricscore.Info");
		startActivity(myIntent);
	}

	private void openLiveScore() {
		Intent myIntent = new Intent();
		myIntent.setClassName("com.mychoize.android.cricscore",
				"com.mychoize.android.cricscore.LiveScore");
		startActivityForResult(myIntent, LIVE_SCORE);
	}

	protected void onActivityResult(int requestCode, int resultCode) {

		switch (resultCode) {
		case LIVE_SCORE:
			if (resultCode == RESULT_OK) {
				alertAndClose();
			}
			break;
		default:
			break;
		}
	}

	public void alertAndClose() {
		// TODO - To customize the alert Message
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

	public void setServiceBinder(IMatchService iMatchService) {
		this.serviceBinder = iMatchService;
	}

	public IMatchService getServiceBinder() {
		return serviceBinder;
	}

	private static class MatchAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MatchAdapter(Context context) {
			mInflater = LayoutInflater.from(context);

		}

		public int getCount() {
			return MATCHES.size();
		}

		public Object getItem(int position) {
			return position;
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
			Match match = MATCHES.get(position);
			holder.text.setText(match.getTeamOne());
			holder.text2.setText(match.getTeamTwo());

			return convertView;
		}

		static class ViewHolder {
			TextView text;
			TextView text2;
		}
	}

}

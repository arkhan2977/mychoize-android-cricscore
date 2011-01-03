package com.mychoize.android.cricscore;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class Options extends Activity {

    protected int mPos;
    protected boolean notifyB;
    protected boolean notifyEvery5OverB;
    protected boolean notifyWicketFallB;
    protected boolean toastB;
    protected boolean toastEvery5MinB;
    protected boolean toastEveryOverB;
    protected boolean toastRunsScoredB;
    protected boolean toastWicketFallB;

    public static final String PREFERENCES_FILE = "ApplicationPrefs";
    
    public static final int DEFAULT_UPDATE_FREQ = 1;
    public static final boolean DEFAULT_NOTIFY = true;
    public static final boolean DEFAULT_NOTIFY_EVERY5_OVER = true;
    public static final boolean DEFAULT_NOTIFY_WICKET_FALL = true;
    public static final boolean DEFAULT_TOAST = true;
    public static final boolean DEFAULT_TOAST_EVERY5_MIN = true;
    public static final boolean DEFAULT_TOAST_EVERY_OVER = true;
    public static final boolean DEFAULT_TOAST_RUNS_SCORED = true;
    public static final boolean DEFAULT_TOAST_WICKET_FALL = true;
    
    public static final String UPDATE_FREQ_KEY = "updateFreq";
    public static final String NOTIFY_KEY = "notify";
    public static final String NOTIFY_EVERY5_OVER_KEY = "notifyEvery5Over";
    public static final String NOTIFY_WICKET_FALL_KEY = "notifyWicketFall";
    public static final String TOAST_KEY = "toast";
    public static final String TOAST_EVERY5_MIN_KEY = "toastEvery5Min";
    public static final String TOAST_EVERY_OVER_KEY = "toastEveryOver";
    public static final String TOAST_RUNS_SCORED_KEY = "toastRunsScored";
    public static final String TOAST_WICKET_FALL_KEY = "toastWicketFall";
    
    /**
     * ArrayAdapter connects the spinner widget to array-based data.
     */
    protected ArrayAdapter<CharSequence> mAdapter;

    
    
    /**
     * Initializes the application and the activity.
     * 1) Sets the view
     * 2) Reads the spinner's backing data from the string resources file
     * 3) Instantiates a callback listener for handling selection from the
     *    spinner
     * Notice that this method includes code that can be uncommented to force
     * tests to fail.
     *
     * This method overrides the default onCreate() method for an Activity.
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        /**
         * derived classes that use onCreate() overrides must always call the super constructor
         */
        super.onCreate(savedInstanceState);

        setContentView(R.layout.options);

        Spinner spinner = (Spinner) findViewById(R.id.Spinner01);
        this.mAdapter = ArrayAdapter.createFromResource(this, R.array.update_freq,
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(this.mAdapter);
        OnItemSelectedListener spinnerListener = new myOnItemSelectedListener(this,this.mAdapter);
        spinner.setOnItemSelectedListener(spinnerListener);

    }


    /**
     * Restores the current state of the spinner (which item is selected, and the value
     * of that item).
     * Since onResume() is always called when an Activity is starting, even if it is re-displaying
     * after being hidden, it is the best place to restore state.
     *
     * Attempts to read the state from a preferences file. If this read fails,
     * assume it was just installed, so do an initialization. Regardless, change the
     * state of the spinner to be the previous position.
     *
     * @see android.app.Activity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        if (!readInstanceState(this)) setInitialState();
        final CheckBox notify = (CheckBox) findViewById(R.id.notify_check);
        final CheckBox notifyEvery5Over = (CheckBox) findViewById(R.id.notify_every5_over);
        final CheckBox notifyWicketFall = (CheckBox) findViewById(R.id.notify_wicket_fall);
        
        notify.setChecked(Options.this.notifyB);
        if(notify.isChecked()){
        	notify.setText(R.string.yes);
        }
        else{
        	notify.setText(R.string.no);
        }
        
        notifyEvery5Over.setChecked(Options.this.notifyEvery5OverB);
        notifyWicketFall.setChecked(Options.this.notifyWicketFallB);
        
        notify.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					notify.setText(R.string.yes);
					notifyEvery5Over.setChecked(true);
					notifyWicketFall.setChecked(true);
                    Toast.makeText(Options.this, R.string.notify_yes, Toast.LENGTH_SHORT).show();
                } else {
                	notify.setText(R.string.no);
					notifyEvery5Over.setChecked(false);
					notifyWicketFall.setChecked(false);
                	Toast.makeText(Options.this, R.string.notify_no, Toast.LENGTH_SHORT).show();
                }
				Options.this.notifyB = notify.isChecked();
            	Options.this.notifyWicketFallB = notifyWicketFall.isChecked();
				Options.this.notifyEvery5OverB = notifyEvery5Over.isChecked();
			}
		});
        
        notifyEvery5Over.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					if(!Options.this.notifyB){
						notify.setChecked(true);
						notify.setText(R.string.yes);
						Toast.makeText(Options.this, R.string.notify_yes, Toast.LENGTH_SHORT).show();
					}
                } else {
                	if(!Options.this.notifyWicketFallB){
						notify.setChecked(false);
						notify.setText(R.string.no);
						Toast.makeText(Options.this, R.string.notify_no, Toast.LENGTH_SHORT).show();
					}
                }
				Options.this.notifyEvery5OverB = notifyEvery5Over.isChecked();
				Options.this.notifyB = notify.isChecked();
			}
		});
        
        notifyWicketFall.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					if(!Options.this.notifyB){
						notify.setChecked(true);
						notify.setText(R.string.yes);
						Toast.makeText(Options.this, R.string.notify_yes, Toast.LENGTH_SHORT).show();
					}
                } else {
                	if(!Options.this.notifyEvery5OverB){
						notify.setChecked(false);
						notify.setText(R.string.no);
						Toast.makeText(Options.this, R.string.notify_no, Toast.LENGTH_SHORT).show();
					}
                }
				Options.this.notifyWicketFallB = notifyWicketFall.isChecked();
				Options.this.notifyB = notify.isChecked();
			}
		});
        
        
        final CheckBox toast = (CheckBox) findViewById(R.id.toast_check);
        final CheckBox toastEvery5Min = (CheckBox) findViewById(R.id.toast_every5_min);
        final CheckBox toastEveryOver = (CheckBox) findViewById(R.id.toast_every_over);
        final CheckBox toastRunsScored = (CheckBox) findViewById(R.id.toast_runs_scored);
        final CheckBox toastWicketFall = (CheckBox) findViewById(R.id.toast_wicket_fall);
        
        toast.setChecked(Options.this.toastB);
        if(toast.isChecked()){
        	toast.setText(R.string.yes);
        }
        else{
        	toast.setText(R.string.no);
        }
        
        toastEvery5Min.setChecked(Options.this.toastEvery5MinB);
        toastEveryOver.setChecked(Options.this.toastEveryOverB);
        toastRunsScored.setChecked(Options.this.toastRunsScoredB);
        toastWicketFall.setChecked(Options.this.toastWicketFallB);

        toast.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					toast.setText(R.string.yes);
					toastEvery5Min.setChecked(true);
					toastEveryOver.setChecked(true);
					toastRunsScored.setChecked(true);
					toastWicketFall.setChecked(true);
                    Toast.makeText(Options.this, R.string.toast_yes, Toast.LENGTH_SHORT).show();
                } else {
                	toast.setText(R.string.no);
					toastEvery5Min.setChecked(false);
					toastEveryOver.setChecked(false);
					toastRunsScored.setChecked(false);
					toastWicketFall.setChecked(false);
                	Toast.makeText(Options.this, R.string.toast_no, Toast.LENGTH_SHORT).show();
                }
				Options.this.toastB = toast.isChecked();
            	Options.this.toastEvery5MinB = toastEvery5Min.isChecked();
				Options.this.toastEveryOverB = toastEveryOver.isChecked();
				Options.this.toastRunsScoredB = toastRunsScored.isChecked();
				Options.this.toastWicketFallB = toastWicketFall.isChecked();
			}
		});
        

        toastEvery5Min.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					if(!Options.this.toastB){
						toast.setChecked(true);
						toast.setText(R.string.yes);
						Toast.makeText(Options.this, R.string.toast_yes, Toast.LENGTH_SHORT).show();
					}
                } else {
                	if(!Options.this.toastEveryOverB && 
                			!Options.this.toastRunsScoredB &&
                			!Options.this.toastWicketFallB ){
						toast.setChecked(false);
						toast.setText(R.string.no);
						Toast.makeText(Options.this, R.string.toast_no, Toast.LENGTH_SHORT).show();
					}
                }
				Options.this.toastEvery5MinB = toastEvery5Min.isChecked();
				Options.this.toastB = toast.isChecked();
			}
		});

        toastEveryOver.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					if(!Options.this.toastB){
						toast.setChecked(true);
						toast.setText(R.string.yes);
						Toast.makeText(Options.this, R.string.toast_yes, Toast.LENGTH_SHORT).show();
					}
                } else {
                	if(!Options.this.toastEvery5MinB && 
                			!Options.this.toastRunsScoredB &&
                			!Options.this.toastWicketFallB ){
						toast.setChecked(false);
						toast.setText(R.string.no);
						Toast.makeText(Options.this, R.string.toast_no, Toast.LENGTH_SHORT).show();
					}
                }
				Options.this.toastEveryOverB = toastEveryOver.isChecked();
				Options.this.toastB = toast.isChecked();
			}
		});

        toastWicketFall.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					if(!Options.this.toastB){
						toast.setChecked(true);
						toast.setText(R.string.yes);
						Toast.makeText(Options.this, R.string.toast_yes, Toast.LENGTH_SHORT).show();
					}
                } else {
                	if(!Options.this.toastEveryOverB && 
                			!Options.this.toastRunsScoredB &&
                			!Options.this.toastEvery5MinB ){
						toast.setChecked(false);
						toast.setText(R.string.no);
						Toast.makeText(Options.this, R.string.toast_no, Toast.LENGTH_SHORT).show();
					}
                }
				Options.this.toastWicketFallB = toastWicketFall.isChecked();
				Options.this.toastB = toast.isChecked();
			}
		});
        
        toastRunsScored.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					if(!Options.this.toastB){
						toast.setChecked(true);
						toast.setText(R.string.yes);
						Toast.makeText(Options.this, R.string.toast_yes, Toast.LENGTH_SHORT).show();
					}
                } else {
                	if(!Options.this.toastEveryOverB && 
                			!Options.this.toastWicketFallB &&
                			!Options.this.toastEvery5MinB ){
						toast.setChecked(false);
						toast.setText(R.string.no);
						Toast.makeText(Options.this, R.string.toast_no, Toast.LENGTH_SHORT).show();
					}
                }
				Options.this.toastRunsScoredB = toastRunsScored.isChecked();
				Options.this.toastB = toast.isChecked();
			}
		});
        
        
        Spinner restoreSpinner = (Spinner)findViewById(R.id.Spinner01);
        restoreSpinner.setSelection(getmPos());
    }

    /**
     * Store the current state of the spinner (which item is selected, and the value of that item).
     * Since onPause() is always called when an Activity is about to be hidden, even if it is about
     * to be destroyed, it is the best place to save state.
     *
     * Attempt to write the state to the preferences file. If this fails, notify the user.
     *
     * @see android.app.Activity#onPause()
     */
    @Override
    public void onPause() {

        super.onPause();
        if (!writeInstanceState(this)) {
             Toast.makeText(this,
                     "Failed to write state!", Toast.LENGTH_LONG).show();
          }
    }


    public void setInitialState() {
    	// TODO - complete this
        this.mPos = DEFAULT_UPDATE_FREQ;
        
        

    }

    public boolean readInstanceState(Context c) {
    	
        SharedPreferences p = c.getSharedPreferences(PREFERENCES_FILE, MODE_WORLD_READABLE);
        
        if(!p.contains(UPDATE_FREQ_KEY)){
    		return false;
    	}
        this.mPos = p.getInt(UPDATE_FREQ_KEY, Options.DEFAULT_UPDATE_FREQ);
        this.notifyB = p.getBoolean(NOTIFY_KEY, DEFAULT_NOTIFY);
        this.notifyEvery5OverB = p.getBoolean(NOTIFY_EVERY5_OVER_KEY, DEFAULT_NOTIFY_EVERY5_OVER);
        this.notifyWicketFallB = p.getBoolean(NOTIFY_WICKET_FALL_KEY, DEFAULT_NOTIFY_WICKET_FALL);
        this.toastB = p.getBoolean(TOAST_KEY, DEFAULT_TOAST);
        this.toastEvery5MinB = p.getBoolean(TOAST_EVERY5_MIN_KEY, DEFAULT_TOAST_EVERY5_MIN);
        this.toastEveryOverB = p.getBoolean(TOAST_EVERY_OVER_KEY, DEFAULT_TOAST_EVERY_OVER);
        this.toastRunsScoredB = p.getBoolean(TOAST_RUNS_SCORED_KEY, DEFAULT_TOAST_RUNS_SCORED);
        this.toastWicketFallB = p.getBoolean(TOAST_WICKET_FALL_KEY, DEFAULT_TOAST_WICKET_FALL);
        return true;
        }

    /**
     * Write the application's current state to a properties repository.
     * @param c - The Activity's Context
     *
     */
    public boolean writeInstanceState(Context c) {
        SharedPreferences p =
                c.getSharedPreferences(Options.PREFERENCES_FILE, MODE_WORLD_READABLE);
        SharedPreferences.Editor e = p.edit();
        e.putInt(UPDATE_FREQ_KEY, this.mPos);
        
        e.putBoolean(NOTIFY_KEY, notifyB);
        e.putBoolean(NOTIFY_WICKET_FALL_KEY, notifyWicketFallB);
        e.putBoolean(NOTIFY_EVERY5_OVER_KEY, notifyEvery5OverB);
        
        e.putBoolean(TOAST_KEY, toastB);
        e.putBoolean(TOAST_EVERY5_MIN_KEY, toastEvery5MinB);
        e.putBoolean(TOAST_EVERY_OVER_KEY, toastEveryOverB);
        e.putBoolean(TOAST_RUNS_SCORED_KEY, toastRunsScoredB);
        e.putBoolean(TOAST_WICKET_FALL_KEY, toastWicketFallB);
        
        return (e.commit());
    }    


    public int getmPos() {
		return mPos;
	}


	public void setmPos(int mPos) {
		this.mPos = mPos;
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




	/**
     *  A callback listener that implements the
     *  {@link android.widget.AdapterView.OnItemSelectedListener} interface
     *  For views based on adapters, this interface defines the methods available
     *  when the user selects an item from the View.
     *
     */
    public class myOnItemSelectedListener implements OnItemSelectedListener {
        ArrayAdapter<CharSequence> mLocalAdapter;
        Activity mLocalContext;
        public myOnItemSelectedListener(Activity c, ArrayAdapter<CharSequence> ad) {

          this.mLocalContext = c;
          this.mLocalAdapter = ad;

        }
        public void onItemSelected(AdapterView<?> parent, View v, int pos, long row) {
        	if(Options.this.mPos != pos){
        		Toast.makeText(Options.this, R.string.score_update_changed, Toast.LENGTH_SHORT).show();
        	}
        	Options.this.mPos = pos;
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

}
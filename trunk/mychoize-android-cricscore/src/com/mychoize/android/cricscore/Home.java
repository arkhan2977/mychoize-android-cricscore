package com.mychoize.android.cricscore;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

public class Home extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ProgressDialog dialog = ProgressDialog.show(Home.this, "",
        		"Loading. Please wait...", true);
        dialog.show();
    }
}
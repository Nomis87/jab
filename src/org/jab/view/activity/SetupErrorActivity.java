package org.jab.view.activity;

import org.jab.main.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SetupErrorActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_error);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_setup_error, menu);
        return true;
    }
}

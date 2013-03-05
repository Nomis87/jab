package org.jab.view.activity;

import org.jab.main.R;
import org.jab.main.R.layout;
import org.jab.main.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class CreateGroupActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_create_group, menu);
        return true;
    }
}

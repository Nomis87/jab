package org.poke.error;

import org.poke.main.R;
import org.poke.main.R.layout;
import org.poke.main.R.menu;

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

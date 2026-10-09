package com.campuslf.app.ui;

import android.app.Activity;
import android.os.Bundle;
import com.campuslf.app.R;

/** Ecrã principal da aplicação. */
public final class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
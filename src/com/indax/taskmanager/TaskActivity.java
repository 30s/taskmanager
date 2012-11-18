package com.indax.taskmanager;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class TaskActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_task, menu);
        return true;
    }
}

package com.indax.taskmanager;

import com.indax.taskmanager.utils.Utils;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.Toast;

public class LoginActivity extends Activity {	
	
	private Utils m_utils;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        m_utils = new Utils(getBaseContext());
        if ( !m_utils.isNetworkAvailable() ) {
        	Toast.makeText(getBaseContext(), 
        			R.string.hint_no_network, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_login, menu);
        return true;
    }
}

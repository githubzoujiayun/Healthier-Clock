package com.jkydjk.healthier.clock;

import com.jkydjk.healthier.clock.util.ActivityHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class Resume extends BaseActivity implements OnClickListener {
  
  private SharedPreferences sharedPreference = null;
  
	private View back;
	private TextView username;
	private TextView email;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resume);
		
		if(!ActivityHelper.isLogged(this)){
		  Toast.makeText(this, R.string.not_logged_in, Toast.LENGTH_SHORT).show();
		  finish();
		}
		
		sharedPreference = this.getSharedPreferences("configure", Context.MODE_PRIVATE);

		back = findViewById(R.id.back);
		back.setOnClickListener(this);

		username = (TextView) findViewById(R.id.username);
		email = (TextView) findViewById(R.id.email);
		
		username.setText(sharedPreference.getString("username", ""));
		email.setText(sharedPreference.getString("email", ""));

	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back:
				finish();
				break;
		}
	}
}

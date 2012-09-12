package com.jkydjk.healthier.clock;

import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.util.Log;

import android.app.Activity;
import android.os.Bundle;

public class SolarTerms extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.solar_terms);
		Log.v("SolarTerms onCreate");
	}
}
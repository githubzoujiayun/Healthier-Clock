package com.jkydjk.healthier.clock;

import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.util.Log;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Favorites extends BaseActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites);
        Log.v("SolarTerms onCreate");
    }

}

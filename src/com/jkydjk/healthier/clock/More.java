package com.jkydjk.healthier.clock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class More extends BaseActivity implements OnClickListener {

    private View back;
    private View setting;
    private View help;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more);

        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        setting = findViewById(R.id.setting);
        setting.setOnClickListener(this);
        
        help = findViewById(R.id.help);
        help.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.back:
            finish();
            break;

        case R.id.setting:
            startActivity(new Intent(this, SettingsActivity.class));
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            break;
            
        case R.id.help:
            startActivity(new Intent(this, Help.class));
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            break;
        }
    }

}
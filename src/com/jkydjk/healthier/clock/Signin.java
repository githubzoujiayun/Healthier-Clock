package com.jkydjk.healthier.clock;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class Signin extends BaseActivity implements OnClickListener {

    private View cancel;
    private View enter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(this);

        enter = findViewById(R.id.enter);
        enter.setOnClickListener(this);
        
    }

    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.cancel:
            finish();
            break;

        case R.id.enter:
            finish();
            break;

        }
    }

}

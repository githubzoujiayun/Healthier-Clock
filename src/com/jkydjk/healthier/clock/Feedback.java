package com.jkydjk.healthier.clock;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class Feedback extends BaseActivity implements OnClickListener {

    private View back;
    private View enter;
    
    private EditText feedback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);

        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        enter = findViewById(R.id.enter);
        enter.setOnClickListener(this);
        
        feedback = (EditText)findViewById(R.id.feedback);
    }

    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.back:
            finish();
            break;

        case R.id.enter:
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(feedback.getWindowToken(), 0);
            
//            finish();
            break;
        }
    }

}
package com.jkydjk.healthier.clock;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Signup extends BaseActivity implements OnClickListener {

    private View cancel;
    private View enter;
    private TextView tip;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(this);

        enter = findViewById(R.id.enter);
        enter.setOnClickListener(this);

        tip = (TextView) findViewById(R.id.signup_tip);

        Pattern pattern = Pattern.compile("健康时钟的注册协议");

        Linkify.addLinks(tip, pattern, null, null, new TransformFilter() {
            public final String transformUrl(final Matcher match, String url) {
                return "http://jkydjk.com/";
            }
        });
        
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

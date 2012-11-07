package com.jkydjk.healthier.clock;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class Resume extends BaseActivity implements OnClickListener {

  private View cancel;

  private EditText username;
  private EditText email;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.signup);

    cancel = findViewById(R.id.cancel);
    cancel.setOnClickListener(this);

    username = (EditText) findViewById(R.id.username);
    email = (EditText) findViewById(R.id.email);

  }

  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.cancel:
      finish();
      break;
    }
  }

}

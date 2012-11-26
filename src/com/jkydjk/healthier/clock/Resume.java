package com.jkydjk.healthier.clock;

import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.StringUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class Resume extends BaseActivity implements OnClickListener {

  private SharedPreferences sharedPreference = null;

  private View back;
  private View logout;
  private TextView username;
  private TextView email;

  private View constitutionLayout;
  private TextView constitutionTextView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.resume);

    if (!ActivityHelper.isLogged(this)) {
      Toast.makeText(this, R.string.not_logged_in, Toast.LENGTH_SHORT).show();
      finish();
    }

    sharedPreference = this.getSharedPreferences("configure", Context.MODE_PRIVATE);

    back = findViewById(R.id.back);
    back.setOnClickListener(this);

    logout = findViewById(R.id.logout);
    logout.setOnClickListener(this);

    constitutionLayout = findViewById(R.id.constitution_layout);
    constitutionLayout.setOnClickListener(this);

    username = (TextView) findViewById(R.id.username);
    email = (TextView) findViewById(R.id.email);

    username.setText(sharedPreference.getString("username", ""));
    email.setText(sharedPreference.getString("email", ""));

    constitutionTextView = (TextView) findViewById(R.id.constitution);
    String constitutionFlag = sharedPreference.getString("constitution_flag", null);
    if (!StringUtil.isEmpty(constitutionFlag)) {
      constitutionTextView.setText(constitutionFlag);
    }

  }
  
  @Override
  protected void onResume() {
    super.onResume();
    String constitutionFlag = sharedPreference.getString("constitution_flag", null);
    if (!StringUtil.isEmpty(constitutionFlag)) {
      constitutionTextView.setText(constitutionFlag);
    }
  }


  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.back:
      finish();
      break;

    case R.id.logout:
      Editor editor = sharedPreference.edit();
      editor.remove("token");
      editor.remove("username");
      editor.remove("email");
      editor.commit();
      finish();
      break;

    case R.id.constitution_layout:
      String constitution = sharedPreference.getString("constitution", null);
      if (  StringUtil.isEmpty(constitution)) {
        startActivity(new Intent(Resume.this, ConstitutionSelector.class));
      } else {
        startActivity(new Intent(Resume.this, Constitution.class));
      }
      break;
    }
  }
}

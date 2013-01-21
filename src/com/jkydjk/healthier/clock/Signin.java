package com.jkydjk.healthier.clock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import com.jkydjk.healthier.clock.entity.User;
import com.jkydjk.healthier.clock.network.HttpClientManager;
import com.jkydjk.healthier.clock.network.ResuestMethod;
import com.jkydjk.healthier.clock.network.RequestRoute;
import com.jkydjk.healthier.clock.util.JSONHelper;
import com.jkydjk.healthier.clock.util.Log;

import org.json.JSONObject;

public class Signin extends BaseActivity implements OnClickListener {

  private View cancel;
  private View enter;
  private TextView login;
  private TextView password;
  private String errorMessage;

  private SharedPreferences sharedPreference = null;
  private Editor edit = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.signin);

    sharedPreference = this.getSharedPreferences("user", Context.MODE_PRIVATE);
    edit = sharedPreference.edit();

    cancel = findViewById(R.id.cancel);
    cancel.setOnClickListener(this);

    enter = findViewById(R.id.enter);
    enter.setOnClickListener(this);

    login = (TextView) findViewById(R.id.login);
    password = (TextView) findViewById(R.id.password);

  }

  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.cancel:
      finish();
      break;

    case R.id.enter:
      User user = submit();
      if (user != null) {
        Intent intent = new Intent(this, Resume.class);
        startActivity(intent);
        finish();
      } else {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
      }
      break;

    }
  }

  private User submit() {

    User user = null;

    String strLogin = login.getText().toString();
    String strPassword = password.getText().toString();

    HttpClientManager httpClientManager = new HttpClientManager(this, RequestRoute.USER_SIGNIN);
    httpClientManager.addParam("login", strLogin);
    httpClientManager.addParam("password", strPassword);

    try {
      httpClientManager.execute(ResuestMethod.POST);
      String result = httpClientManager.getResponse();

      JSONObject json = new JSONObject(result);

      if ("1".equals(json.getString("status"))) {

        user = User.parse(json.getJSONObject("user"));

        User.serializable(Signin.this, sharedPreference, user);

      } else {
        user = null;
        errorMessage = json.getString("message");
      }
    } catch (Exception e) {

      e.printStackTrace();
      user = null;
      errorMessage = "网络访问异常";
    }
    return user;
  }
}

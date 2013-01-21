package com.jkydjk.healthier.clock;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.jkydjk.healthier.clock.entity.User;
import com.jkydjk.healthier.clock.network.HttpClientManager;
import com.jkydjk.healthier.clock.network.RequestRoute;
import com.jkydjk.healthier.clock.network.ResuestMethod;
import com.jkydjk.healthier.clock.util.JSONHelper;
import com.jkydjk.healthier.clock.util.StringUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.sax.TextElementListener;
import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Signup extends BaseActivity implements OnClickListener {

  private View cancel;
  private View enter;
  private TextView tip;

  private EditText username;
  private EditText email;
  private EditText password;

  private String errorMessage;

  private SharedPreferences sharedPreference = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.signup);

    sharedPreference = this.getSharedPreferences("configure", Context.MODE_PRIVATE);

    cancel = findViewById(R.id.cancel);
    cancel.setOnClickListener(this);

    enter = findViewById(R.id.enter);
    enter.setOnClickListener(this);

    tip = (TextView) findViewById(R.id.signup_tip);

    username = (EditText) findViewById(R.id.username);
    email = (EditText) findViewById(R.id.email);
    password = (EditText) findViewById(R.id.password);

    Pattern pattern = Pattern.compile("健康时钟的注册协议");

    Linkify.addLinks(tip, pattern, null, null, new TransformFilter() {
      public final String transformUrl(final Matcher match, String url) {
        return "http://jkydjk.com/";
      }
    });

  }

  private boolean isCheck() {
    boolean flag = true;

    if (StringUtil.isEmpty(username.getText().toString())) {
      errorMessage = "用户名不能为空";
      flag = false;
    } else if (!(StringUtil.isEmail(email.getText().toString()))) {
      errorMessage = "Email格式不正确";
      flag = false;
    } else if (StringUtil.isEmpty(password.getText().toString())) {
      errorMessage = "密码不能为空";
      flag = false;
    }

    return flag;
  }

  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.cancel:
      finish();
      break;

    case R.id.enter:
      if (isCheck()) {
        User user = submit();
        if (user != null) {
          Intent intent = new Intent(this, Resume.class);
          startActivity(intent);
          finish();
        } else {
          Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
      } else {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
      }
      break;

    }
  }

  /**
   * 提交注册信息
   * 
   * @return
   */
  private User submit() {
    
  //ProgressDialog.show(Login.this, "请等待", "正在为你登录...", true);
    
    String strUsername = username.getText().toString();
    String strEmail = email.getText().toString();
    String strPassword = password.getText().toString();

    User user = null;

    HttpClientManager httpClientManager = new HttpClientManager(this, RequestRoute.USER_SIGNUP);

    httpClientManager.addParam("username", strUsername);
    httpClientManager.addParam("email", strEmail);
    httpClientManager.addParam("password", strPassword);

    try {
      httpClientManager.execute(ResuestMethod.POST);

      String result = httpClientManager.getResponse();

      JSONObject json = new JSONObject(result);

      if ("1".equals(json.getString("status"))) {

        user = User.parse(json.getJSONObject("user"));

        User.serializable(Signup.this, sharedPreference, user);

      } else {

        user = null;
        
        errorMessage = json.getString("message");
        
        Log.d("signup=", errorMessage);

      }
    } catch (Exception e) {
      user = null;
      errorMessage = "网络访问异常";
      e.printStackTrace();
    }
    return user;
  }

}

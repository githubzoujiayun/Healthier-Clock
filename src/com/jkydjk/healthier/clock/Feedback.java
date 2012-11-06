package com.jkydjk.healthier.clock;

import org.json.JSONObject;

import com.jkydjk.healthier.clock.network.HttpClientManager;
import com.jkydjk.healthier.clock.network.ResuestMethod;
import com.jkydjk.healthier.clock.util.StringUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

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

    feedback = (EditText) findViewById(R.id.feedback);
  }

  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.back:
      finish();
      break;

    case R.id.enter:
      InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(feedback.getWindowToken(), 0);
      submit();
      break;
    }
  }

  // 向服务器提交数据
  private void submit() {
    String context = feedback.getText().toString();
    
    if(StringUtil.isEmpty(context)){
      Toast.makeText(this, "请填写反馈内容!", Toast.LENGTH_SHORT).show();
      return;
    }

    HttpClientManager httpClientManager = new HttpClientManager(this, HttpClientManager.REQUEST_PATH + "feedback");
    httpClientManager.addParam("content", context);

    try {
      httpClientManager.execute(ResuestMethod.POST);
      String result = httpClientManager.getResponse();
      JSONObject json = new JSONObject(result);
      if ("1".equals(json.getString("status"))) {
        this.finish();
        Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
package com.jkydjk.healthier.clock;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jkydjk.healthier.clock.network.HttpClientManager;
import com.jkydjk.healthier.clock.network.RequestRoute;
import com.jkydjk.healthier.clock.network.ResuestMethod;
import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.StringUtil;
import com.jkydjk.healthier.clock.widget.CustomDialog;

public class Resume extends BaseActivity implements OnClickListener {

  private SharedPreferences sharedPreference = null;
  private Editor editor;

  private View back;
  private View logout;

  private TextView username;
  private TextView email;

  private View realnameLayout;
  private TextView realnameTextView;

  private View genderLayout;
  private TextView genderTextView;

  private View birthdayLayout;
  private TextView birthdayTextView;

  private View locationLayout;
  private TextView locationTextView;

  private View constitutionLayout;
  private TextView constitutionTextView;

  private String token;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.resume);

    if (!ActivityHelper.isLogged(this)) {
      Toast.makeText(this, R.string.not_logged_in, Toast.LENGTH_SHORT).show();
      finish();
    }

    sharedPreference = this.getSharedPreferences("configure", Context.MODE_PRIVATE);
    editor = sharedPreference.edit();

    token = sharedPreference.getString("token", null);

    back = findViewById(R.id.back);
    back.setOnClickListener(this);

    logout = findViewById(R.id.logout);
    logout.setOnClickListener(this);

    username = (TextView) findViewById(R.id.username);
    email = (TextView) findViewById(R.id.email);

    username.setText(sharedPreference.getString("username", ""));
    email.setText(sharedPreference.getString("email", ""));

    realnameLayout = findViewById(R.id.realname_layout);
    realnameLayout.setOnClickListener(this);

    realnameTextView = (TextView) findViewById(R.id.realname);

    genderLayout = findViewById(R.id.gender_layout);
    genderLayout.setOnClickListener(this);

    genderTextView = (TextView) findViewById(R.id.gender);

    birthdayLayout = findViewById(R.id.birthday_layout);
    birthdayLayout.setOnClickListener(this);

    birthdayTextView = (TextView) findViewById(R.id.birthday);

    locationLayout = findViewById(R.id.location_layout);
    locationLayout.setOnClickListener(this);

    locationTextView = (TextView) findViewById(R.id.location);

    constitutionLayout = findViewById(R.id.constitution_layout);
    constitutionLayout.setOnClickListener(this);

    constitutionTextView = (TextView) findViewById(R.id.constitution);

    initProfile();

  }

  @Override
  protected void onResume() {
    super.onResume();
    initProfile();
  }

  private void initProfile() {
    String realname = sharedPreference.getString("realname", null);
    if (!StringUtil.isEmpty(realname)) {
      realnameTextView.setText(realname);
    }

    String gender = sharedPreference.getString("gender", null);
    if (!StringUtil.isEmpty(gender)) {
      if ("male".equals(gender)) {
        genderTextView.setText("男");
      }
      if ("female".equals(gender)) {
        genderTextView.setText("女");
      }
    }

    String birthday = sharedPreference.getString("birthday", null);
    if (!StringUtil.isEmpty(birthday)) {
      birthdayTextView.setText(birthday);
    }

    String city = sharedPreference.getString("city", null);
    if (!StringUtil.isEmpty(city)) {
      locationTextView.setText(city);
    }

    String constitutionFlag = sharedPreference.getString("constitution_flag", null);
    if (!StringUtil.isEmpty(constitutionFlag)) {
      constitutionTextView.setText(constitutionFlag);
    }
  }

  public void onClick(View v) {
    switch (v.getId()) {

    // 返回
    case R.id.back:
      finish();
      break;

    // 注销(清除用户数据)
    case R.id.logout:
      Editor editor = sharedPreference.edit();
      editor.clear();
      editor.commit();
      finish();
      break;

    // 设置姓名
    case R.id.realname_layout:

      String realname = sharedPreference.getString("realname", null);
      if (!StringUtil.isEmpty(realname)) {
        Toast.makeText(Resume.this, "已设置，不可以修改!", Toast.LENGTH_SHORT).show();
      } else {
        final CustomDialog realnameDialog = new CustomDialog(this);
        realnameDialog.setTitle(R.string.set_realname);
        realnameDialog.setView(LinearLayout.inflate(Resume.this, R.layout.dialog_resume_realname, null));
        realnameDialog.setPositiveButton(R.string.cancel, new OnClickListener() {
          public void onClick(View v) {
            realnameDialog.dismiss();
          }
        });
        realnameDialog.setNegativeButton(R.string.save, new OnClickListener() {
          public void onClick(View v) {
            EditText realnameEditText = (EditText) realnameDialog.findViewById(R.id.realname_set);
            String realname = realnameEditText.getText().toString();
            saveRealname(realname);
            realnameDialog.dismiss();
          }
        });
        realnameDialog.show();
      }
      break;

    // 设置性别
    case R.id.gender_layout:
      String gender = sharedPreference.getString("gender", null);
      if ("male".equals(gender) || "female".equals(gender)) {
        Toast.makeText(Resume.this, "已设置，不可以修改!", Toast.LENGTH_SHORT).show();
      } else {

        final CustomDialog genderDialog = new CustomDialog(this);
        genderDialog.setTitle(R.string.set_gender);
        genderDialog.setContentText(R.string.after_setting_can_not_be_modified);
        genderDialog.setPositiveButton(R.string.male, new OnClickListener() {
          public void onClick(View v) {
            saveGender("male");
            genderDialog.dismiss();
          }
        });
        genderDialog.setNegativeButton(R.string.female, new OnClickListener() {
          public void onClick(View v) {
            saveGender("female");
            genderDialog.dismiss();
          }
        });
        genderDialog.show();
      }
      break;

    // 设置生日
    case R.id.birthday_layout:
      String birthday = sharedPreference.getString("birthday", null);
      if (!StringUtil.isEmpty(birthday)) {
        Toast.makeText(Resume.this, "已设置，不可以修改!", Toast.LENGTH_SHORT).show();
      } else {
        final CustomDialog birthdayDialog = new CustomDialog(this);
        birthdayDialog.setTitle(R.string.set_birthday);
        birthdayDialog.setView(LinearLayout.inflate(Resume.this, R.layout.dialog_resume_birthday, null));
        birthdayDialog.setPositiveButton(R.string.cancel, new OnClickListener() {
          public void onClick(View v) {
            birthdayDialog.dismiss();
          }
        });
        birthdayDialog.setNegativeButton(R.string.save, new OnClickListener() {
          public void onClick(View v) {
            DatePicker birthdaySet = (DatePicker) birthdayDialog.findViewById(R.id.birthday_set);
            saveBirthday(birthdaySet.getYear() + "-" + (birthdaySet.getMonth() + 1) + "-" + birthdaySet.getDayOfMonth());
            birthdayDialog.dismiss();
          }
        });
        birthdayDialog.show();
      }
      break;

    // 设置所在地
    case R.id.location_layout:
      startActivity(new Intent(Resume.this, RegionSelector.class));
      break;

    // 设置体质
    case R.id.constitution_layout:
      String constitution = sharedPreference.getString("constitution", null);
      if (StringUtil.isEmpty(constitution)) {
        startActivity(new Intent(Resume.this, ConstitutionSelector.class));
      } else {
        startActivity(new Intent(Resume.this, Constitution.class));
      }
      break;
    }
  }

  /**
   * 保存姓名
   * 
   * @param realname
   */
  private void saveRealname(String realname) {
    new HttpLongOperation().execute(RequestRoute.USER_REALNAME, "realname", realname);
  }

  /**
   * 保存性别
   * 
   * @param gender
   */
  private void saveGender(String gender) {
    new HttpLongOperation().execute(RequestRoute.USER_GENDER, "gender", gender);
  }

  /**
   * 保存生日
   * 
   * @param birthday
   */
  protected void saveBirthday(String birthday) {
    new HttpLongOperation().execute(RequestRoute.USER_BIRTHDAY, "birthday", birthday);
  }

  class HttpLongOperation extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground(String... params) {
      String route = params[0];
      String type = params[1];
      String value = params[2];

      HttpClientManager connect = new HttpClientManager(Resume.this, RequestRoute.REQUEST_PATH + route);
      connect.addParam(type, value);

      try {
        connect.execute(ResuestMethod.POST);

        JSONObject json = new JSONObject(connect.getResponse());

        if (json.getInt("status") == 1) {
          editor.putString(type, value);
          editor.commit();
          return json.getString("message");
        } else {
          return "保存失败";
        }
      } catch (Exception e) {
        return "网络访问异常";
      }
    }

    @Override
    protected void onPostExecute(String result) {
      Toast.makeText(Resume.this, result, Toast.LENGTH_SHORT).show();
      initProfile();
    }
  }

}

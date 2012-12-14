package com.jkydjk.healthier.clock;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jkydjk.healthier.clock.database.DatabaseManager;
import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.Log;
import com.jkydjk.healthier.clock.util.StringUtil;
import com.jkydjk.healthier.clock.widget.CustomDialog;

public class Resume extends BaseActivity implements OnClickListener {

  private SharedPreferences sharedPreference = null;

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

    username = (TextView) findViewById(R.id.username);
    email = (TextView) findViewById(R.id.email);

    username.setText(sharedPreference.getString("username", ""));
    email.setText(sharedPreference.getString("email", ""));

    realnameLayout = findViewById(R.id.realname_layout);
    realnameLayout.setOnClickListener(this);

    realnameTextView = (TextView) findViewById(R.id.realname);

    genderLayout = findViewById(R.id.gender_layout);
    genderLayout.setOnClickListener(this);

    birthdayLayout = findViewById(R.id.birthday_layout);
    birthdayLayout.setOnClickListener(this);

    locationLayout = findViewById(R.id.location_layout);
    locationLayout.setOnClickListener(this);

    constitutionLayout = findViewById(R.id.constitution_layout);
    constitutionLayout.setOnClickListener(this);

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
          Log.v(realnameEditText.getText().toString());
          realnameDialog.dismiss();
        }
      });
      realnameDialog.show();
      break;

    // 设置性别
    case R.id.gender_layout:
      final CustomDialog genderDialog = new CustomDialog(this);
      genderDialog.setTitle(R.string.set_gender);
      genderDialog.setContentText(R.string.after_setting_can_not_be_modified);
      genderDialog.setPositiveButton(R.string.male, new OnClickListener() {
        public void onClick(View v) {
          genderDialog.dismiss();
        }
      });
      genderDialog.setNegativeButton(R.string.female, new OnClickListener() {
        public void onClick(View v) {
          genderDialog.dismiss();
        }
      });
      genderDialog.show();
      break;

    // 设置生日
    case R.id.birthday_layout:
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

          int year = birthdaySet.getYear();
          int month = birthdaySet.getMonth();
          int day = birthdaySet.getDayOfMonth();

          Log.v("birthday: " + year + "-" + month + "-" + day);

          birthdayDialog.dismiss();
        }
      });
      birthdayDialog.show();
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

}

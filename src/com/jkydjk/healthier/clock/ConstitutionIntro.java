package com.jkydjk.healthier.clock;

import com.jkydjk.healthier.clock.util.ActivityHelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ConstitutionIntro extends BaseActivity implements OnClickListener {

  private View backAction;
  private TextView titleTextView;
  private TextView introTextView;
  private TextView descriptionTextView;

  private View settedConstitutionView;
  private View gotoTestView;

  private SharedPreferences sharedPreference = null;

  private String constitution;
  private String constitution_flag;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.constitution_intro);

    Intent intent = getIntent();

    constitution = intent.getStringExtra("constitution");

    sharedPreference = this.getSharedPreferences("configure", Context.MODE_PRIVATE);

    titleTextView = (TextView) findViewById(R.id.title_text);
    introTextView = (TextView) findViewById(R.id.intro);
    descriptionTextView = (TextView) findViewById(R.id.description);

    int title = ActivityHelper.getStringResourceID(this, "constitution_" + constitution);
    int intro = ActivityHelper.getStringResourceID(this, "constitution_" + constitution + "_intro");
    int description = ActivityHelper.getStringResourceID(this, "constitution_" + constitution + "_desc");
    
    constitution_flag = getString(title);
    
    titleTextView.setText(title);
    introTextView.setText(getString(intro).replaceAll("\n", " "));
    descriptionTextView.setText(description);

    backAction = findViewById(R.id.back);
    backAction.setOnClickListener(this);

    settedConstitutionView = findViewById(R.id.setted_constitution);
    settedConstitutionView.setOnClickListener(this);

    gotoTestView = findViewById(R.id.goto_test);
    gotoTestView.setOnClickListener(this);
  }

  public void onClick(View v) {

    switch (v.getId()) {
    case R.id.back:
      this.finish();
      break;

    case R.id.setted_constitution:
      Editor editor = sharedPreference.edit();
      editor.putString("constitution", constitution);
      editor.putString("constitution_flag", constitution_flag);
      if (editor.commit()) {
        setResult(ConstitutionSelector.SELECTED, getIntent());
        finish();
      }
      break;

    case R.id.goto_test:
      setResult(ConstitutionSelector.GOTOTEST, getIntent());
      finish();
      break;
    }
  }

}

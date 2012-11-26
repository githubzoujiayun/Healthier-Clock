package com.jkydjk.healthier.clock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Constitution extends BaseActivity implements OnClickListener {

  private View backButton;

  private TextView introTextView;

  private View retestButton;

  private SharedPreferences sharedPreference = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.constitution);

    sharedPreference = this.getSharedPreferences("configure", Context.MODE_PRIVATE);

    String constitution = sharedPreference.getString("constitution", null);

    introTextView = (TextView) findViewById(R.id.intro);

    int type = getStringResourceID(this, "constitution_" + constitution);
    int intro = getStringResourceID(this, "constitution_" + constitution + "_intro");

    introTextView.setText(getString(intro).replaceAll("\n", "") + "的" + getString(type));

    backButton = findViewById(R.id.back);
    backButton.setOnClickListener(this);

    retestButton = findViewById(R.id.retest);
    retestButton.setOnClickListener(this);

  }

  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.back:
      this.finish();
      break;

    case R.id.retest:
      startActivity(new Intent(Constitution.this, ConstitutionSelector.class));
      this.finish();
      break;
    }
  }

}

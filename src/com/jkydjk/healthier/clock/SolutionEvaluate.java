package com.jkydjk.healthier.clock;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;

public class SolutionEvaluate extends BaseActivity implements OnClickListener {
  
  private Button back;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.solution_evaluate);
    
    back = (Button)findViewById(R.id.back);
    back.setOnClickListener(this);
    
//    RadioButton
    
  }

  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.back:
      finish();
      break;

    default:
      break;
    }
    
  }

}

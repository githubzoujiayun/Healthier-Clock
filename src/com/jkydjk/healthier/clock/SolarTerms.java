package com.jkydjk.healthier.clock;

import com.jkydjk.healthier.clock.util.TaskHandler;
import com.jkydjk.healthier.clock.util.TaskHandler.ThreadTask;

import android.os.Bundle;

public class SolarTerms extends BaseActivity {

//  TaskHandler taskHandler;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.solar_terms);

//    taskHandler = new TaskHandler(this, new ThreadTask() {
//      public void run() {
//         SolarTerms.this.setContentView(R.layout.solar_terms);
//      }
//    });

  }

}

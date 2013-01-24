package com.jkydjk.healthier.clock;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.jkydjk.healthier.clock.database.DatabaseHelper;
import com.jkydjk.healthier.clock.entity.Solution;
import com.jkydjk.healthier.clock.entity.SolutionStep;
import com.jkydjk.healthier.clock.network.HttpClientManager;
import com.jkydjk.healthier.clock.network.RequestRoute;
import com.jkydjk.healthier.clock.network.ResuestMethod;
import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.Log;
import com.jkydjk.healthier.clock.util.StringUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Process extends OrmLiteBaseActivity<DatabaseHelper> implements OnClickListener {

  LayoutInflater layoutInflater;

  View back;
  View enter;

  View loading;
  View scrollViewContent;

  TextView textViewTitle;

  LinearLayout layoutSteps;

  int solutionId;

  DatabaseHelper helper;
  Dao<Solution, Integer> solutionDao;
  Dao<SolutionStep, Integer> solutionStepDao;

  Solution solution;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.process);

    Intent intent = getIntent();
    solutionId = intent.getIntExtra("solutionId", 0);

    if (solutionId == 0)
      finish();

    back = findViewById(R.id.back);
    back.setOnClickListener(this);

    enter = findViewById(R.id.enter);
    enter.setOnClickListener(this);

    loading = findViewById(R.id.loading);
    scrollViewContent = findViewById(R.id.scroll_view_content);
    textViewTitle = (TextView) findViewById(R.id.text_view_title);
    layoutSteps = (LinearLayout) findViewById(R.id.layout_steps);

    // RadioButton

    new Task().execute();
  }

  /**
   * 
   * @author miclle
   * 
   */
  class Task extends AsyncTask<String, Integer, String> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      loading.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... params) {

      layoutInflater = Process.this.getLayoutInflater();

      helper = getHelper();

      try {
        solutionDao = helper.getSolutionDao();
        solutionStepDao = helper.getSolutionStepDao();

        solution = solutionDao.queryForId(solutionId);

        if (solution == null) {
          if (!ActivityHelper.networkConnected(Process.this)) {
            return "网络未连接！";
          }

          HttpClientManager connect = new HttpClientManager(Process.this, RequestRoute.solution(solutionId));

          connect.execute(ResuestMethod.GET);

          JSONObject json = new JSONObject(connect.getResponse());

          JSONObject solutionJSON = json.getJSONObject("solution");

          solution = Solution.parseJsonObject(solutionJSON);

          solutionDao.createOrUpdate(solution);

          JSONArray stepsArray = solutionJSON.getJSONArray("steps");

          ForeignCollection<SolutionStep> steps = solutionDao.getEmptyForeignCollection("steps");

          for (int i = 0; i < stepsArray.length(); i++) {
            SolutionStep step = SolutionStep.parseJsonObject((JSONObject) stepsArray.get(i));
            step.setSolution(solution);
            solutionStepDao.delete(step);
            // solutionStepDao.createOrUpdate(step);
            steps.add(step);
          }

          solution.setSteps(steps);

        }

      } catch (Exception e) {
        e.printStackTrace();
      }

      return null;
    }

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);

      if (solution == null) {
        Process.this.finish();
        Toast.makeText(Process.this, R.string.this_solution_can_not_be_found, Toast.LENGTH_SHORT).show();
        return;
      }

      loading.setVisibility(View.GONE);
      scrollViewContent.setVisibility(View.VISIBLE);

      textViewTitle.setText(solution.getTitle());

      ForeignCollection<SolutionStep> steps = solution.getSteps();

      if (steps != null) {

        Iterator<SolutionStep> it = steps.iterator();

        while (it.hasNext()) {
          final SolutionStep step = (SolutionStep) it.next();

          View stepView = layoutInflater.inflate(R.layout.process_item, null);

          TextView stepNoTextView = (TextView) stepView.findViewById(R.id.step_no);
          stepNoTextView.setText(step.getNo() + "");

          TextView stepContentTextView = (TextView) stepView.findViewById(R.id.step_content);
          stepContentTextView.setText(step.getContent());

          layoutSteps.addView(stepView);
        }
      }

    }
  }

  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.back:
      finish();
      break;

    case R.id.enter:
      finish();
      break;

    default:
      break;
    }

  }

}

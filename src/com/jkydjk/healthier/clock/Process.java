package com.jkydjk.healthier.clock;

import java.util.Iterator;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.jkydjk.healthier.clock.database.DatabaseHelper;
import com.jkydjk.healthier.clock.entity.Names;
import com.jkydjk.healthier.clock.entity.Solution;
import com.jkydjk.healthier.clock.entity.SolutionStep;
import com.jkydjk.healthier.clock.util.Log;

public class Process extends OrmLiteBaseActivity<DatabaseHelper> implements OnClickListener {

  LayoutInflater layoutInflater;

  View back;
  View enter;

  View loadingLayout;
  View contentScrollView;

  TextView textViewTitle;

  LinearLayout layoutSteps;

  View materialAndToolLayout;

  View materialLayout;
  TextView materialTextView;
  EditText materialEditText;

  View toolLayout;
  TextView toolTextView;
  EditText toolEditText;

  View occasionLayout;
  TextView occasionTextView;

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

    loadingLayout = findViewById(R.id.loading);

    contentScrollView = findViewById(R.id.scroll_view_content);

    textViewTitle = (TextView) findViewById(R.id.text_view_title);

    materialAndToolLayout = findViewById(R.id.layout_material_and_tool);

    materialLayout = findViewById(R.id.layout_material);
    materialTextView = (TextView) findViewById(R.id.text_view_material);
    materialEditText = (EditText) findViewById(R.id.edit_text_material);

    toolLayout = findViewById(R.id.layout_tool);
    toolTextView = (TextView) findViewById(R.id.text_view_tool);
    toolEditText = (EditText) findViewById(R.id.edit_text_tool);

    toolEditText.addTextChangedListener(new TextWatcher() {

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO Auto-generated method stub

      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // TODO Auto-generated method stub

      }

      @Override
      public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub
        Log.v("Editable s: " + s);
      }
    });

    layoutSteps = (LinearLayout) findViewById(R.id.layout_steps);

    occasionLayout = findViewById(R.id.layout_occasion);
    occasionTextView = (TextView) findViewById(R.id.text_view_occasion);
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
      loadingLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... params) {

      layoutInflater = Process.this.getLayoutInflater();

      helper = getHelper();

      try {
        solutionDao = helper.getSolutionDao();
        solutionStepDao = helper.getSolutionStepDao();

        solution = solutionDao.queryForId(solutionId);

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

      loadingLayout.setVisibility(View.GONE);
      contentScrollView.setVisibility(View.VISIBLE);

      textViewTitle.setText(solution.getTitle());

      Names materials = solution.getMaterials();

      if (materials.size() > 0) {
        materialTextView.setText(String.format(getString(R.string.title_content), getString(R.string.material), materials.joinNames()));
        materialLayout.setVisibility(View.VISIBLE);
        materialAndToolLayout.setVisibility(View.VISIBLE);
      }

      Names tools = solution.getTools();

      if (tools.size() > 0) {
        toolTextView.setText(String.format(getString(R.string.title_content), getString(R.string.tool), tools.joinNames()));
        toolLayout.setVisibility(View.VISIBLE);
        materialAndToolLayout.setVisibility(View.VISIBLE);
      }

      Names occasions = solution.getOccasions();

      if (occasions.size() > 0) {
        occasionTextView.setText(String.format(getString(R.string.title_content), getString(R.string.occasion), occasions.joinNames()));
        occasionLayout.setVisibility(View.VISIBLE);
      }

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

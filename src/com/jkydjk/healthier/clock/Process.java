package com.jkydjk.healthier.clock;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.jkydjk.healthier.clock.database.DatabaseHelper;
import com.jkydjk.healthier.clock.entity.Names;
import com.jkydjk.healthier.clock.entity.Solution;
import com.jkydjk.healthier.clock.entity.SolutionProcess;
import com.jkydjk.healthier.clock.entity.SolutionStep;
import com.jkydjk.healthier.clock.entity.SolutionStepProcess;
import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.CollectionHelp;
import com.jkydjk.healthier.clock.util.Log;

/**
 * 过程管理
 * 
 * @author miclle
 * 
 */
@SuppressLint("UseSparseArrays") public class Process extends OrmLiteBaseActivity<DatabaseHelper> implements OnClickListener, OnCheckedChangeListener {

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
  RadioGroup materialRadios;
  RadioButton materialComplyRadio;
  RadioButton materialCustomRadio;

  View toolLayout;
  TextView toolTextView;
  RadioGroup toolRadios;
  RadioButton toolComplyRadio;
  RadioButton toolCustomRadio;

  View timeLayout;
  TextView timeTextView;
  RadioGroup timeRadios;
  RadioButton timeComplyRadio;
  RadioButton timeCustomRadio;

  View occasionLayout;
  TextView occasionTextView;
  RadioGroup occasionRadios;
  RadioButton occasionComplyRadio;
  RadioButton occasionCustomRadio;

  int solutionId;

  DatabaseHelper helper;
  Dao<Solution, Integer> solutionDao;
  Dao<SolutionStep, Integer> solutionStepDao;
  Dao<SolutionProcess, Integer> solutionProcessDao;
  Dao<SolutionStepProcess, Integer> solutionStepProcessDao;

  Solution solution;
  SolutionProcess solutionProcess;
  Map<Integer, SolutionStepProcess> solutionStepProcesses;

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
    materialRadios = (RadioGroup) findViewById(R.id.radios_material);
    materialRadios.setOnCheckedChangeListener(this);
    materialComplyRadio = (RadioButton) findViewById(R.id.radio_material_comply);
    materialCustomRadio = (RadioButton) findViewById(R.id.radio_material_custom);

    toolLayout = findViewById(R.id.layout_tool);
    toolTextView = (TextView) findViewById(R.id.text_view_tool);
    toolRadios = (RadioGroup) findViewById(R.id.radios_tool);
    toolRadios.setOnCheckedChangeListener(this);
    toolComplyRadio = (RadioButton) findViewById(R.id.radio_tool_comply);
    toolCustomRadio = (RadioButton) findViewById(R.id.radio_tool_custom);

    timeLayout = findViewById(R.id.layout_time);
    timeTextView = (TextView) findViewById(R.id.text_view_time);
    timeRadios = (RadioGroup) findViewById(R.id.radios_time);
    timeRadios.setOnCheckedChangeListener(this);
    timeComplyRadio = (RadioButton) findViewById(R.id.radio_time_comply);
    timeCustomRadio = (RadioButton) findViewById(R.id.radio_time_custom);

    layoutSteps = (LinearLayout) findViewById(R.id.layout_steps);

    occasionLayout = findViewById(R.id.layout_occasion);
    occasionTextView = (TextView) findViewById(R.id.text_view_occasion);
    occasionRadios = (RadioGroup) findViewById(R.id.radios_occasion);
    occasionRadios.setOnCheckedChangeListener(this);
    occasionComplyRadio = (RadioButton) findViewById(R.id.radio_occasion_comply);
    occasionCustomRadio = (RadioButton) findViewById(R.id.radio_occasion_custom);

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

      try {

        helper = getHelper();
        solutionDao = helper.getSolutionDao();
        solutionStepDao = helper.getSolutionStepDao();
        solutionProcessDao = helper.getSolutionProcessDao();
        solutionStepProcessDao = helper.getSolutionStepProcessDao();

        solution = solutionDao.queryForId(solutionId);
        solutionProcess = solutionProcessDao.queryForId(solutionId);

      } catch (Exception e) {
        e.printStackTrace();
      }

      solutionStepProcesses = new HashMap<Integer, SolutionStepProcess>();

      return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);

      if (solution == null) {
        Process.this.finish();
        Toast.makeText(Process.this, R.string.this_solution_can_not_be_found, Toast.LENGTH_SHORT).show();
        return;
      }

      if (solutionProcess == null) {
        solutionProcess = new SolutionProcess();
        solutionProcess.setId(solution.getId());
      }

      loadingLayout.setVisibility(View.GONE);
      contentScrollView.setVisibility(View.VISIBLE);

      textViewTitle.setText(solution.getTitle());

      Names materials = solution.getMaterials();

      if (materials.size() > 0) {
        materialTextView.setText(String.format(getString(R.string.title_content), getString(R.string.material), CollectionHelp.join(materials.getNames())));
        materialLayout.setVisibility(View.VISIBLE);
        materialAndToolLayout.setVisibility(View.VISIBLE);
        ActivityHelper.switchRadio(solutionProcess.getMaterialIsComply(), materialComplyRadio, materialCustomRadio);
      }

      Names tools = solution.getTools();

      if (tools.size() > 0) {
        toolTextView.setText(String.format(getString(R.string.title_content), getString(R.string.tool), CollectionHelp.join(tools.getNames())));
        toolLayout.setVisibility(View.VISIBLE);
        materialAndToolLayout.setVisibility(View.VISIBLE);
        ActivityHelper.switchRadio(solutionProcess.getToolIsComply(), toolComplyRadio, toolCustomRadio);
      }

      Names hours = solution.getHours();
      Names solarTerms = solution.getSolarTerms();

      if (hours.size() > 0 || solarTerms.size() > 0) {
        timeTextView.setText(CollectionHelp.join(hours.getNames(), solarTerms.getNames()));
        timeLayout.setVisibility(View.VISIBLE);
        ActivityHelper.switchRadio(solutionProcess.getTimeIsComply(), timeComplyRadio, timeCustomRadio);
      }

      Names occasions = solution.getOccasions();

      if (occasions.size() > 0) {
        occasionTextView.setText(String.format(getString(R.string.title_content), getString(R.string.occasion), CollectionHelp.join(occasions.getNames())));
        occasionLayout.setVisibility(View.VISIBLE);
        ActivityHelper.switchRadio(solutionProcess.getOccasionIsComply(), occasionComplyRadio, occasionCustomRadio);
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

          RadioGroup radios = (RadioGroup) stepView.findViewById(R.id.radios);
          radios.setId(step.getId());
          radios.setOnCheckedChangeListener(Process.this);

          RadioButton complyRadio = (RadioButton) stepView.findViewById(R.id.radio_comply);
          RadioButton customRadio = (RadioButton) stepView.findViewById(R.id.radio_custom);

          // 回显
          SolutionStepProcess solutionStepProcess = null;
          try {
            solutionStepProcess = solutionStepProcessDao.queryForId(step.getId());
          } catch (SQLException e) {
            e.printStackTrace();
          }

          if (solutionStepProcess == null) {
            solutionStepProcess = new SolutionStepProcess(step.getId());
          }

          solutionStepProcesses.put(step.getId(), solutionStepProcess);

          ActivityHelper.switchRadio(solutionStepProcess.isComply(), complyRadio, customRadio);

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

    case R.id.enter: {
      try {
        solutionProcessDao.createOrUpdate(solutionProcess);
      } catch (SQLException e) {
        e.printStackTrace();
      }
      // finish();
      break;
    }
    default:
      break;
    }

  }

  @Override
  public void onCheckedChanged(RadioGroup group, int checkedId) {
    switch (group.getId()) {
    case R.id.radios_material:
      solutionProcess.setMaterialIsComply(checkedId == R.id.radio_material_comply ? true : false);
      break;

    case R.id.radios_tool:
      solutionProcess.setToolIsComply(checkedId == R.id.radio_tool_comply ? true : false);
      break;

    case R.id.radios_time:
      solutionProcess.setTimeIsComply(checkedId == R.id.radio_time_comply ? true : false);
      break;

    case R.id.radios_occasion:
      solutionProcess.setOccasionIsComply(checkedId == R.id.radio_occasion_comply ? true : false);
      break;

    default:
      int id = group.getId();
      solutionStepProcesses.get(id).setComply(checkedId == R.id.radio_comply ? true : false);
      break;
    }
  }

}

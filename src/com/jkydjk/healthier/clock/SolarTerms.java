package com.jkydjk.healthier.clock;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.jkydjk.healthier.clock.adapter.SolutionListAdapter;
import com.jkydjk.healthier.clock.animation.Cycling;
import com.jkydjk.healthier.clock.database.DatabaseHelper;
import com.jkydjk.healthier.clock.entity.GenericSolution;
import com.jkydjk.healthier.clock.entity.SolarTermSolution;
import com.jkydjk.healthier.clock.network.HttpClientManager;
import com.jkydjk.healthier.clock.network.RequestRoute;
import com.jkydjk.healthier.clock.network.ResuestMethod;
import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.Log;
import com.jkydjk.healthier.clock.util.Lunar;

@SuppressLint("SimpleDateFormat")
public class SolarTerms extends OrmLiteBaseActivity<DatabaseHelper> implements OnItemClickListener, OnClickListener {

  SharedPreferences sharedPreferences;
  int solarTermIndex;

  ListView solutionList;

  View loadingPage;
  View headerView;

  ImageView picture;
  View updatedAtWrapper;
  TextView updatedAtTextView;
  View loadingView;

  DatabaseHelper helper;

  Dao<GenericSolution, Integer> genericSolutionIntegerDao;
  List<GenericSolution> genericSolutions;

  boolean isUpdatIng = false;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.solar_terms);

    sharedPreferences = getSharedPreferences("solar_terms", Context.MODE_PRIVATE);

    headerView = View.inflate(this, R.layout.solar_terms_header, null);

    updatedAtWrapper = headerView.findViewById(R.id.updated_at_wrapper);
    updatedAtWrapper.setOnClickListener(this);

    picture = (ImageView) headerView.findViewById(R.id.picture);
    picture.setOnClickListener(this);

    updatedAtTextView = (TextView) headerView.findViewById(R.id.updated_at);

    loadingView = headerView.findViewById(R.id.cycling_loading);

    loadingPage = View.inflate(this, R.layout.loading_page, null);
    loadingPage.setPadding(0, 20, 0, 20);

    solutionList = (ListView) findViewById(R.id.solution_list);
    solutionList.addHeaderView(headerView, null, false);
    solutionList.addFooterView(loadingPage, null, false);
    solutionList.setAdapter(ActivityHelper.getEmptyArrayAdapter(this));

    new Task().execute();
  }

  @Override
  protected void onResume() {
    super.onResume();
    solarTermIndex = Lunar.getCurrentSolarTermIntervalIndex();
    picture.setImageResource(ActivityHelper.getImageResourceID(this, "solar_terms_" + solarTermIndex));
  }

  /**
   * 
   * @author miclle
   * 
   */
  class Task extends AsyncTask<String, Integer, String> {

    private boolean force = false;

    /**
     * Set before execute() methods
     * 
     * @param force
     * @return
     */
    public Task setForceUpdate(boolean force) {
      this.force = force;
      return this;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      isUpdatIng = true;
      Cycling.start(loadingView);
    }

    @Override
    protected String doInBackground(String... params) {
      helper = getHelper();
      try {
        genericSolutionIntegerDao = helper.getGenericSolutionIntegerDao();
//        genericSolutions = genericSolutionIntegerDao.queryForEq("type", "recipe");
//        genericSolutionIntegerDao.

//        if (force || genericSolutions.size() == 0) {

          Log.v("==========");

          if (!ActivityHelper.networkIsConnected(SolarTerms.this)) {
            return "网络未连接！";
          }

          HttpClientManager connect = new HttpClientManager(SolarTerms.this, RequestRoute.REQUEST_PATH + RequestRoute.SOLUTION_SOLAR_TERM);
          connect.addParam("solar_term", solarTermIndex + "");
          connect.execute(ResuestMethod.GET);
          JSONObject json = new JSONObject(connect.getResponse());
          JSONArray solutionsArray = json.getJSONArray("solutions");

          HashSet<String> ids = new HashSet<String>();

          for (int i = 0; i < solutionsArray.length(); i++) {
            GenericSolution solution = GenericSolution.parseJsonObject((JSONObject) solutionsArray.get(i));
            genericSolutionIntegerDao.createOrUpdate(solution);
            ids.add(String.valueOf(solution.getId()));
//            genericSolutions.add(solution);
          }

          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
          Calendar calendar = Calendar.getInstance();

          Editor editor = sharedPreferences.edit();
          editor.putString("solution_" + solarTermIndex + "_updated_at", dateFormat.format(calendar.getTime()));
          editor.putStringSet("solution_ids", ids);
          editor.commit();
//        }

      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);

      String updatedAt = sharedPreferences.getString("solution_" + solarTermIndex + "_updated_at", null);

      if (updatedAt != null) {
        updatedAtTextView.setText("更新于" + updatedAt);
        updatedAtTextView.setVisibility(View.VISIBLE);
      }

//      if (solarTermSolutions.size() == 0) {
//        TextView loadingText = (TextView) loadingPage.findViewById(R.id.loading_text);
//        loadingText.setText("暂无方案");
//        loadingPage.findViewById(R.id.loading_icon).setVisibility(View.GONE);
//      } else {
//        solutionList.removeAllViewsInLayout();
//        solutionList.setAdapter(new SolutionListAdapter<SolarTermSolution>(SolarTerms.this, solarTermSolutions));
//        solutionList.setOnItemClickListener(SolarTerms.this);
//        solutionList.removeFooterView(loadingPage);
//      }

      Cycling.stop(loadingView);
      isUpdatIng = false;
    }
  }

  /**
   * ListView onItemClick();
   */
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    SolarTermSolution solution = (SolarTermSolution) parent.getItemAtPosition(position);
    Intent intent = new Intent(this, SolutionActivity.class);
    intent.putExtra("solutionId", solution.getSolutionId());
    startActivity(intent);
  }

  /**
   * View onClick();
   */
  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.updated_at_wrapper:
      if (!isUpdatIng)
        new Task().setForceUpdate(true).execute();
      break;
    }

  }

}

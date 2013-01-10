package com.jkydjk.healthier.clock;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.jkydjk.healthier.clock.adapter.SolutionListAdapter;
import com.jkydjk.healthier.clock.database.DatabaseHelper;
import com.jkydjk.healthier.clock.entity.SolarTermSolution;
import com.jkydjk.healthier.clock.entity.Solution;
import com.jkydjk.healthier.clock.entity.SolutionStep;
import com.jkydjk.healthier.clock.network.HttpClientManager;
import com.jkydjk.healthier.clock.network.RequestRoute;
import com.jkydjk.healthier.clock.network.ResuestMethod;
import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.Log;
import com.jkydjk.healthier.clock.util.Lunar;

@SuppressLint("SimpleDateFormat")
public class SolarTerms extends OrmLiteBaseActivity<DatabaseHelper> implements OnItemClickListener {

  SharedPreferences sharedPreferences;
  int solarTermIndex;

  
  ListView solutionList;
  View headerView;
  View loading;
  ImageView picture;
  TextView updatedAtTextView;

  DatabaseHelper helper;

  Dao<SolarTermSolution, Integer> solarTermSolutionDao;

  List<SolarTermSolution> solarTermSolutions;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.solar_terms);

    sharedPreferences = getSharedPreferences("solar_terms", Context.MODE_PRIVATE);
    
    solutionList = (ListView) findViewById(R.id.solution_list);

    headerView = View.inflate(this, R.layout.solar_terms_header, null);
    loading = View.inflate(this, R.layout.loading_page, null);
    loading.setPadding(0, 20, 0, 20);

    picture = (ImageView) headerView.findViewById(R.id.picture);
    
    updatedAtTextView = (TextView)headerView.findViewById(R.id.updated_at);

    solutionList.addHeaderView(headerView, null, false);
    solutionList.addFooterView(loading, null, false);

    solutionList.setAdapter(ActivityHelper.getEmptyArrayAdapter(this));
  }

  @Override
  protected void onResume() {
    super.onResume();

    solarTermIndex = Lunar.getCurrentSolarTermIntervalIndex();
    picture.setImageResource(ActivityHelper.getImageResourceID(this, "solar_terms_" + solarTermIndex));

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
    }

    @Override
    protected String doInBackground(String... params) {
      helper = getHelper();
      try {
        solarTermSolutionDao = helper.getSolarTermSolutionDao();

        solarTermSolutions = solarTermSolutionDao.queryForEq("solar_term_index", solarTermIndex);

        if (solarTermSolutions.size() == 0) {

          if (!ActivityHelper.networkConnected(SolarTerms.this)) {
            return "网络未连接！";
          }

          HttpClientManager connect = new HttpClientManager(SolarTerms.this, HttpClientManager.REQUEST_PATH + RequestRoute.SOLUTION_SOLAR_TERM);
          connect.addParam("solar_term", solarTermIndex + "");

          connect.execute(ResuestMethod.GET);

          JSONObject json = new JSONObject(connect.getResponse());

          JSONArray solutionsArray = json.getJSONArray("solutions");

          for (int i = 0; i < solutionsArray.length(); i++) {
            SolarTermSolution solution = SolarTermSolution.parseJsonObject((JSONObject) solutionsArray.get(i));
            solarTermSolutionDao.create(solution);
            solarTermSolutions.add(solution);
          }

          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
          Calendar calendar = Calendar.getInstance();
          String today = dateFormat.format(calendar.getTime());

          Editor editor = sharedPreferences.edit();
          editor.putString("solution_" + solarTermIndex + "_updated_at", today);
          editor.commit();
        }

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

      if (solarTermSolutions.size() == 0) {
        TextView loadingText = (TextView) loading.findViewById(R.id.loading_text);
        loadingText.setText("暂无方案");
        loading.findViewById(R.id.loading_icon).setVisibility(View.GONE);
      } else {
        solutionList.setAdapter(new SolutionListAdapter<SolarTermSolution>(SolarTerms.this, solarTermSolutions));
        solutionList.setOnItemClickListener(SolarTerms.this);
        solutionList.removeFooterView(loading);
      }

    }
  }

  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    SolarTermSolution solution = (SolarTermSolution) parent.getItemAtPosition(position);

    Intent intent = new Intent(this, SolutionActivity.class);

    intent.putExtra("solutionId", solution.getSolutionId());

    startActivity(intent);
  }

}

package com.jkydjk.healthier.clock;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.jkydjk.healthier.clock.Favorites.Task;
import com.jkydjk.healthier.clock.adapter.SolutionListAdapter;
import com.jkydjk.healthier.clock.database.DatabaseHelper;
import com.jkydjk.healthier.clock.entity.Solution;
import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.Lunar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class SolarTerms extends OrmLiteBaseActivity<DatabaseHelper> implements OnItemClickListener {

  ListView solutionList;

  View headerView;
  View loading;

  ImageView picture;

  int solarTermIndex;

  DatabaseHelper helper;
  Dao<Solution, Integer> solutionDao;
  List<Solution> solutions;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.solar_terms);

    solutionList = (ListView) findViewById(R.id.solution_list);

    headerView = View.inflate(this, R.layout.solar_terms_header, null);
    loading = View.inflate(this, R.layout.loading_page, null);
    loading.setPadding(0, 20, 0, 20);

    picture = (ImageView) headerView.findViewById(R.id.picture);

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
        solutionDao = helper.getSolutionDao();
        solutions = solutionDao.queryForAll();
        solutions.clear();
      } catch (SQLException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);

      if (solutions.size() == 0) {
        TextView loadingText = (TextView) loading.findViewById(R.id.loading_text);
        loadingText.setText("暂无方案");
        loading.findViewById(R.id.loading_icon).setVisibility(View.GONE);
      } else {
        solutionList.setAdapter(new SolutionListAdapter(SolarTerms.this, solutions));
        solutionList.setOnItemClickListener(SolarTerms.this);
        solutionList.removeFooterView(loading);
      }

    }
  }

  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    // TODO Auto-generated method stub
  }

}

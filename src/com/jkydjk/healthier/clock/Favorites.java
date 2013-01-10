package com.jkydjk.healthier.clock;

import java.sql.SQLException;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.jkydjk.healthier.clock.adapter.SolutionListAdapter;
import com.jkydjk.healthier.clock.database.DatabaseHelper;
import com.jkydjk.healthier.clock.entity.Solution;

public class Favorites extends OrmLiteBaseActivity<DatabaseHelper> implements OnItemClickListener {

  ListView solutionList;
  View noFavoritesView;
  View loading;

  DatabaseHelper helper;
  Dao<Solution, Integer> solutionDao;
  List<Solution> solutions;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.favorites);

    solutionList = (ListView) findViewById(R.id.solution_list);
    noFavoritesView = findViewById(R.id.no_favorites);
    loading = findViewById(R.id.loading);
  }

  @Override
  protected void onResume() {
    new Task().execute();
    super.onResume();
  }

  class Task extends AsyncTask<String, Integer, String> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      solutionList.setVisibility(View.GONE);
      noFavoritesView.setVisibility(View.GONE);
      loading.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... params) {
      helper = getHelper();
      try {
        solutionDao = helper.getSolutionDao();
        solutions = solutionDao.queryForEq("favorited", true);
      } catch (SQLException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);
      loading.setVisibility(View.GONE);
      if (solutions.size() == 0) {
        noFavoritesView.setVisibility(View.VISIBLE);
        solutionList.removeAllViewsInLayout();
        solutionList.setVisibility(View.GONE);
      } else {
        noFavoritesView.setVisibility(View.GONE);
        solutionList.setAdapter(new SolutionListAdapter<Solution>(Favorites.this, solutions));
        solutionList.setOnItemClickListener(Favorites.this);
        solutionList.setVisibility(View.VISIBLE);
      }
    }
  }

  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Solution solution = (Solution) parent.getItemAtPosition(position);
    Intent intent = new Intent(this, SolutionActivity.class);
    intent.putExtra("solutionId", solution.getId());
    startActivity(intent);
  }

}

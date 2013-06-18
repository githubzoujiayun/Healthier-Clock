package com.jkydjk.healthier.clock;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.analytics.tracking.android.EasyTracker;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.jkydjk.healthier.clock.adapter.GenericSolutionListAdapter;
import com.jkydjk.healthier.clock.adapter.SolutionListAdapter;
import com.jkydjk.healthier.clock.database.DatabaseHelper;
import com.jkydjk.healthier.clock.entity.GenericSolution;
import com.jkydjk.healthier.clock.entity.Solution;

public class Favorites extends OrmLiteBaseActivity<DatabaseHelper> implements OnItemClickListener {

  ListView solutionList;
  View noFavoritesView;
  View loading;

  DatabaseHelper helper;
  Dao<GenericSolution, String> genericSolutionStringDao;
  List<GenericSolution> genericSolutions;

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
        genericSolutionStringDao = helper.getGenericSolutionStringDao();
        genericSolutions = genericSolutionStringDao.queryForEq("favorited", true);
      } catch (SQLException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);
      loading.setVisibility(View.GONE);
      if (genericSolutions.size() == 0) {
        noFavoritesView.setVisibility(View.VISIBLE);
        solutionList.removeAllViewsInLayout();
        solutionList.setVisibility(View.GONE);
      } else {
        noFavoritesView.setVisibility(View.GONE);
        solutionList.setAdapter(new GenericSolutionListAdapter<GenericSolution>(Favorites.this, genericSolutions));

//        solutionList.setAdapter(new GenericSolutionListAdapter<GenericSolution>(Favorites.this, solutions));

        solutionList.setOnItemClickListener(Favorites.this);
        solutionList.setVisibility(View.VISIBLE);
      }
    }
  }

  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    GenericSolution genericSolution = (GenericSolution) parent.getItemAtPosition(position);

    String solutionType = genericSolution.getType();

    Intent intent = null;

    if (GenericSolution.Type.RECIPE.equals(solutionType)){
      intent = new Intent(this, RecipeActivity.class);
    }

    if (GenericSolution.Type.MASSAGE_SOLUTION.equals(solutionType) || GenericSolution.Type.MOXIBUSTION_SOLUTION.equals(solutionType)
      || GenericSolution.Type.CUPPING_SOLUTION.equals(solutionType) || GenericSolution.Type.SKIN_SCRAPING_SOLUTION.equals(solutionType)){
      intent = new Intent(this, SolutionActivity.class);
    }

    intent.putExtra("generic_solution_id", genericSolution.getId());
    startActivity(intent);
  }

  @Override
  protected void onStart() {
    // TODO Auto-generated method stub
    super.onStart();
    EasyTracker.getInstance().activityStart(this);
  }

  @Override
  protected void onStop() {
    // TODO Auto-generated method stub
    super.onStop();
    EasyTracker.getInstance().activityStop(this); // Add this method.
  }

}

package com.jkydjk.healthier.clock;

import java.sql.SQLException;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts.Data;
import android.view.View;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.jkydjk.healthier.clock.database.DatabaseHelper;
import com.jkydjk.healthier.clock.entity.Solution;
import com.jkydjk.healthier.clock.util.Log;

public class Favorites extends OrmLiteBaseActivity<DatabaseHelper> {

  ListView solutionlist;
  View loading;

  DatabaseHelper helper;
  Dao<Solution, Integer> solutionDao;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.favorites);

    solutionlist = (ListView) findViewById(R.id.solution_list);
    loading = findViewById(R.id.loading);

    new Task().execute();
  }

  class Task extends AsyncTask<String, Integer, String> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      loading.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... params) {
      helper = getHelper();
      try {

        solutionDao = helper.getSolutionDao();

        List<Solution> solutions = solutionDao.queryForEq("favorited", true);

      } catch (SQLException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);
      loading.setVisibility(View.GONE);
      solutionlist.setVisibility(View.VISIBLE);
    }

  }

}

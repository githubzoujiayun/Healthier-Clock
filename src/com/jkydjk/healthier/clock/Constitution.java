package com.jkydjk.healthier.clock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jkydjk.healthier.clock.database.DatabaseHelper;
import com.jkydjk.healthier.clock.network.HttpClientManager;
import com.jkydjk.healthier.clock.network.RequestRoute;
import com.jkydjk.healthier.clock.network.ResuestMethod;
import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.Log;
import com.jkydjk.healthier.clock.util.StringUtil;

public class Constitution extends BaseActivity implements OnClickListener {

  private View backButton;
  private View updateButton;
  private TextView introTextView;
  private View retestButton;
  private LinearLayout page;
  private View loading;

  private SharedPreferences sharedPreference = null;
  private LayoutInflater layoutInflater;

  private String constitutionType;

  private LongOperation longOperation;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.constitution);

    sharedPreference = this.getSharedPreferences("configure", Context.MODE_PRIVATE);

    layoutInflater = LayoutInflater.from(this);

    constitutionType = sharedPreference.getString("constitution", null);

    page = (LinearLayout) findViewById(R.id.page);

    loading = findViewById(R.id.loading);

    introTextView = (TextView) findViewById(R.id.intro);

    int type = ActivityHelper.getStringResourceID(this, "constitution_" + constitutionType);
    int intro = ActivityHelper.getStringResourceID(this, "constitution_" + constitutionType + "_intro");

    introTextView.setText(getString(intro).replaceAll("\n", "") + "的" + getString(type));

    backButton = findViewById(R.id.back);
    backButton.setOnClickListener(this);

    updateButton = findViewById(R.id.update);
    updateButton.setOnClickListener(this);

    retestButton = findViewById(R.id.retest);
    retestButton.setOnClickListener(this);

    longOperation = new LongOperation();
    longOperation.execute("");
  }

  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.back:
      this.finish();
      break;

    case R.id.update:
      longOperation = new LongOperation();
      longOperation.cache = false;
      longOperation.execute("");
      break;

    case R.id.retest:
      startActivity(new Intent(Constitution.this, ConstitutionSelector.class));
      this.finish();
      break;
    }
  }

  class LongOperation extends AsyncTask<String, Integer, String> {

    private boolean cache = true;

    @Override
    protected String doInBackground(String... params) {

      SQLiteDatabase database = new DatabaseHelper(Constitution.this).getWritableDatabase();

      if (cache) {
        String[] selectionArgs = { constitutionType };
        Cursor cursor = database.rawQuery("select * from constitutions where type = ?", selectionArgs);
        if (cursor != null && cursor.moveToFirst()) {
          do {
            String regimen = cursor.getString(cursor.getColumnIndex("regimen"));
            if (!StringUtil.isEmpty(regimen)) {
              cursor.close();
              database.close();
              return regimen;
            }
          } while (cursor.moveToNext());
        }
      }

      HttpClientManager connect = new HttpClientManager(Constitution.this, HttpClientManager.REQUEST_PATH + RequestRoute.USER_CONSTITUTION);

      connect.addParam("constitution", constitutionType);

      try {
        connect.execute(ResuestMethod.POST);

        JSONObject json = new JSONObject(connect.getResponse());

        String constitution = json.getString("constitution");

        String version = json.getString("updated_at");

        ContentValues cvs = new ContentValues();

        cvs.put("regimen", constitution);
        cvs.put("updated_at", version);

        database.update("constitutions", cvs, "type = ?", new String[] { constitutionType });

        database.close();

        return constitution;

      } catch (Exception e) { // errorMessage = "网络访问异常";
        if (database != null)
          database.close();
      }

      return null;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      loading.setVisibility(View.VISIBLE);
      updateButton.setClickable(false);
    }

    @Override
    protected void onPostExecute(String result) {

      loading.setVisibility(View.GONE);

      if (result == null) {
        Toast.makeText(Constitution.this, R.string.network_access_exception, Toast.LENGTH_SHORT).show();
        updateButton.setClickable(true);
        return;
      }

      try {

        page.removeAllViews();

        JSONObject json = new JSONObject(result);

        JSONArray fields = json.getJSONArray("fields");

        for (int i = 0; i < fields.length(); i++) {

          JSONArray field = (JSONArray) fields.get(i);

          String key = (String) field.get(0);

          String title = (String) field.get(1);

          String content = json.getString(key);

          if (!StringUtil.isEmpty(content)) {
            View contentItemView = layoutInflater.inflate(R.layout.content_item, null, false);

            TextView titleTextView = (TextView) contentItemView.findViewById(R.id.title);
            titleTextView.setText(title);

            TextView contentTextView = (TextView) contentItemView.findViewById(R.id.content);
            contentTextView.setText(content);

            page.addView(contentItemView);
          }
        }

        // longOperation.cancel(true);
        // this.cancel(true);
        updateButton.setClickable(true);

      } catch (JSONException e) {
        e.printStackTrace();
      }

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
      // TODO Auto-generated method stub
      super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
      // TODO Auto-generated method stub
      super.onCancelled();
    }

  }

}

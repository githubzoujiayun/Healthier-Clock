package com.jkydjk.healthier.clock;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.Toast;

import com.jkydjk.healthier.clock.database.DatabaseHelper;
import com.jkydjk.healthier.clock.network.HttpClientManager;
import com.jkydjk.healthier.clock.network.RequestRoute;
import com.jkydjk.healthier.clock.network.ResuestMethod;

public class RegionSelector extends BaseActivity implements OnClickListener, OnChildClickListener {

  private View backAction;
  private ExpandableListView regionList;

  private SharedPreferences sharedPreference = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.region_selector);

    sharedPreference = this.getSharedPreferences("configure", Context.MODE_PRIVATE);

    regionList = (ExpandableListView) findViewById(R.id.region_expandable_list_view);

    backAction = findViewById(R.id.back);
    backAction.setOnClickListener(this);

    SQLiteDatabase database = new DatabaseHelper(this).getWritableDatabase();

    Cursor cursor = database.rawQuery("select * from regions where type = 'Province' order by _id", null);

    RegionAdapter regionAdapter = new RegionAdapter(cursor, this, R.layout.region_item_province, R.layout.region_item_city, new String[] { "name" }, new int[] { R.id.name }, new String[] { "name" },
        new int[] { R.id.name });

    regionList.setAdapter(regionAdapter);
    regionList.setOnChildClickListener(this);
    
    //此处不能关闭资源，会导致ExpandableListView 列表不显示
    // cursor.close();
    // database.close();
  }

  /**
   * 点击事件
   */
  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.back:
      this.finish();
      break;

    default:
      break;
    }
  }

  /**
   * 按键事件
   */
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
      finish();
      return false;
    }
    return false;
  }

  class RegionAdapter extends SimpleCursorTreeAdapter {

    public RegionAdapter(Cursor cursor, Context context, int groupLayout, int childLayout, String[] groupFrom, int[] groupTo, String[] childrenFrom, int[] childrenTo) {
      super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childrenFrom, childrenTo);
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
      SQLiteDatabase database = new DatabaseHelper(RegionSelector.this).getWritableDatabase();
      return database.rawQuery("select * from regions where type = 'City' and parent_id = '" + groupCursor.getInt(groupCursor.getColumnIndex("_id")) + "' order by _id", null);
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

      if (convertView != null) {
        ImageView arrow = (ImageView) convertView.findViewById(R.id.arrow);
        arrow.setImageResource(isExpanded ? R.drawable.arrow_down : R.drawable.setting_arrow);
      }

      return super.getGroupView(groupPosition, isExpanded, convertView, parent);
    }

  }

  public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

    SQLiteDatabase database = new DatabaseHelper(this).getWritableDatabase();

    Cursor cursor = database.rawQuery("select * from regions where _id = ?", new String[] { id + "" });

    if (cursor != null && cursor.moveToFirst()) {

      Editor editor = sharedPreference.edit();

      editor.putString("city", cursor.getString(cursor.getColumnIndex("name")));
      editor.putLong("city_id", id);

      editor.commit();

      cursor.close();
      database.close();

      new HttpLongOperation().execute(RequestRoute.USER_CITY, "city_id", id + "");

      finish();
    }

    return true;
  }

  class HttpLongOperation extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground(String... params) {
      String route = params[0];
      String type = params[1];
      String value = params[2];

      HttpClientManager connect = new HttpClientManager(RegionSelector.this, HttpClientManager.REQUEST_PATH + route);
      connect.addParam(type, value);

      try {
        connect.execute(ResuestMethod.POST);

        JSONObject json = new JSONObject(connect.getResponse());

        if (json.getInt("status") == 1) {
          return json.getString("message");
        } else {
          return "保存失败";
        }
      } catch (Exception e) {
        return "网络访问异常";
      }
    }

    @Override
    protected void onPostExecute(String result) {
      Toast.makeText(RegionSelector.this, result, Toast.LENGTH_SHORT).show();
    }

  }

}

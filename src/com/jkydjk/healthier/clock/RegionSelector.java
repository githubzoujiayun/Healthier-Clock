package com.jkydjk.healthier.clock;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.jkydjk.healthier.clock.adapter.QuestionListAdapter;
import com.jkydjk.healthier.clock.adapter.RegionListAdapter;
import com.jkydjk.healthier.clock.database.DatabaseManager;
import com.jkydjk.healthier.clock.entity.Question;
import com.jkydjk.healthier.clock.entity.Region;

public class RegionSelector extends BaseActivity implements OnClickListener, OnItemClickListener {

  private View backAction;
  private ListView regionListView;

  private LayoutInflater layoutInflater;
  private SharedPreferences sharedPreference = null;

  private List<Region> provinces = new ArrayList<Region>();
  private List<Region> citys = new ArrayList<Region>();
  private List<Region> districts = new ArrayList<Region>();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.region_selector);

    layoutInflater = LayoutInflater.from(this);

    sharedPreference = this.getSharedPreferences("configure", Context.MODE_PRIVATE);

    regionListView = (ListView) findViewById(R.id.region_list_view);

    backAction = findViewById(R.id.back);
    backAction.setOnClickListener(this);

    builder();

  }

  /**
   * 构建问题列表ListView
   */
  private void builder() {

    SQLiteDatabase database = DatabaseManager.openDatabase(this);

    Cursor cursor = database.rawQuery("select * from regions where type = 'Province' order by _id", null);

    if (cursor != null && cursor.moveToFirst()) {
      do {
        Region question = new Region();

        question.setId(cursor.getInt(cursor.getColumnIndex("_id")));
        question.setType(cursor.getString(cursor.getColumnIndex("type")));
        question.setName(cursor.getString(cursor.getColumnIndex("name")));
        question.setZipcode(cursor.getString(cursor.getColumnIndex("zipcode")));
        question.setParentId(cursor.getInt(cursor.getColumnIndex("parent_id")));
        
        provinces.add(question);

      } while (cursor.moveToNext());
//      regionListView.removeAllViews();
      regionListView.setAdapter(new RegionListAdapter(this, provinces));
      regionListView.setOnItemClickListener(this);
    }
    
    cursor.close();
    database.close();
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

  /**
   * ListView 单项点击后，展开选项RadioGroup
   */
  public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {

  }

}

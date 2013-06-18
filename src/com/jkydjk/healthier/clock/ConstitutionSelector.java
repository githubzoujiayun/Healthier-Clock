package com.jkydjk.healthier.clock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.analytics.tracking.android.EasyTracker;
import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.Log;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

public class ConstitutionSelector extends BaseActivity implements OnClickListener, OnItemClickListener {

  public static final int SELECTOR = 0x000001;
  public static final int SELECTED = 0x000002;
  public static final int GOTOTEST = 0x000003;

  private String[] constitutions = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i" };

  private View cancelAction;
  private GridView constitutionGrid;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.constitution_selector);

    cancelAction = findViewById(R.id.cancel);
    cancelAction.setOnClickListener(this);

    constitutionGrid = (GridView) findViewById(R.id.constitution_grid);

    // 准备要添加的数据条目
    List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();

    for (int i = 0; i < constitutions.length; i++) {

      Map<String, Object> item = new HashMap<String, Object>();

      item.put("image", ActivityHelper.getImageResourceID(this, "constitution_" + constitutions[i]));
      item.put("text", getString(ActivityHelper.getStringResourceID(this, "constitution_" + constitutions[i] + "_intro")));
      item.put("type", constitutions[i]);
      items.add(item);
    }

    // 实例化一个适配器
    SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.constitution_selector_grid_iterm, new String[] { "image", "text" }, new int[] { R.id.image_item, R.id.text_item });

    // 将GridView和数据适配器关联
    constitutionGrid.setAdapter(adapter);
    constitutionGrid.setOnItemClickListener(this);
  }

  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.cancel:
      this.finish();
      break;
    }
  }

  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    HashMap<String, Object> item = (HashMap<String, Object>) parent.getItemAtPosition(position);
    String constitution = (String) item.get("type");

    Intent intent = new Intent(this, ConstitutionIntro.class);
    intent.putExtra("constitution", constitution);

    startActivityForResult(intent, SELECTOR);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {

    case SELECTOR:

      if (resultCode == SELECTED) {
        startActivity(new Intent(this, Constitution.class));
        finish();
      }

      if (resultCode == GOTOTEST) {
        startActivity(new Intent(this, ConstitutionTest.class));
        finish();
      }
      break;

    default:
      break;
    }
    super.onActivityResult(requestCode, resultCode, data);
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

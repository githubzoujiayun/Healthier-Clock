package com.jkydjk.healthier.clock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jkydjk.healthier.clock.util.Log;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;

public class AddAlarm extends BaseActivity implements OnClickListener, OnItemClickListener {

	private String[] alarmCategory = new String[] { "work_rest", "take_medicine", "movement", "timer", "custom" };

	private View cancelAction;
	private GridView alarmGrid;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_alarm);

		cancelAction = findViewById(R.id.cancel);
		cancelAction.setOnClickListener(this);

		alarmGrid = (GridView) findViewById(R.id.alarm_grid);

		// 准备要添加的数据条目
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < alarmCategory.length; i++) {

			Map<String, Object> item = new HashMap<String, Object>();

			item.put("image", getImageResourceID(this, "alarm_category_" + alarmCategory[i]));
			item.put("text", getString(getStringResourceID(this, "alarm_category_" + alarmCategory[i])));
			item.put("category", alarmCategory[i]);
			items.add(item);
		}

		// 实例化一个适配器
		SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.alarm_grid_iterm, new String[] { "image", "text" }, new int[] { R.id.image_item, R.id.text_item });

		// 将GridView和数据适配器关联
		alarmGrid.setAdapter(adapter);
		alarmGrid.setOnItemClickListener(this);
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

		int alarm_cate = (Integer) item.get("image");

		switch (alarm_cate) {
		case R.drawable.alarm_category_custom:
//			Toast.makeText(this, (String) item.get("category"), Toast.LENGTH_SHORT).show();
			Uri uri = Alarms.addAlarm(getContentResolver());
			// TODO: Create new alarm _after_ SetAlarm so the user has the
			// chance to cancel alarm creation.
			String segment = uri.getPathSegments().get(1);
			int newId = Integer.parseInt(segment);
			if (Log.LOGV) {
				Log.v("In AlarmClock, new alarm id = " + newId);
			}
			
			Intent intent = new Intent(this, SetAlarm.class);
			
			intent.putExtra(Alarms.ALARM_ID, newId);
			
			startActivity(intent);
			break;
		}

	}

}

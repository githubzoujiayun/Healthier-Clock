package com.jkydjk.healthier.clock;

import com.jkydjk.healthier.clock.database.DatabaseManager;
import com.jkydjk.healthier.clock.entity.Hour;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

public class HourRemind extends BaseActivity implements OnClickListener {

  private Button back;
  private TextView titleTextView;

  TextView appropriateTextView;
  TextView tabooTextView;

  ImageView meridian;
  ImageView meridianName;

  private int hour;

  String hourName;
  String hourTimeInterval;
  String hourAppropriate;
  String hourTaboo;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.hour_remind);

    Time time = new Time();
    time.setToNow();

    Intent intent = getIntent();
    hour = intent.getIntExtra("hour", Hour.from_time_hour(time.hour));

    titleTextView = (TextView) findViewById(R.id.title_text_view);
    appropriateTextView = (TextView) findViewById(R.id.appropriate_text_view);
    tabooTextView = (TextView) findViewById(R.id.taboo_text_view);

    meridian = (ImageView) findViewById(R.id.meridian);
    meridian.setImageResource(getImageResourceID(this, "meridian_" + hour));

    meridianName = (ImageView) findViewById(R.id.meridian_name);
    meridianName.setImageResource(getImageResourceID(this, "meridian_name_" + hour));

    back = (Button) findViewById(R.id.back);
    back.setOnClickListener(this);

    SQLiteDatabase database = DatabaseManager.openDatabase(this);

    Cursor cursor = database.rawQuery("select * from hours where _id = ?", new String[] { hour + "" });

    if (cursor != null && cursor.moveToFirst()) {
      hourName = cursor.getString(cursor.getColumnIndex("name"));
      hourTimeInterval = cursor.getString(cursor.getColumnIndex("interval"));
      hourAppropriate = cursor.getString(cursor.getColumnIndex("appropriate"));
      hourTaboo = cursor.getString(cursor.getColumnIndex("taboo"));

      titleTextView.setText(hourName + " " + hourTimeInterval);
      appropriateTextView.setText("宜：" + hourAppropriate);
      tabooTextView.setText("忌：" + hourTaboo);

      cursor.close();
    }
    database.close();

  }

  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.back:
      finish();
      break;

    default:
      break;
    }

  }

}

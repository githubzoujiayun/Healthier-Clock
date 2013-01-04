package com.jkydjk.healthier.clock;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jkydjk.healthier.clock.entity.Hour;
import com.jkydjk.healthier.clock.util.ActivityHelper;

public class HourRemind extends BaseActivity implements OnClickListener {

  private Button back;
  private TextView titleTextView;

  TextView appropriateTextView;
  TextView tabooTextView;

  ImageView meridian;
  ImageView meridianName;

  private long hourID;

  private Hour hour;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.hour_remind);

    Time time = new Time();
    time.setToNow();

    Intent intent = getIntent();
    hourID = intent.getIntExtra("hourID", Hour.from_time_hour(time.hour));

    titleTextView = (TextView) findViewById(R.id.title_text_view);
    appropriateTextView = (TextView) findViewById(R.id.appropriate_text_view);
    tabooTextView = (TextView) findViewById(R.id.taboo_text_view);

    meridian = (ImageView) findViewById(R.id.meridian);
    meridian.setImageResource(ActivityHelper.getImageResourceID(this, "meridian_" + hourID));

    meridianName = (ImageView) findViewById(R.id.meridian_name);
    meridianName.setImageResource(ActivityHelper.getImageResourceID(this, "meridian_name_" + hourID));

    back = (Button) findViewById(R.id.back);
    back.setOnClickListener(this);

    hour = Hour.find(this, hourID);

    if (hour != null) {
      titleTextView.setText(hour.getName() + " " + hour.getTimeInterval());
      appropriateTextView.setText("宜：" + hour.getAppropriate());
      tabooTextView.setText("忌：" + hour.getTaboo());
    }

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

package com.jkydjk.healthier.clock.adapter;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jkydjk.healthier.clock.DigitalClock;
import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.SetAlarm;
import com.jkydjk.healthier.clock.SetAlarmCustom;
import com.jkydjk.healthier.clock.entity.Alarm;
import com.jkydjk.healthier.clock.util.Alarms;
import com.jkydjk.healthier.clock.util.StringUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("UseSparseArrays")
public class AlarmAdapter extends BaseAdapter {

  private String mAm, mPm;

  private LayoutAnimationController controller;
  private AnimationSet set;
  private Animation animation;

  private View currentAlarmLayout;

  private Context context;
  private LayoutInflater layoutInflater;

  private List<Alarm> items = new ArrayList<Alarm>();

  private Map<Integer, View> views = new HashMap<Integer, View>();

  public AlarmAdapter(Context context, List<Alarm> alarms) {
    this.context = context;
    layoutInflater = LayoutInflater.from(context);
    items = alarms;

    String[] ampm = new DateFormatSymbols().getAmPmStrings();
    mAm = ampm[0];
    mPm = ampm[1];

    set = new AnimationSet(true);

    animation = new AlphaAnimation(0.0f, 1.0f);
    animation.setDuration(100);
    set.addAnimation(animation);

    animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
    animation.setDuration(100);
    set.addAnimation(animation);

    controller = new LayoutAnimationController(set, 0.5f);

  }

  public int getCount() {
    return items.size();
  }

  public Object getItem(int position) {
    return items.get(position);
  }

  public long getItemId(int position) {
    // TODO Auto-generated method stub
    return position;
  }

  public View getView(int position, View convertView, ViewGroup parent) {

    View view = views.get(position);
    if (view != null) {
      return view;
    }

    view = layoutInflater.inflate(R.layout.alarm_time, parent, false);

    ((TextView) view.findViewById(R.id.am)).setText(mAm);
    ((TextView) view.findViewById(R.id.pm)).setText(mPm);

    DigitalClock digitalClock = (DigitalClock) view.findViewById(R.id.digitalClock);
    digitalClock.setLive(false);

    final Alarm alarm = items.get(position);
    
    View alarmLnfoLayout = view.findViewById(R.id.alarm_info_layout);
    alarmLnfoLayout.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        actionToggle(v);
      }
    });

    final View closed = view.findViewById(R.id.closed);
    closed.setVisibility(!alarm.isEnabled() ? View.VISIBLE : View.GONE);

    TextView alarmName = (TextView) view.findViewById(R.id.alarm_name);
    if (!StringUtil.isEmpty(alarm.getLabel())) {
      alarmName.setText(alarm.getLabel());
    }

    // 设置闹钟文字
    final Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
    calendar.set(Calendar.MINUTE, alarm.getMinutes());
    digitalClock.updateTime(calendar);

    // 设置重复的文字或，如果它不重复留空
    TextView daysOfWeekView = (TextView) digitalClock.findViewById(R.id.daysOfWeek);

    final String daysOfWeekStr = alarm.getDaysOfWeek().toString(context, false);

    if (daysOfWeekStr != null && daysOfWeekStr.length() != 0) {
      daysOfWeekView.setText(daysOfWeekStr);
      daysOfWeekView.setVisibility(View.VISIBLE);
    } else {
      daysOfWeekView.setVisibility(View.GONE);
    }

    // 编辑按钮
    Button editButton = (Button) view.findViewById(R.id.edit);
    editButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Intent intent = new Intent(context, SetAlarmCustom.class);
        intent.putExtra(Alarms.ALARM_ID, alarm.getId());
        context.startActivity(intent);
      }
    });

    // 关闭/开启 切换按钮
    Button toggle = (Button) view.findViewById(R.id.toggle);
    toggle.setText(alarm.isEnabled() ? R.string.close : R.string.open);
    toggle.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Button button = (Button) v;
        if (alarm.isEnabled() == true) {
          alarm.setEnabled(false);
          Alarms.enableAlarm(context, alarm.getId(), false);
          button.setText(R.string.open);
        } else {
          alarm.setEnabled(true);
          Alarms.enableAlarm(context, alarm.getId(), true);
          SetAlarm.popAlarmSetToast(context, alarm.getHour(), alarm.getMinutes(), alarm.getDaysOfWeek());
          button.setText(R.string.close);
        }
        closed.setVisibility(!alarm.isEnabled() ? View.VISIBLE : View.GONE);
      }
    });

    // 删除按钮
    Button deleteButton = (Button) view.findViewById(R.id.delete);
    deleteButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        // Confirm that the alarm will be deleted.
        new AlertDialog.Builder(context).setTitle(context.getString(R.string.delete_alarm)).setMessage(context.getString(R.string.delete_alarm_confirm))
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface d, int w) {
                Alarms.deleteAlarm(context, alarm.getId());
                
//                updateAlarmList();
              }
            }).setNegativeButton(android.R.string.cancel, null).show();
      }
    });

    // // 跳过按钮
    // Button skipButton = (Button) view.findViewById(R.id.skip);
    // skipButton.setOnClickListener(new OnClickListener() {
    // public void onClick(View v) {
    // Intent intent = new Intent(AlarmClock.this, AlarmAlertTest.class);
    // // intent.putExtra(Alarms.ALARM_ID, alarm.id);
    // startActivity(intent);
    //
    // }
    // });

    views.put(position, view);
    return view;
  }

  private void actionToggle(View v) {
    View parentLayout = (View) v.getParent();

    LinearLayout alarmActionsLayout;
    ImageView arrow;

    if (currentAlarmLayout != null && currentAlarmLayout != parentLayout) {
      alarmActionsLayout = (LinearLayout) currentAlarmLayout.findViewById(R.id.alarm_actions_layout);
      arrow = (ImageView) currentAlarmLayout.findViewById(R.id.arrow);
      alarmActionsLayout.setVisibility(View.GONE);
      arrow.setVisibility(View.GONE);
      alarmActionsLayout.setEnabled(true);
      currentAlarmLayout = null;
    }

    alarmActionsLayout = (LinearLayout) parentLayout.findViewById(R.id.alarm_actions_layout);
    arrow = (ImageView) parentLayout.findViewById(R.id.arrow);

    if (alarmActionsLayout.isEnabled() == true) {
      currentAlarmLayout = parentLayout;
      alarmActionsLayout.setLayoutAnimation(controller);

      alarmActionsLayout.setVisibility(View.VISIBLE);
      arrow.setVisibility(View.VISIBLE);
      alarmActionsLayout.setEnabled(false);
    } else {
      alarmActionsLayout.setVisibility(View.GONE);
      arrow.setVisibility(View.GONE);
      alarmActionsLayout.setEnabled(true);
      currentAlarmLayout = null;
    }

  }

}

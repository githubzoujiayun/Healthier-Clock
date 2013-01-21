package com.jkydjk.healthier.clock;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Simple widget to show analog clock.
 * 
 * @author miclle
 * 
 */
public class AnalogAppWidgetProvider extends BroadcastReceiver {

  static final String TAG = "AnalogAppWidgetProvider";

  public void onReceive(Context context, Intent intent) {
    String action = intent.getAction();

    if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {

      RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.analog_appwidget);
      
      PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, Healthier.class), PendingIntent.FLAG_CANCEL_CURRENT);
      
//      PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, ClockPicker.class), PendingIntent.FLAG_CANCEL_CURRENT);
      
      views.setOnClickPendingIntent(R.id.analog_appwidget, pendingIntent);

      int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

      AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

      appWidgetManager.updateAppWidget(appWidgetIds, views);
    }
  }
}

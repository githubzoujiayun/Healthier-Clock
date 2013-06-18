package com.jkydjk.healthier.clock;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import com.google.analytics.tracking.android.EasyTracker;

public class SystemRingtone extends BaseActivity implements OnClickListener {

  private MediaPlayer mediaPlayer;
  private LayoutInflater mFactory;

  private Uri alert;

  private Uri currentPlay;
  private CheckedTextView currentChecked;

  private View cancelAction;
  private View enterAction;

  private ListView ringtones;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.system_ringtone);

    Intent intent = getIntent();
    alert = (Uri) intent.getParcelableExtra("alert");

    cancelAction = findViewById(R.id.cancel);
    cancelAction.setOnClickListener(this);

    enterAction = findViewById(R.id.enter);
    enterAction.setOnClickListener(this);

    ringtones = (ListView) findViewById(R.id.ringtones);

    mFactory = LayoutInflater.from(this);

    final RingtoneManager manager = new RingtoneManager(this);

    manager.setType(RingtoneManager.TYPE_ALARM);

    CursorAdapter ringtoneAdapter = new CursorAdapter(this, manager.getCursor()) {

      @Override
      public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mFactory.inflate(R.layout.system_ringtone_item, parent, false);
      }

      @Override
      public void bindView(View view, Context context, Cursor cursor) {

        final Uri uri = manager.getRingtoneUri(cursor.getInt(RingtoneManager.ID_COLUMN_INDEX));

        CheckedTextView ctv = (CheckedTextView) view.findViewById(R.id.ringtone);
        ctv.setText(cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX));

        if(uri.equals(alert)){
          ctv.setChecked(true);
          currentChecked = ctv;
        }

        ctv.setOnClickListener(new OnClickListener() {
          public void onClick(View v) {

            CheckedTextView ctv = (CheckedTextView) v;
            ctv.setChecked(true);

            if(currentChecked != null && !currentChecked.equals(ctv)){
              currentChecked.setChecked(false);
            }

            currentChecked = ctv;

            autoPlayMedia(uri);
          }
        });
      }
    };

    ringtones.setAdapter(ringtoneAdapter);

  }

  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.cancel:
        finish();
        break;

      case R.id.enter:
        Intent intent = getIntent();
        intent.putExtra("alert", alert);
        setResult(RESULT_OK, intent);
        finish();
        break;

      default:
        break;
    }
  }

  private void autoPlayMedia(Uri uri) {
    alert = uri;
    if (currentPlay != uri) {
      if (mediaPlayer != null) {
        stopPlayMedia();
      }
      mediaPlayer = MediaPlayer.create(this, uri);
      mediaPlayer.start();
      currentPlay = uri;

      mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {
          stopPlayMedia();
        }
      });

    } else {
      stopPlayMedia();
    }
  }

  private void stopPlayMedia() {
    if (mediaPlayer != null) {
      mediaPlayer.release();
      mediaPlayer = null;
      currentPlay = null;
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    stopPlayMedia();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    stopPlayMedia();
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

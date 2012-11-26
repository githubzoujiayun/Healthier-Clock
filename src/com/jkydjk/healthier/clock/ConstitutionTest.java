package com.jkydjk.healthier.clock;

import com.jkydjk.healthier.clock.database.DatabaseManager;
import com.jkydjk.healthier.clock.util.Log;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

public class ConstitutionTest extends BaseActivity implements OnClickListener {

  private View closeAction;
  private LinearLayout questionList;

  private LayoutInflater layoutInflater;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.constitution_test);

    layoutInflater = LayoutInflater.from(this);

    questionList = (LinearLayout) findViewById(R.id.question_list);

    closeAction = findViewById(R.id.close);
    closeAction.setOnClickListener(this);

    SQLiteDatabase database = DatabaseManager.openDatabase(this);

    Cursor cursor = database.rawQuery("select * from constitution_questions order by _id", null);

    if (cursor != null && cursor.moveToFirst()) {
      do {
        View view = layoutInflater.inflate(R.layout.constitution_question, questionList, false);
        
        view.setId(cursor.getPosition());
        
        TextView question = (TextView) view.findViewById(R.id.question_text);
        
        question.setOnClickListener(this);
        
        RadioGroup options = (RadioGroup)view.findViewById(R.id.options);
        
        question.setText(cursor.getInt(cursor.getColumnIndex("_id")) + ". " + cursor.getString(cursor.getColumnIndex("question")));

        // ctcmq.setType(cur.getString(cur.getColumnIndex("type")));
        // ctcmq.setLabel(cur.getInt(cur.getColumnIndex("label")));
        // ctcmq.setScore(cur.getInt(cur.getColumnIndex("score")));

        questionList.addView(view);

      } while (cursor.moveToNext());
    }
    cursor.close();
    database.close();
  }

  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.close:
      this.finish();
      break;
      
    default:
      ViewParent parent = v.getParent();
      
      break;
    }
  }

}

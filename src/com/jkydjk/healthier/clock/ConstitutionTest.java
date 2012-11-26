package com.jkydjk.healthier.clock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.jkydjk.healthier.clock.database.DatabaseManager;
import com.jkydjk.healthier.clock.util.Log;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class ConstitutionTest extends BaseActivity implements OnClickListener, OnCheckedChangeListener {

  private View closeAction;
  private LinearLayout questionList;
  private View calculateButton;

  private LayoutInflater layoutInflater;

  private SharedPreferences sharedPreference = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.constitution_test);

    layoutInflater = LayoutInflater.from(this);

    sharedPreference = this.getSharedPreferences("configure", Context.MODE_PRIVATE);

    questionList = (LinearLayout) findViewById(R.id.question_list);
    calculateButton = findViewById(R.id.calculate);
    calculateButton.setOnClickListener(this);

    closeAction = findViewById(R.id.close);
    closeAction.setOnClickListener(this);

    builder();
    
  }
  
  private void builder(){
    SQLiteDatabase database = DatabaseManager.openDatabase(this);

    Cursor cursor = database.rawQuery("select * from constitution_questions group by question order by _id", null);

    if (cursor != null && cursor.moveToFirst()) {
      do {
        View view = layoutInflater.inflate(R.layout.constitution_question, questionList, false);

        questionList.addView(view);

        int index = cursor.getPosition() + 1;

        int id = cursor.getInt(cursor.getColumnIndex("_id"));
        String question = cursor.getString(cursor.getColumnIndex("question"));
        int score = cursor.getInt(cursor.getColumnIndex("score"));

        TextView questionTextView = (TextView) view.findViewById(R.id.question_text);
        questionTextView.setId(id + 10000);
        questionTextView.setText(index + ". " + question);
        questionTextView.setHint(question);
        questionTextView.setOnClickListener(this);

        RadioGroup options = (RadioGroup) view.findViewById(R.id.options);
        options.setId(id);
        options.setOnCheckedChangeListener(this);

        if (score > 0) {
          options.setVisibility(View.VISIBLE);
          RadioButton radio = null;
          switch (score) {
          case 1:
            radio = (RadioButton) options.findViewById(R.id.question_option_no);
            radio.setChecked(true);
            break;

          case 2:
            radio = (RadioButton) options.findViewById(R.id.question_option_seldom);
            radio.setChecked(true);
            break;

          case 3:
            radio = (RadioButton) options.findViewById(R.id.question_option_sometimes);
            radio.setChecked(true);
            break;

          case 4:
            radio = (RadioButton) options.findViewById(R.id.question_option_often);
            radio.setChecked(true);
            break;

          case 5:
            radio = (RadioButton) options.findViewById(R.id.question_option_always);
            radio.setChecked(true);
            break;

          default:
            break;
          }
        }

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
    case R.id.calculate:
      calculate();
      break;

    default:
      View options = findViewById(v.getId() - 10000);
      options.setVisibility(View.VISIBLE);
      break;
    }
  }

  public void onCheckedChanged(RadioGroup group, int checkedId) {

    int id = group.getId();
    int score = 0;

    TextView questionTextView = (TextView) findViewById(id + 10000);
    String question = questionTextView.getHint().toString();

    switch (checkedId) {
    case R.id.question_option_no:
      score = 1;
      break;

    case R.id.question_option_seldom:
      score = 2;
      break;

    case R.id.question_option_sometimes:
      score = 3;
      break;

    case R.id.question_option_often:
      score = 4;
      break;

    case R.id.question_option_always:
      score = 5;
      break;

    default:
      break;
    }

    SQLiteDatabase database = DatabaseManager.openDatabase(this);

    ContentValues cvs = new ContentValues();

    cvs.put("score", score);

    String[] whereArgs = { question };

    int i = database.update("constitution_questions", cvs, "question=?", whereArgs);

    if (i > 0) {
      // Toast toast = Toast.makeText(this, "带图片的Toast", Toast.LENGTH_SHORT);
      // toast.setView(linearLayout);
      // toast.show();
      // Toast.makeText(getApplicationContext(), currentQuestion.getId() +
      // "|" + score + "|" + question.getHint(),
      // Toast.LENGTH_SHORT).show();
    }

    database.close();
  }

  private void calculate() {

    Map<Double, String> scoreType = new HashMap<Double, String>();
    Map<String, Double> typeScore = new HashMap<String, Double>();

    calculateScore(scoreType, typeScore);

    double a = typeScore.get("a");
    double b = typeScore.get("b");
    double c = typeScore.get("c");
    double d = typeScore.get("d");
    double e = typeScore.get("e");
    double f = typeScore.get("f");
    double g = typeScore.get("g");
    double h = typeScore.get("h");
    double i = typeScore.get("i");

    double[] biased = { b, c, d, e, f, g, h, i };

    Arrays.sort(biased);

    double max = biased[biased.length - 1];

    Editor editor = sharedPreference.edit();

    if (a >= 60 && max < 40) {
      editor.putString("constitution", "a");
      editor.putString("constitution_flag", getString(getStringResourceID(this, "constitution_a")));
      if (max > 30) {
        editor.putString("constitution_second", scoreType.get(max));
        editor.putString("constitution_second_flag", getString(getStringResourceID(this, "constitution_" + scoreType.get(max))));
      }
    } else {
      if (max > 30) {
        editor.putString("constitution", scoreType.get(max));
        editor.putString("constitution_flag", getString(getStringResourceID(this, "constitution_" + scoreType.get(max))));
      }
    }

    if (editor.commit()) {
      startActivity(new Intent(this, Constitution.class));
      finish();
    }

  }

  /**
   * 计算体质得分（转化分）
   * 
   * @param database
   * @param type
   * @return
   */
  private void calculateScore(Map<Double, String> scoreType, Map<String, Double> typeScore) {

    String[] types = { "a", "b", "c", "d", "e", "f", "g", "h", "i" };

    SQLiteDatabase database = DatabaseManager.openDatabase(this);

    for (int i = 0; i < types.length; i++) {
      Cursor cur = database.rawQuery("SELECT * FROM constitution_questions WHERE type='" + types[i] + "'", null);
      double score = 0;
      double size = 0;
      if (cur != null && cur.moveToFirst()) {
        do {
          if (cur.getInt(cur.getColumnIndex("label")) == 1) { // 平和质分组题中label项逆向算分
            score += 6 - cur.getInt(cur.getColumnIndex("score"));
          } else {
            score += cur.getInt(cur.getColumnIndex("score"));
          }
          size++;
        } while (cur.moveToNext());
      }
      score = (score - size) / (size * 4) * 100;
      scoreType.put(score, types[i]);
      typeScore.put(types[i], score);
    }
    database.close();
  }

}

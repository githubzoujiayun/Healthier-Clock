package com.jkydjk.healthier.clock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.jkydjk.healthier.clock.adapter.QuestionListAdapter;
import com.jkydjk.healthier.clock.database.DatabaseManager;
import com.jkydjk.healthier.clock.entity.Question;
import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.widget.CustomDialog;

public class ConstitutionTest extends BaseActivity implements OnClickListener, OnItemClickListener, OnCheckedChangeListener {

  private View closeButton;
  private ListView questionListView;

  private LayoutInflater layoutInflater;
  private SharedPreferences sharedPreference = null;
  private List<Object> questions = new ArrayList<Object>();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.constitution_test);

    layoutInflater = LayoutInflater.from(this);

    sharedPreference = this.getSharedPreferences("configure", Context.MODE_PRIVATE);

    questionListView = (ListView) findViewById(R.id.question_list_view);

    closeButton = findViewById(R.id.close);
    closeButton.setOnClickListener(this);

    builder();

  }

  /**
   * 构建问题列表ListView
   */
  private void builder() {

    View constitutionTextTop = layoutInflater.inflate(R.layout.constitution_test_top, null, false);

    questions.add(0, constitutionTextTop);

    SQLiteDatabase database = DatabaseManager.openDatabase(this);

    ContentValues cvs = new ContentValues();
    cvs.put("score", "");
    database.update("constitution_questions", cvs, null, null); // 清空分值

    Cursor cursor = database.rawQuery("select * from constitution_questions group by question order by _id", null);

    if (cursor != null && cursor.moveToFirst()) {
      do {
        Question question = new Question();

        question.setId(cursor.getInt(cursor.getColumnIndex("_id")));
        question.setTitle(cursor.getString(cursor.getColumnIndex("question")));
        question.setScore(cursor.getInt(cursor.getColumnIndex("score")));
        question.setType(cursor.getString(cursor.getColumnIndex("type")));
        question.setLabel(cursor.getInt(cursor.getColumnIndex("label")));

        questions.add(question);

      } while (cursor.moveToNext());
      questionListView.setAdapter(new QuestionListAdapter(this, questions));
      questionListView.setOnItemClickListener(this);
    }

    View constitutionTextBottom = layoutInflater.inflate(R.layout.constitution_test_bottom, null, false);

    questions.add(constitutionTextBottom);

    Button calculate = (Button) constitutionTextBottom.findViewById(R.id.calculate);
    calculate.setOnClickListener(this);

    cursor.close();
    database.close();
  }

  /**
   * 点击事件
   */
  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.close:
      exit();
      break;

    case R.id.calculate:
      calculate();
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
      exit();
      return false;
    }
    return false;
  }

  /**
   * 退出
   */
  public void exit() {

    final CustomDialog dialog = new CustomDialog(this);

    dialog.setTitle(R.string.tip);
    dialog.setContentText(R.string.constitution_test_dialog_tip);

    dialog.setPositiveButton(R.string.abandon_the_test, new OnClickListener() {
      public void onClick(View v) {
        dialog.dismiss();
        ConstitutionTest.this.finish();
      }
    });

    dialog.show();
  }

  /**
   * 计算体质
   */
  public void calculate() {

    SQLiteDatabase database = DatabaseManager.openDatabase(this);

    Cursor cursor = database.rawQuery("select * from constitution_questions where score = '' or score = 0 or score = 'NULL' or score is NULL", null);

    if (cursor.getCount() > 0) {
      Toast.makeText(this, R.string.constitution_test_not_complete, Toast.LENGTH_SHORT).show();
      cursor.close();
      database.close();
      return;
    }

    Map<Double, String> scoreType = new HashMap<Double, String>();
    Map<String, Double> typeScore = new HashMap<String, Double>();

    String[] types = { "a", "b", "c", "d", "e", "f", "g", "h", "i" };

    for (int i = 0; i < types.length; i++) {
      cursor = database.rawQuery("SELECT * FROM constitution_questions WHERE type = '" + types[i] + "'", null);
      double score = 0;
      double size = 0;
      if (cursor != null && cursor.moveToFirst()) {
        do {
          if (cursor.getInt(cursor.getColumnIndex("label")) == 1) { // 平和质分组题中label项逆向算分
            score += 6 - cursor.getInt(cursor.getColumnIndex("score"));
          } else {
            score += cursor.getInt(cursor.getColumnIndex("score"));
          }
          size++;
        } while (cursor.moveToNext());
      }
      score = (score - size) / (size * 4) * 100;
      scoreType.put(score, types[i]);
      typeScore.put(types[i], score);
    }
    database.close();

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
      editor.putString("constitution_flag", getString(ActivityHelper.getStringResourceID(this, "constitution_a")));
      if (max > 30) {
        editor.putString("constitution_second", scoreType.get(max));
        editor.putString("constitution_second_flag", getString(ActivityHelper.getStringResourceID(this, "constitution_" + scoreType.get(max))));
      }
    } else {
      if (max > 30) {
        editor.putString("constitution", scoreType.get(max));
        editor.putString("constitution_flag", getString(ActivityHelper.getStringResourceID(this, "constitution_" + scoreType.get(max))));
      }
    }

    if (editor.commit()) {
      startActivity(new Intent(this, Constitution.class));
      finish();
    }

  }

  /**
   * ListView 单项点击后，展开选项RadioGroup
   */
  public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {

    Question question = (Question) adapter.getItemAtPosition(position);

    view.setClickable(true);

    RadioGroup options = (RadioGroup) view.findViewById(R.id.options);
    if (options != null) {
      options.setVisibility(View.VISIBLE);
      options.setClickable(true);
      options.findViewById(R.id.question_option_no).setClickable(true);
      options.findViewById(R.id.question_option_seldom).setClickable(true);
      options.findViewById(R.id.question_option_sometimes).setClickable(true);
      options.findViewById(R.id.question_option_often).setClickable(true);
      options.findViewById(R.id.question_option_always).setClickable(true);
      options.setId(question.getId());
      options.setOnCheckedChangeListener(this);
    }
  }

  /**
   * 选项单选按钮点击事件
   */
  public void onCheckedChanged(RadioGroup group, int checkedId) {

    int id = group.getId();
    int score = 0;

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
    String[] whereArgs = { id + "" };
    database.update("constitution_questions", cvs, "question = (select question from constitution_questions where _id = ?)", whereArgs);
    database.close();
  }

}

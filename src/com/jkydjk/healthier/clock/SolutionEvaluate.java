package com.jkydjk.healthier.clock;

import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.jkydjk.healthier.clock.database.DatabaseHelper;
import com.jkydjk.healthier.clock.entity.SolutionComment;
import com.jkydjk.healthier.clock.network.HttpClientManager;
import com.jkydjk.healthier.clock.network.RequestRoute;
import com.jkydjk.healthier.clock.network.ResuestMethod;
import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.Log;
import com.jkydjk.healthier.clock.widget.CustomDialog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

@SuppressLint("UseValueOf")
public class SolutionEvaluate extends OrmLiteBaseActivity<DatabaseHelper> implements OnClickListener, OnCheckedChangeListener {

  Button back;
  Button enter;
  RadioGroup effectRadios;

  int solutionId;

  int effectFeel;

  private RatingBar cost;
  private RatingBar convenience;
  private RatingBar overall;

  SolutionComment comment;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.solution_evaluate);

    Intent intent = getIntent();
    solutionId = intent.getIntExtra("solutionId", 0);

    if (solutionId == 0)
      return;

    back = (Button) findViewById(R.id.back);
    back.setOnClickListener(this);

    enter = (Button) findViewById(R.id.enter);
    enter.setOnClickListener(this);

    effectRadios = (RadioGroup) findViewById(R.id.radio_group_effect);
    effectRadios.setOnCheckedChangeListener(this);

    cost = (RatingBar) findViewById(R.id.rating_bar_cost);
    convenience = (RatingBar) findViewById(R.id.rating_bar_convenience);
    overall = (RatingBar) findViewById(R.id.rating_bar_overall);

    try {
      comment = getHelper().getSolutionCommentDao().queryForId(solutionId);
      if (comment != null) {
        echo();
      } else {
        comment = new SolutionComment();
      }
    } catch (SQLException e) {
      comment = new SolutionComment();
      e.printStackTrace();
    }
  }

  /**
   * 点击处理
   */
  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.back:
      finish();
      break;

    case R.id.enter:
      commitComment();
      break;

    default:
      break;
    }
  }

  /**
   * 效果感觉选择
   */
  public void onCheckedChanged(RadioGroup group, int checkedId) {
    switch (checkedId) {
    case R.id.effect_feel_cure: // 治愈
      effectFeel = 1;
      break;

    case R.id.effect_feel_improvement: // 改善
      effectFeel = 2;
      break;

    case R.id.effect_feel_invalid: // 无效
      effectFeel = 3;
      break;

    case R.id.effect_feel_aggravating: // 加重
      effectFeel = 4;
      break;

    default:
      break;
    }
  }

  /**
   * 评价回显
   */
  private void echo() {

    switch (comment.getEffectFeel()) {
    case 1: // 治愈
      effectRadios.check(R.id.effect_feel_cure);
      break;

    case 2: // 改善
      effectRadios.check(R.id.effect_feel_improvement);
      break;

    case 3: // 无效
      effectRadios.check(R.id.effect_feel_invalid);
      break;

    case 4: // 加重
      effectRadios.check(R.id.effect_feel_aggravating);
      break;

    default:
      break;
    }

    cost.setRating(comment.getCost());
    convenience.setRating(comment.getConvenience());
    overall.setRating(comment.getOverall());
  }

  /**
   * 提交评价
   */
  private void commitComment() {

    if (effectFeel == 0) {
      Toast.makeText(this, R.string.please_select_the_effect_and_feel, Toast.LENGTH_SHORT).show();
      return;
    }

    comment.setId(solutionId);
    comment.setEffectFeel(effectFeel);
    comment.setCost(new Float(cost.getRating()).intValue());
    comment.setConvenience(new Float(convenience.getRating()).intValue());
    comment.setOverall(new Float(overall.getRating()).intValue());

    new Task().execute();
  }

  class Task extends AsyncTask<String, Integer, String> {
    CustomDialog dialog;

    @Override
    protected void onPreExecute() {

      dialog = new CustomDialog(SolutionEvaluate.this);

      dialog.setTitle(R.string.tip);
      dialog.setContentText(R.string.constitution_test_dialog_tip);

      dialog.setPositiveButton(R.string.cancel, new OnClickListener() {
        public void onClick(View v) {
          dialog.dismiss();
          SolutionEvaluate.this.finish();
        }
      });

      dialog.show();
      super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

      try {

        if (!ActivityHelper.networkConnected(SolutionEvaluate.this)) {
          return SolutionEvaluate.this.getString(R.string.network_is_not_connected);
        }

        HttpClientManager connect = new HttpClientManager(SolutionEvaluate.this, RequestRoute.solutionEvaluate(solutionId));

        connect.addParam("effect", comment.getEffectFeel() + "");
        connect.addParam("cost", comment.getCost() + "");
        connect.addParam("convenience", comment.getConvenience() + "");
        connect.addParam("overall", comment.getOverall() + "");

        connect.execute(ResuestMethod.POST);

        if (connect.getResponseCode() == 200) {

          getHelper().getSolutionCommentDao().createOrUpdate(comment);

          return SolutionEvaluate.this.getString(R.string.evaluation_of_success);
        }

      } catch (Exception e) {
        e.printStackTrace();
      }

      return SolutionEvaluate.this.getString(R.string.evaluation_of_failure);
    }

    @Override
    protected void onPostExecute(String result) {
      dialog.dismiss();

      Toast.makeText(SolutionEvaluate.this, result, Toast.LENGTH_SHORT).show();

      // SolutionEvaluate.this.finish();
      super.onPostExecute(result);
    }

  }

}

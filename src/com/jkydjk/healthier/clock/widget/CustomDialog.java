package com.jkydjk.healthier.clock.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jkydjk.healthier.clock.R;

public class CustomDialog extends Dialog {

  Context context;

  private TextView title;
  private View close;
  private View actions;
  private View divider;
  private Button positive;
  private Button negative;

  private int positiveTextResourceID;
  private int negativeTextResourceID;

  private int titleRexourceID;
  private Button.OnClickListener positiveOnClickListener;
  private Button.OnClickListener negativeOnClickListener;

  public CustomDialog(Context context) {
    // super(context);
    super(context, R.style.CustomDialog);
    this.context = context;
  }

  public CustomDialog(Context context, int theme) {
    super(context, theme);
    this.context = context;
  }

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.custom_dialog);

    title = (TextView) findViewById(R.id.title);
    if(titleRexourceID != 0){
       title.setText(titleRexourceID);
    }

    close = findViewById(R.id.close);
    close.setOnClickListener(new Button.OnClickListener() {
      public void onClick(View v) {
        dismiss();
      }
    });

    actions = findViewById(R.id.actions);
    divider = findViewById(R.id.divider);

    positive = (Button) findViewById(R.id.positive);
    negative = (Button) findViewById(R.id.negative);

    if (positiveOnClickListener != null && negativeOnClickListener != null) {
      divider.setVisibility(View.VISIBLE);
    }

    if (positiveOnClickListener != null) {
      actions.setVisibility(View.VISIBLE);
      positive.setVisibility(View.VISIBLE);
      positive.setText(positiveTextResourceID);
      positive.setOnClickListener(positiveOnClickListener);
    }

    if (negativeOnClickListener != null) {
      actions.setVisibility(View.VISIBLE);
      negative.setVisibility(View.VISIBLE);
      negative.setText(negativeTextResourceID);
      negative.setOnClickListener(negativeOnClickListener);
    }

  }

  public void setTitle(int rid) {
    this.titleRexourceID = rid;
  }

  public void setPositiveButton(int rid, Button.OnClickListener positiveOnClickListener) {
    this.positiveTextResourceID = rid;
    this.positiveOnClickListener = positiveOnClickListener;
  }

  public void setNegativeButton(int rid, Button.OnClickListener negativeOnClickListener) {
    this.negativeTextResourceID = rid;
    this.negativeOnClickListener = negativeOnClickListener;
  }

  // called when this dialog is dismissed
  // protected void onStop() {
  // }

}
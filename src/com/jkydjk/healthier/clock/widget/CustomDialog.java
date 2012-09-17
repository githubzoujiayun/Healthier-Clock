package com.jkydjk.healthier.clock.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.util.Log;

public class CustomDialog extends Dialog {
  
  public static CustomDialog accountDialog(Context context){
    CustomDialog dialog = new CustomDialog(context);
    dialog.setContentView(R.layout.account_dialog);
    return dialog;
  }
  

   private View close;

  Context context;

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

    close = findViewById(R.id.close);
    close.setOnClickListener(new Button.OnClickListener() {
      public void onClick(View v) {
        dismiss();
      }
    });

  }
  
  // called when this dialog is dismissed
  protected void onStop() {
    Log.v("TAG");
  }

}
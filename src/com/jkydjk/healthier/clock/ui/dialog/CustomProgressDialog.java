package com.jkydjk.healthier.clock.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.jkydjk.healthier.clock.R;

/**
 * Custom Progress Dialog
 * 
 * @author miclle
 * 
 */
public class CustomProgressDialog extends Dialog {

  String message;

  int messageResourceID = -1;

  private TextView messageTextView;

  public CustomProgressDialog(Context context) {
    super(context, R.style.CustomDialog);
  }

  public CustomProgressDialog(Context context, int theme) {
    super(context, theme);
  }

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.custom_progress_dialog);

    messageTextView = (TextView) findViewById(R.id.text_view_message);

    if (messageResourceID != -1) {
      messageTextView.setText(messageResourceID);
    } else if (message != null) {
      messageTextView.setText(message);
    }

  }

  /**
   * Start loading animation
   */
  public void onWindowFocusChanged(boolean hasFocus) {
    ImageView imageView = (ImageView) findViewById(R.id.image_view_loading);
    AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
    animationDrawable.start();
  }

  /**
   * Set message
   * 
   * @param message
   * @return
   */
  public CustomProgressDialog setMessage(String message) {
    this.message = message;
    if (messageTextView != null)
      messageTextView.setText(message);
    return this;
  }

  /**
   * Set message
   * 
   * @param messageResourceID
   * @return
   */
  public CustomProgressDialog setMessage(int messageResourceID) {
    this.messageResourceID = messageResourceID;
    if (messageTextView != null)
      messageTextView.setText(messageResourceID);
    return this;
  }

}
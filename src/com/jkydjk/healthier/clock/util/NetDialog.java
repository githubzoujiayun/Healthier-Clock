package com.jkydjk.healthier.clock.util;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

public class NetDialog {

  public static Dialog getNetDialog(Context context) {

    Builder builder = new Builder(context);
    builder.setTitle("提示");
    builder.setMessage("无法获取数据，您现在的无线网络可能有问题哦");
    builder.setPositiveButton("确定", new android.app.AlertDialog.OnClickListener() {

      public void onClick(DialogInterface dialog, int which) {

      }
    });

    builder.setNegativeButton("取消", new android.app.AlertDialog.OnClickListener() {

      public void onClick(DialogInterface dialog, int which) {
      }
    });

    return builder.create();
  }

}

package com.jkydjk.healthier.clock.widget;

import com.jkydjk.healthier.clock.Log;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;

public class FileView extends HorizontalScrollView {

    private View mainView;

    public FileView(Context context) {
        super(context);
        init(context);
    }

    public FileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FileView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setHorizontalFadingEdgeEnabled(false);
        setVerticalFadingEdgeEnabled(false);
    }

    // @Override
    // public boolean onTouchEvent(MotionEvent ev) {
    // // Log.v(mainView.getId()+"");
    // return true;
    // }
    //
    // @Override
    // public boolean onInterceptTouchEvent(MotionEvent ev) {
    // // Log.v(mainView.getId()+"");
    // return true;
    // }

    public void setMainView(final FilePage child) {
        final ViewGroup parent = (ViewGroup) getChildAt(0);
        mainView = child;
        child.hideShadow();
        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                final HorizontalScrollView me = FileView.this;
                me.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                parent.addView(child, me.getMeasuredWidth(), me.getMeasuredHeight());
            }
        });
    }

    public void appendView(FilePage child) {
        ViewGroup parent = (ViewGroup) getChildAt(0);
        parent.addView(child);
        OnGlobalLayoutListener listener = new MyOnGlobalLayoutListener(parent, child);
        getViewTreeObserver().addOnGlobalLayoutListener(listener);
    }

    class MyOnGlobalLayoutListener implements OnGlobalLayoutListener {
        private ViewGroup parent;
        private FilePage child;
        private int scrollToViewIdx;
        private int scrollToViewPos = 0;

        public MyOnGlobalLayoutListener(ViewGroup parent, FilePage child) {
            this.parent = parent;
            this.child = child;
        }

        public void onGlobalLayout() {
            final HorizontalScrollView me = FileView.this;
            me.getViewTreeObserver().removeGlobalOnLayoutListener(this);

            final int w = me.getMeasuredWidth();
            final int h = me.getMeasuredHeight();

            scrollToViewPos = 0;

            parent.removeView(child);
            parent.addView(child, w - 30, h);

            // parent.addView(child, params);

            // if (i < scrollToViewIdx) {
            // scrollToViewPos += dims[0];
            // }

            // For some reason we need to post this action, rather than call
            // immediately.
            // If we try immediately, it will not scroll.

        }
    }

    public void scrollTo(FilePage filePage) {
        
        Log.v(filePage.getId()+"");
        
        View view = findViewById(filePage.getId());
        
//        final HorizontalScrollView me = FileView.this;
//
//        new Handler().post(new Runnable() {
//            public void run() {
////                me.scrollBy(scrollToViewPos, 0);
//            }
//        });

    }

}

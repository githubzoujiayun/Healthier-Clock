package com.jkydjk.healthier.clock.widget;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;

public class FileFlipBook extends HorizontalScrollView {

    private float SWIPE_VERT_MIN_DISTANCE = 30;
    private float startlocation = 0;
    private float diffX = 0;
    private boolean vertScroll = false;

    private Map<Integer, View> pages = new HashMap<Integer, View>();

    public FileFlipBook(Context context) {
        super(context);
        init(context);
    }

    public FileFlipBook(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FileFlipBook(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setHorizontalFadingEdgeEnabled(false);
        setVerticalFadingEdgeEnabled(false);
    }

    // @Override
    // public boolean onTouchEvent(MotionEvent ev) {
    // return false;
    // }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            startlocation = ev.getX();
            vertScroll = false;
            break;
        case MotionEvent.ACTION_MOVE:
            diffX = Math.abs(startlocation - ev.getX());
            if (diffX > SWIPE_VERT_MIN_DISTANCE)
                vertScroll = true;
            break;
        case MotionEvent.ACTION_UP:
            vertScroll = false;
            break;
        }
        
        return vertScroll;
    }

    public void appendPage(final FilePage filePage, final boolean scrollTo) {
        final ViewGroup parent = (ViewGroup) getChildAt(0);

        final int pageNumber = filePage.getId();

        if (pageNumber == 0) {
            parent.removeAllViews();
            pages.clear();
            filePage.hideShadow();
        }

        parent.addView(filePage);

        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                final HorizontalScrollView me = FileFlipBook.this;
                me.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                parent.removeView(filePage);
                int offset = pageNumber == 0 ? 0 : 30;

                if (pages.get(pageNumber) != null) {
                    for (int i = pageNumber; i < pages.size() + 1; i++) {
                        parent.removeView(pages.get(i));
                    }
                }

                final int w = me.getMeasuredWidth();
                final int h = me.getMeasuredHeight();

                parent.addView(filePage, w - offset, h);
                pages.put(pageNumber, filePage);

                if (scrollTo) {
                    new Handler().post(new Runnable() {
                        public void run() {
                            smoothScrollTo((w - 30) * pageNumber, 0);
                        }
                    });
                }
            }
        });

    }

    public void scrollToPage(int pageNumber) {
        // final int w = getMeasuredWidth();
        // final int h = getMeasuredHeight();
        ViewGroup parent = (ViewGroup) getChildAt(0);
        if (pages.get(pageNumber) != null) {
            for (int i = pageNumber; i < pages.size() + 1; i++) {
                parent.removeView(pages.get(i));
            }
        }
    }

    /**
     * This is called in response to an internal scroll in this view (i.e., the
     * view scrolled its own contents). This is typically as a result of
     * {@link #scrollBy(int, int)} or {@link #scrollTo(int, int)} having been
     * called.
     * 
     * @param l
     *            Current horizontal scroll origin.
     * @param t
     *            Current vertical scroll origin.
     * @param oldl
     *            Previous horizontal scroll origin.
     * @param oldt
     *            Previous vertical scroll origin.
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //
        // Log.v("Scrolling  X from [" + oldl + "] to [" + l + "]");
        //
        // if (Math.abs(l - oldl) <= 50) {
        // // mScrollable = false;
        // } else {
        // // mScrollable = false;
        // }
        // return;
    }

}

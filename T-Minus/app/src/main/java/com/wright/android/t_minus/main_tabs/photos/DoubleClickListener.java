package com.wright.android.t_minus.main_tabs.photos;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;

public abstract class DoubleClickListener implements View.OnClickListener {

    private static final long DEFAULT_QUALIFICATION_SPAN = 200;
    private final long doubleClickQualificationSpanInMillis;
    private long timestampLastClick;
    private boolean mHasDoubleClicked = false;

    public DoubleClickListener() {
        doubleClickQualificationSpanInMillis = DEFAULT_QUALIFICATION_SPAN;
        timestampLastClick = 0;
    }

    public DoubleClickListener(long doubleClickQualificationSpanInMillis) {
        this.doubleClickQualificationSpanInMillis = doubleClickQualificationSpanInMillis;
        timestampLastClick = 0;
    }

    @Override
    public void onClick(View view) {
        if ((SystemClock.elapsedRealtime() - timestampLastClick) < doubleClickQualificationSpanInMillis) {
            onDoubleClick(view);
            mHasDoubleClicked = true;
        }else {
            mHasDoubleClicked = false;
            final Handler myHandler = new doubleClickHandler(view);
            myHandler.sendMessageDelayed(new Message(), doubleClickQualificationSpanInMillis);
        }
        timestampLastClick = SystemClock.elapsedRealtime();
    }

    public abstract void onDoubleClick(View view);

    public abstract void onSingleClick(View view);


    private class doubleClickHandler extends Handler{
        private final View view;

        doubleClickHandler(View view) {
            this.view = view;
        }

        public void handleMessage(Message m) {
            if (!mHasDoubleClicked) {
                onSingleClick(view);
            }
        }
    }
}

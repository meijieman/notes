package com.hongfans.demo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 横屏时点击 EditText 弹出框不会触发 onSizeChanged
 */
public class KeyboardChangeLinearLayout extends LinearLayout {

    private OnKeyboardChangeListener mListener;

    public KeyboardChangeLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyboardChangeLinearLayout(Context context) {
        super(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mListener != null) {
            if (oldh > h) {
                mListener.onShow();
            } else {
                mListener.onHidden();
            }
        }
    }

    public void setOnKeyboardChangeListener(OnKeyboardChangeListener listener) {
        mListener = listener;
    }

    public interface OnKeyboardChangeListener {

        void onShow();

        void onHidden();
    }
}
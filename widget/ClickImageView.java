package com.hongfans.customview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

/**
 * 缩放的 ImageView
 */
public class ClickImageView extends ImageView {

    public static final float SCALE = 0.8f;
    public static final int DURATION = 200;


    private Animator mAnimatorIn;
    private Animator mAnimatorOut;

    private ClickListener mListener;

    public ClickImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        PropertyValuesHolder scaleInX = PropertyValuesHolder.ofFloat("scaleX", 1f, SCALE);
        PropertyValuesHolder scaleInY = PropertyValuesHolder.ofFloat("scaleY", 1f, SCALE);
        mAnimatorIn = ObjectAnimator.ofPropertyValuesHolder(this, scaleInX, scaleInY);
        mAnimatorIn.setDuration(DURATION);
        mAnimatorIn.setInterpolator(new LinearInterpolator());

        PropertyValuesHolder scaleOutX = PropertyValuesHolder.ofFloat("scaleX", SCALE, 1f);
        PropertyValuesHolder scaleOutY = PropertyValuesHolder.ofFloat("scaleY", SCALE, 1f);
        mAnimatorOut = ObjectAnimator.ofPropertyValuesHolder(this, scaleOutX, scaleOutY);
        mAnimatorOut.setDuration(DURATION);
        mAnimatorOut.setInterpolator(new LinearInterpolator());
    }

    /**
     * 使用 setClickListener 代替 setOnClickListener
     */
    @Deprecated
    @Override
    public void setOnClickListener(OnClickListener l) {
//        super.setOnClickListener(l);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!canScale) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mAnimatorOut.end();
                mAnimatorIn.start();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mAnimatorIn.end();
                mAnimatorOut.start();
                if (mListener != null) {
                    mListener.onClick();
                }
                break;
        }

        return true;
    }

    private boolean canScale = true;

    public void setCanScale(boolean canScale) {
        this.canScale = canScale;
    }

    public void setClickListener(ClickListener clickListener) {
        mListener = clickListener;
    }

    /**
     * 点击事件处理回调
     */
    public interface ClickListener {

        void onClick();
    }
}

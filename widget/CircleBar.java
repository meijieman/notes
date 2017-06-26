package com.hongfans.demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class CircleBar extends View {

    private RectF mColorWheelRectangle = new RectF(); // 圆圈的矩形范围
    private Paint mDefaultWheelPaint; // 绘制底部灰色圆圈的画笔
    private Paint mColorWheelPaint; // 绘制蓝色扇形的画笔
    private Paint mTextPaint; // 中间文字的画笔
    private float mColorWheelRadius; //  圆圈普通状态下的半径
    private float circleStrokeWidth; // 圆圈的线条粗细
    private float pressExtraStrokeWidth; // 按下状态下增加的圆圈线条增加的粗细
    private String mText = "0"; // 中间文字内容

    private float mSweepAngle; // 旋转过角度
    private float mMaxAngle; // 最大角度 [0, 360]
    private int mTextSize;
    private BarAnimation anim;
    private Rect mBounds = new Rect(); // 接收中间文本的高宽

    public CircleBar(Context context) {
        super(context);
        init();
    }

    public CircleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        circleStrokeWidth = dip2px(getContext(), 2);
        pressExtraStrokeWidth = dip2px(getContext(), 2);
        mTextSize = dip2px(getContext(), 40);

        mColorWheelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mColorWheelPaint.setColor(0xFF29a6f6);
        mColorWheelPaint.setStyle(Paint.Style.STROKE);
        mColorWheelPaint.setStrokeWidth(circleStrokeWidth);

        mDefaultWheelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDefaultWheelPaint.setColor(0xFFeeefef);
        mDefaultWheelPaint.setStyle(Paint.Style.STROKE);
        mDefaultWheelPaint.setStrokeWidth(circleStrokeWidth);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        mTextPaint.setColor(0xFF333333);
        mTextPaint.setStyle(Style.FILL_AND_STROKE);
        mTextPaint.setTextAlign(Align.LEFT);
        mTextPaint.setTextSize(mTextSize);

        anim = new BarAnimation();
        anim.setDuration(2000); // 设置默认时长
        anim.setInterpolator(new FastOutLinearInInterpolator());// 设置插值器
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(mColorWheelRectangle, 90, 360, false, mDefaultWheelPaint);
        canvas.drawArc(mColorWheelRectangle, 90, mSweepAngle, false, mColorWheelPaint);
        mTextPaint.getTextBounds(mText, 0, mText.length(), mBounds);
        // mTextPaint.measureText(mText) 获取 mText 的宽度
        canvas.drawText(mText,
                mColorWheelRectangle.centerX() - mBounds.width() / 2,
                mColorWheelRectangle.centerY() + mBounds.height() / 2,
                mTextPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        mColorWheelRadius = min - circleStrokeWidth - pressExtraStrokeWidth;
        mColorWheelRectangle.set(circleStrokeWidth + pressExtraStrokeWidth,
                circleStrokeWidth + pressExtraStrokeWidth,
                mColorWheelRadius,
                mColorWheelRadius);
    }

    @Override
    public void setPressed(boolean pressed) {
        print("call setPressed ");
        if (pressed) {
            mColorWheelPaint.setColor(0xFF165da6);
            mTextPaint.setColor(0xFF070707);
            mColorWheelPaint.setStrokeWidth(circleStrokeWidth + pressExtraStrokeWidth);
            mDefaultWheelPaint.setStrokeWidth(circleStrokeWidth + pressExtraStrokeWidth);
            mTextPaint.setTextSize(mTextSize - pressExtraStrokeWidth * 2);
        } else {
            mColorWheelPaint.setColor(0xFF29a6f6);
            mTextPaint.setColor(0xFF333333);
            mColorWheelPaint.setStrokeWidth(circleStrokeWidth);
            mDefaultWheelPaint.setStrokeWidth(circleStrokeWidth);
            mTextPaint.setTextSize(mTextSize);
        }
        super.setPressed(pressed);
        invalidate();
    }

    public void startCustomAnimation() {
        mSweepAngle = 0;

        startAnimation(anim);
    }

    public void setDuration(long duration) {
        anim.setDuration(duration);
    }

    public void scaleCurrentDuration(float scale) {
        anim.scaleCurrentDuration(scale);
    }

    public long getCurrentDuration() {
        return anim.getDuration();
    }

    /**
     * 设置中心文本
     */
    public void setText(String text) {
        mText = text;
        postInvalidate();
    }

    /**
     * 设置旋转最大值
     *
     * @param angle [0, 360]
     */
    public void setAngle(float angle) {
        if (angle > 360.0f) {
            mMaxAngle = 360.0f;
        } else if (mMaxAngle < 0.0f) {
            mMaxAngle = 0.0f;
        }

        mMaxAngle = angle;
    }

    private class BarAnimation extends Animation {

        private float mInterpolatedTime; // 避免多次调用 onEnd()

        public BarAnimation() {

        }

        // interpolatedTime (从0到1)
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (mInterpolatedTime != interpolatedTime) {
                mSweepAngle = interpolatedTime * mMaxAngle;
                mText = (int) mSweepAngle + "";
                if (mListener != null) {
                    mListener.onUpdate(mSweepAngle);
                }
                if (interpolatedTime == 1.0f) {
                    if (mListener != null) {
                        mListener.onEnd(mSweepAngle);
                    }
                }
                postInvalidate();
            }
            mInterpolatedTime = interpolatedTime;
        }
    }

    private static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private OnUpdateListener mListener;

    public void setOnUpdateListener(OnUpdateListener listener) {
        mListener = listener;
    }

    public interface OnUpdateListener {
        /**
         * 进度
         *
         * @param sweepAngle 扫面过的角度
         */
        void onUpdate(float sweepAngle);

        /**
         * 扫描完成
         */
        void onEnd(float sweepAngle);
    }

    private void print(String msg) {
        Log.e("beauty_a", msg);
    }
}

package com.hongfans.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Administrator on 2016/11/14.
 */
public class MicView extends View {

    private static final float VALUE_MIN = 0.3f;
    private static final int DURATION = 300;
    private int DISTANCE_OF_LINE_MAX = 80;// 竖线的最大高度


    private int mLineGap = 20; // 竖线之间的间隔
    private int mLines = 4; // 总线条数
    private int[] mDistance = new int[mLines]; // 线段到y轴的距离


    private int mStartX; // x 起点的位置
    private int mMiddleY; // Y 起点所在线的中点的位置

    private int mIndex;// 最后一个开始动画的点

    private boolean isReverse;// 是否是反转

    private boolean isFirst = true;

    private Paint mPaint;

    public static final int STATE_STOP = 0;
    public static final int STATE_LISTENER = 1;
    public static final int STATE_PARSER = 2;

    @IntDef({STATE_STOP, STATE_LISTENER, STATE_PARSER})
    @Retention(RetentionPolicy.SOURCE)
    @interface State {

    }

    private @State int mState;

    public MicView(Context context) {
        super(context);
        init();
    }

    public MicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public void startAnim1() {
        if (mState == STATE_LISTENER) {
            return;
        }
        isFirst = true;
        isReverse = false;
        mIndex = 0;
        mState = STATE_LISTENER;
        invalidate();
    }

    public void startAnim2() {
        if (mState == STATE_PARSER) {
            return;
        }
        isFirst = true;
        isReverse = false;
        mState = STATE_PARSER;
        invalidate();
    }

    public void stop() {
        mState = STATE_STOP;
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.tb_bg_selected));
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(8);// 线宽
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getWidth();
        int height = getHeight();
        print("控件宽度 " + width + " 控件高度 " + height);

        if (width > 0) {
            if (mLines <= 0) {
                throw new IllegalArgumentException("mLines should bigger than 0");
            }
            if (mLines / 2 == 0) {
                mStartX = (int) ((width - mLines * mLineGap + mLineGap) * 1.0f / 2);
            } else {
                mStartX = (int) (width * 1.0f / 2 - (mLines / 2 * mLineGap));
            }

            mMiddleY = height / 2;
            print("起点 x " + mStartX + " 中点 Y " + mMiddleY);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT); // 绘制背景色

        // 坐标轴
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(1);
        canvas.drawLine(0f, getHeight() / 2, getWidth(), getHeight() / 2, paint); // x
        canvas.drawLine(getWidth() / 2, 0f, getWidth() / 2, getHeight(), paint); // y

        if (mState == STATE_LISTENER) {
            animate1(canvas);
        } else if (mState == STATE_PARSER) {
            animate2(canvas);
        }

        postDelayed(new Runnable() {
            @Override
            public void run() {
                mIndex++;
                if (mIndex > mLines) {
                    mIndex = 0;
                    isReverse = !isReverse;
                }

                postInvalidate();
            }
        }, DURATION);
    }

    // 听语音中
    private void animate1(Canvas canvas) {
        if (isFirst) {
            isFirst = false;
            // 初始化
            for (int i = 0; i < mLines; i++) {
                mDistance[i] = (int) (DISTANCE_OF_LINE_MAX * VALUE_MIN);
            }
        }

        for (int i = 0; i < mLines; i++) {
            if (isReverse) {
                if (mIndex < i) {
                    mDistance[i] = (int) (DISTANCE_OF_LINE_MAX * VALUE_MIN);
                } else {
                    mDistance[i] = DISTANCE_OF_LINE_MAX;
                }
            } else {
                if (mIndex >= i) {
                    mDistance[i] = (int) (DISTANCE_OF_LINE_MAX * VALUE_MIN);
                } else {
                    mDistance[i] = DISTANCE_OF_LINE_MAX;
                }
            }
        }

        for (int i = 0; i < mLines; i++) {
            int x = mStartX + i * mLineGap;
            drawListenLine(canvas, x, mMiddleY - mDistance[i], mMiddleY + mDistance[i]);
        }
    }

    // 解析中
    private void animate2(Canvas canvas) {
        if (isFirst) {
            isFirst = false;
            for (int i = 0; i < mLines; i++) {
                // 初始化


            }
        }

        for (int i = 0; i < mLines; i++) {
            int x = mStartX + i * mLineGap;
            canvas.drawLine(x, mMiddleY - mDistance[i], x, mMiddleY + mDistance[i], mPaint);
        }
    }


    private int getNextValue2(int src) {
        if (isReverse) {
            src += 2;
            if (src <= DISTANCE_OF_LINE_MAX) {
                return src;
            } else {
                return (int) (DISTANCE_OF_LINE_MAX * VALUE_MIN);
            }
        } else {
            src -= 2;
            if (src >= DISTANCE_OF_LINE_MAX * VALUE_MIN) {
                return src;
            } else {
                return (int) (DISTANCE_OF_LINE_MAX * VALUE_MIN);
            }
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        print("onSaveInstanceState");

        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        print("onRestoreInstanceState");
    }

    private void drawListenLine(Canvas canvas, int x, int startY, int endY) {
        canvas.drawLine(x, startY, x, endY, mPaint);
    }

    private static final String TAG = "beauty";

    public void print(String msg) {
        Log.e(TAG, msg);
    }
}

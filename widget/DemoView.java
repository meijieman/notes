package com.foo.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Calendar;
import java.util.Random;

/**
 * @desc: TODO
 * @author: Major
 * @since: 2016/11/14 22:47
 */

public class DemoView extends View {

    public static final int STATE_CANCEL = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_PARSE  = 2;

    private static final String TAG      = "beauty_p";
    public static final  int    DURATION = 400;

    private int DISTANCE_OF_LINE_MAX = 80;// 竖线的最大高度

    private ValueAnimator mIncreaseAnimator;
    private ValueAnimator mReduceAnimator;

    private Paint mPaint;

    private int   mLines    = 15; // 总线条数
    private int   mLineGap  = 20; // 竖线之间的间隔
    private int[] mDistance = new int[mLines]; // 线段到y轴的距离

    private int mIndex;

    private int mStartX; // x 起点的位置
    private int mMiddleY; // Y 起点所在线的中点的位置
    private int mLatestValue;


    public DemoView(Context context) {
        super(context);
        init();
    }

    public DemoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        mPaint = new Paint();
//        mPaint.setColor(getResources().getColor(R.color.tb_bg_selected));
        mPaint.setColor(Color.GREEN);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(8);// 线宽
        mPaint.setStyle(Paint.Style.STROKE);

        mReduceAnimator = ValueAnimator.ofInt(5, 2); // 使用 5，2 为减少 cpu 消耗
        mReduceAnimator.setDuration(DURATION);
        mReduceAnimator.setInterpolator(new FastOutLinearInInterpolator());
        mReduceAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mReduceAnimator.setRepeatMode(ValueAnimator.RESTART); // 一直从大到小
        mReduceAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int tmp = (int)animation.getAnimatedValue();
                if (tmp == mLatestValue) {
                    return;
                }

                if (tmp < mLatestValue) {
                    // 说明在减少
                    mDistance[mIndex] = (int)(DISTANCE_OF_LINE_MAX * tmp * 0.2f);
                } else {
                    // 该绘制下一个了
                    mDistance[mIndex] = (int)(DISTANCE_OF_LINE_MAX * 0.4f);
                    mIndex++;
                    if (mIndex >= mLines) {
                        mIndex = 0;
                        mReduceAnimator.cancel();
                        mIncreaseAnimator.start();
                        return;
                    }
                    mDistance[mIndex] = (int)(DISTANCE_OF_LINE_MAX * tmp * 0.2f);
                }
                mLatestValue = tmp;
                invalidate();
            }
        });

        mIncreaseAnimator = ValueAnimator.ofInt(2, 5);
        mIncreaseAnimator.setDuration(DURATION);
        mIncreaseAnimator.setInterpolator(new LinearOutSlowInInterpolator());
        mIncreaseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mIncreaseAnimator.setRepeatMode(ValueAnimator.RESTART); // 一直从小到大
        mIncreaseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int tmp = (int)animation.getAnimatedValue();
                if (tmp == mLatestValue) {
                    return;
                }
                if (tmp > mLatestValue) {
                    // 说明在增加
                    mDistance[mIndex] = (int)(DISTANCE_OF_LINE_MAX * tmp * 0.2f);
                    for (int i = 0; i < mIndex; i++) {
                        mDistance[i] = DISTANCE_OF_LINE_MAX;
                    }
                } else {
                    // 该绘制下一个了
                    mDistance[mIndex] = DISTANCE_OF_LINE_MAX;
                    mIndex++;
                    if (mIndex >= mLines) {
                        mIndex = 0;
                        mIncreaseAnimator.cancel();
                        mReduceAnimator.start();
                        return;
                    }
                    mDistance[mIndex] = (int)(DISTANCE_OF_LINE_MAX * tmp * 0.2f);
                    for (int i = 0; i < mIndex; i++) {
                        mDistance[i] = DISTANCE_OF_LINE_MAX;
                    }
                }
                mLatestValue = tmp;
                invalidate();

            }
        });
    }


    public void startParserAnimate() {

        cancel();

        // 初始化
        for (int i = 0; i < mLines; i++) {
            mDistance[i] = DISTANCE_OF_LINE_MAX;
        }
        mReduceAnimator.start();
    }

    private Random mRandom = new Random();

    private boolean[] isReduces = new boolean[mLines];
    private Handler   mHandler  = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            for (int i = 0; i < mLines; i++) {
                if (isReduces[i]) {
                    int tmp = mDistance[i] - 8;
                    if (tmp >= DISTANCE_OF_LINE_MAX * 0.4f) {
                        mDistance[i] = tmp;
                    } else {
                        mDistance[i] = (int)(DISTANCE_OF_LINE_MAX * 0.4f);
                        isReduces[i] = false;
                    }
                } else {
                    int tmp = mDistance[i] + 8;

                    if (tmp <= DISTANCE_OF_LINE_MAX) {
                        mDistance[i] = tmp;
                    } else {
                        mDistance[i] = DISTANCE_OF_LINE_MAX;
                        isReduces[i] = true;
                    }
                }
            }
            sendEmptyMessageDelayed(1, 300);

            invalidate();
        }
    };

    public void startListenerAnimate() {
        cancel();
        // 初始化
        for (int i = 0; i < mLines; i++) {
            if (i % 3 == 1) {
                mDistance[i] = DISTANCE_OF_LINE_MAX;
                isReduces[i] = true;
            } else {
                mDistance[i] =  (int)(DISTANCE_OF_LINE_MAX * (mRandom.nextInt(3) + 4) * 0.1f);
                isReduces[i] = false;
            }
        }

        mHandler.sendEmptyMessageDelayed(1, 300);
    }

    public void cancel() {

        mHandler.removeCallbacksAndMessages(null);

        if (mIncreaseAnimator.isRunning()) {
            mIncreaseAnimator.cancel();
        }
        if (mReduceAnimator.isRunning()) {
            mReduceAnimator.cancel();
        }
        // 恢复初始化
        for (int i = 0; i < mLines; i++) {
            mDistance[i] = DISTANCE_OF_LINE_MAX;
        }
        mIndex = 0;
        mLatestValue = 0;
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

            mStartX = (int)((width - mLineGap * (mLines - 1)) * 1.0f / 2);

            mMiddleY = height / 2;
            print("起点 x " + mStartX + " 中点 Y " + mMiddleY);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        print("onDraw: " + Calendar.getInstance().getTime());
//        canvas.drawColor(Color.TRANSPARENT); // 绘制背景色

        // 坐标轴
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(1);
        canvas.drawLine(0f, getHeight() / 2, getWidth(), getHeight() / 2, paint); // x
        canvas.drawLine(getWidth() / 2, 0f, getWidth() / 2, getHeight(), paint); // y

        for (int i = 0; i < mLines; i++) {
            int x = mStartX + i * mLineGap;
            drawListenLine(canvas, x, mMiddleY - mDistance[i], mMiddleY + mDistance[i]);
        }
    }

    private void drawListenLine(Canvas canvas, int x, int startY, int endY) {
        canvas.drawLine(x, startY, x, endY, mPaint);
    }

    public void print(String msg) {
        Log.e(TAG, msg);
    }
}

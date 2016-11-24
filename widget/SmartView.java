package com.hongfans.cvi.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.hongfans.cvi.R;
import com.hongfans.cvi.utils.DebugHelp;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc: 麦克风动画
 * @author: Major
 * @since: 2016/11/14 22:47
 */
public class SmartView extends View {

    public static final int STATE_CANCEL = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_PARSE = 2;

    private static final int PARSER_DURATION = 60; // 60
    private static final int LISTEN_DURATION = 400;
    private static final float HEIGHT_SCALE = 0.2f; // 0.2 * 5 = 1

    private int mLineGap = 20; // 竖线之间的间隔
    private int mLines = 15; // 总线条数
    private int mMaxHeightOfLine = 50;// 竖线的最大高度

    private ValueAnimator mIncreaseAnimator;
    private ValueAnimator mReduceAnimator;

    private int mIndex;
    private int mState;
    private Paint mPaint;

    private int[] mDistance = new int[mLines]; // 线段到y轴的距离
    private List<ValueAnimator> mAnimatorList = new ArrayList<>(mLines);

    private int[] mDelayArr = {100, 400, 160, 200, 350, 120, 140, 300, 130, 180, 550, 110, 220, 380, 160}; // 动画延时

    private int mStartX; // x 起点的位置
    private int mMiddleY; // Y 起点所在线的中点的位置
    private int mLatestValue;

    public SmartView(Context context) {
        super(context);
        init(context, null);
    }

    public SmartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        print("init");
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
//            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SmartView);
        mMaxHeightOfLine = (int) typedArray.getDimension(R.styleable.SmartView_sv_max_height, mMaxHeightOfLine);
        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.tb_bg_selected));
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(3);// 线宽

        mReduceAnimator = ValueAnimator.ofInt(5, 2); // 使用 5，2 为减少 cpu 消耗
        mReduceAnimator.setDuration(PARSER_DURATION);
        mReduceAnimator.setInterpolator(new FastOutLinearInInterpolator());
        mReduceAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mReduceAnimator.setRepeatMode(ValueAnimator.RESTART); // 一直从大到小
        mReduceAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int tmp = (int) animation.getAnimatedValue();
                if (tmp == mLatestValue) {
                    return;
                }
                if (tmp < mLatestValue) {
                    // 说明在减少
                    mDistance[mIndex] = (int) (mMaxHeightOfLine * tmp * HEIGHT_SCALE);
                } else {
                    // 该绘制下一个了
                    mDistance[mIndex] = (int) (mMaxHeightOfLine * HEIGHT_SCALE);
                    mIndex++;
                    if (mIndex >= mLines) {
                        mIndex = 0;
                        mReduceAnimator.cancel();
                        mIncreaseAnimator.start();
                        return;
                    }
                    mDistance[mIndex] = (int) (mMaxHeightOfLine * tmp * HEIGHT_SCALE);
                }
                mLatestValue = tmp;

                invalidate();
            }
        });

        if (mIncreaseAnimator == null) {
            mIncreaseAnimator = ValueAnimator.ofInt(2, 5);
            mIncreaseAnimator.setDuration(PARSER_DURATION);
            mIncreaseAnimator.setInterpolator(new LinearOutSlowInInterpolator());
            mIncreaseAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mIncreaseAnimator.setRepeatMode(ValueAnimator.RESTART); // 一直从小到大
            mIncreaseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int tmp = (int) animation.getAnimatedValue();
                    if (tmp == mLatestValue) {
                        return;
                    }
                    if (tmp > mLatestValue) {
                        // 说明在增加
                        mDistance[mIndex] = (int) (mMaxHeightOfLine * tmp * HEIGHT_SCALE);
                        for (int i = 0; i < mIndex; i++) {
                            mDistance[i] = mMaxHeightOfLine;
                        }
                    } else {
                        // 该绘制下一个了
                        mDistance[mIndex] = mMaxHeightOfLine;
                        mIndex++;
                        if (mIndex >= mLines) {
                            mIndex = 0;
                            mIncreaseAnimator.cancel();
                            mReduceAnimator.start();
                            return;
                        }
                        mDistance[mIndex] = (int) (mMaxHeightOfLine * tmp * HEIGHT_SCALE);
                        for (int i = 0; i < mIndex; i++) {
                            mDistance[i] = mMaxHeightOfLine;
                        }
                    }
                    mLatestValue = tmp;

                    invalidate();
                }
            });
        }
    }

    public void startParserAnimate(boolean isRotate) {
        if (getVisibility() != VISIBLE) {
            return;
        }
        if (!isRotate) {
            if (mState == STATE_PARSE) {
                return;
            }
            cancel();

            mState = STATE_PARSE;
        }

        // 初始化
        for (int i = 0; i < mLines; i++) {
            mDistance[i] = mMaxHeightOfLine;
        }
        mIndex = 0;
        mLatestValue = 0;
        mReduceAnimator.start();
    }

    /**
     * 开始解析动画
     */
    public void startParserAnimate() {
        startParserAnimate(false);
    }

    private void startListenerAnimate(boolean isRotate) {
        if (getVisibility() != VISIBLE) {
            return;
        }
        if (!isRotate) {
            if (mState == STATE_LISTEN) {
                return;
            }
            cancel();

            mState = STATE_LISTEN;
        }

        // 初始化
        for (int i = 0; i < mLines; i++) {
            mDistance[i] = mMaxHeightOfLine;

            ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.4f);
            animator.setDuration(LISTEN_DURATION);
            animator.setStartDelay(mDelayArr[i]);
            animator.setRepeatCount(-1);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            final int finalI = i;
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float animatedValue = (float) animation.getAnimatedValue();
                    mDistance[finalI] = (int) (mMaxHeightOfLine * animatedValue);
                    invalidate();
                }
            });
            mAnimatorList.add(animator);
        }

        for (ValueAnimator animator : mAnimatorList) {
            animator.start();
        }
    }

    /**
     * 开始监听动画
     */
    public void startListenerAnimate() {
        startListenerAnimate(false);
    }

    /**
     * 取消动画
     */
    public synchronized void cancel() {
//        print("cancel mState " + mState);
        if (mState == STATE_CANCEL) {
            return;
        } else if (mState == STATE_LISTEN) {
            for (ValueAnimator animator : mAnimatorList) {
                animator.cancel();
            }
        } else {
            if (mIncreaseAnimator.isRunning()) {
                mIncreaseAnimator.cancel();
            }
            if (mReduceAnimator.isRunning()) {
                mReduceAnimator.cancel();
            }
        }

        mState = STATE_CANCEL;
    }

    /**
     * 判断动画是否在运行
     *
     * @return
     */
    public synchronized boolean isRunning() {
//        print("isRunning state " + mState);
        return mState != STATE_CANCEL;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int height = getHeight();
        print("onLayout mMiddleY " + mMiddleY);
        if (height != 0 && mMiddleY != height / 2) {
            int width = getWidth();
            print("onLayout 控件宽度 " + width + " 控件高度 " + height);
            if (width > 0) {
                if (mLines <= 0) {
                    throw new IllegalArgumentException("mLines should bigger than 0");
                }

                mStartX = (int) ((width - mLineGap * (mLines - 1)) * 1.0f / 2);

                mMiddleY = height / 2;
                print("onLayout 起点 x " + mStartX + " 中点 Y " + mMiddleY);
            }
        } else {
            print("onLayout mMiddleY " + mMiddleY);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isRunning()) {
            return;
        }
        super.onDraw(canvas);
//        canvas.drawColor(Color.TRANSPARENT); // 绘制背景色

        // 坐标轴
//        Paint paint = new Paint();
//        paint.setColor(Color.RED);
//        paint.setStrokeWidth(1);
//        canvas.drawLine(0f, getHeight() / 2, getWidth(), getHeight() / 2, paint); // x
//        canvas.drawLine(getWidth() / 2, 0f, getWidth() / 2, getHeight(), paint); // y

        for (int i = 0; i < mLines; i++) {
            int x = mStartX + i * mLineGap;
            drawListenLine(canvas, x, mMiddleY - mDistance[i], mMiddleY + mDistance[i]);
        }
    }

    private void drawListenLine(Canvas canvas, int x, int startY, int endY) {
        RectF rect = new RectF();
        rect.top = startY;
        rect.bottom = endY;
        rect.left = x - 1.5f;
        rect.right = x + 1.5f;
        canvas.drawRoundRect(rect, 2.0f, 2.0f, mPaint);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        print("onRestoreInstanceState");
        if (!(state instanceof SaveState)) {
            super.onRestoreInstanceState(state);
        } else {
            SaveState saveState = (SaveState) state;
            super.onRestoreInstanceState(saveState.getSuperState());
            mState = saveState.state;
            setVisibility(saveState.visibility);
            print("==##state onRestoreInstanceState " + ", mState " + mState + ", visibility " + saveState.visibility);

            if (mState == STATE_LISTEN) {
                startListenerAnimate(true);
            } else if (mState == STATE_PARSE) {
                startParserAnimate(true);
            }
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SaveState saveState = new SaveState(superState);
        saveState.state = mState;
        saveState.visibility = getVisibility();

        print("==##state onSaveInstanceState state " + mState + ", isRunning " + isRunning() + ", visibility " + getVisibility());

//        if (isRunning()) {
//            cancel(); // 需要在 saveState.state = mState; 之后执行
//        }

        return saveState;
    }

    private static class SaveState extends BaseSavedState {
        public int state;
        public int visibility;

        public SaveState(Parcelable superState) {
            super(superState);
        }

        private SaveState(Parcel in) {
            super(in);
            state = in.readInt();
            visibility = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(state);
            out.writeInt(visibility);
        }

        public static final Parcelable.Creator<SaveState> CREATOR = new Parcelable.Creator<SaveState>() {

            @Override
            public SaveState createFromParcel(Parcel source) {
                return new SaveState(source);
            }

            @Override
            public SaveState[] newArray(int size) {
                return new SaveState[size];
            }
        };
    }

    public void print(String msg) {
//        Log.e("beauty_q", msg);
    }
}

package com.foo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/6/19.
 */
public class RotateImageView extends ImageView {

    public static final int DELAY_MILLISECONDS = 30;

    private Drawable mDrawableSrc;
    private Bitmap   output;
    private Paint    mPaint;

    private int     defaultWidth;
    private int     defaultHeight;
    private int     diameter;
    private int     radius;
    private int     currentDegree;
    private int     savedDegree;
    private boolean isRotateEnable;
    private boolean isRotating;


    public RotateImageView(Context context) {
        this(context, null);
    }

    public RotateImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotateImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDrawableSrc = getDrawable();
        isRotateEnable = false;
        isRotating = false;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setDither(true);
    }


    public void startRotate() {
        if (!isRotating) {
            isRotateEnable = true;
            isRotating = true;
            currentDegree = savedDegree;
            invalidate();
        }
    }

    public void pauseRotate() {
        isRotating = false;
        isRotateEnable = false;
        savedDegree = currentDegree;
    }

    public void stopRotate() {
        isRotating = false;
        isRotateEnable = false;
        savedDegree = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mDrawableSrc == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        // 如果是.9图
        if (mDrawableSrc.getClass() == NinePatchDrawable.class) {
            return;
        }

        if (output == null) {
            defaultHeight = getHeight();
            defaultWidth = getWidth();
            diameter = Math.min(defaultHeight, defaultWidth);
            radius = diameter / 2;
            Bitmap bitmapOut = getCutPic(mDrawableSrc);


            Rect rect = new Rect(0, 0, bitmapOut.getWidth(), bitmapOut.getHeight());

            output = Bitmap.createBitmap(bitmapOut.getWidth(), bitmapOut.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas mTempCanvas = new Canvas(output);
            mTempCanvas.drawARGB(0, 0, 0, 0);
            mTempCanvas.drawCircle(bitmapOut.getWidth() / 2, bitmapOut.getHeight() / 2, bitmapOut.getWidth() / 2, mPaint);
            //
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            mTempCanvas.drawBitmap(bitmapOut, rect, rect, mPaint);
        }

        if (isRotateEnable) {
            currentDegree = (currentDegree + 1) % 360;
            canvas.save();
            canvas.rotate(currentDegree, defaultWidth / 2, defaultHeight / 2);
            canvas.drawBitmap(output, defaultWidth / 2 - radius, defaultHeight / 2 - radius, null);
            canvas.restore();
            postInvalidateDelayed(DELAY_MILLISECONDS);
        } else {
            canvas.save();
            canvas.rotate(currentDegree, defaultWidth / 2, defaultHeight / 2);
            canvas.drawBitmap(output, defaultWidth / 2 - radius, defaultHeight / 2 - radius, null);
            canvas.restore();
        }
    }

    private Bitmap getCutPic(Drawable DrawableSrc) {
        Bitmap mBitmapOrigin = ((BitmapDrawable)DrawableSrc).getBitmap();
        int mWidth = mBitmapOrigin.getWidth();
        int mHeight = mBitmapOrigin.getHeight();
        float scale = Math.min((float)mWidth / (float)defaultWidth, (float)mHeight / (float)defaultHeight);
        Bitmap mBitmapScaled = Bitmap.createScaledBitmap(mBitmapOrigin, (int)(mWidth / scale), (int)(mHeight / scale), false);
        int x = mBitmapScaled.getWidth() / 2 - radius;
        int y = mBitmapScaled.getHeight() / 2 - radius;
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }

        return Bitmap.createBitmap(mBitmapScaled, x, y, diameter, diameter);
    }
}

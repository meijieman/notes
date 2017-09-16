package com.hohistar.loadpicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ScaleImageView extends ImageView {

    /*拖拉照片模式*/
    private static final int MODE_DRAG = 1;
    /*放大缩小照片模式*/
    private static final int MODE_ZOOM = 2;
    /*不支持Matrix*/
    private static final int MODE_UNABLE = 3;
    /*最大缩放*/
    private int maxScale = 6;
    /*当前模式*/
    private int mMode = 0;//
    /**
     * 缩放开始时的手指间距
     */
    private float mStartDis;
    /**
     * 当前Matrix
     */
    private Matrix mCurrentMatrix = new Matrix();
    /**
     * 用于记录开始时候的坐标位置
     */
    private PointF startPoint = new PointF();
    /**
     * 模板Matrix，用以初始化
     */
    private Matrix mMatrix = new Matrix();
    /**
     * 图片在view中的宽度和高度:用于计算边界
     */
    private float imageWidth;
    private float imageHeight;
    /**
     * 图片真实的宽度和高度:用于计算缩放状态
     */
    private float mImageWidth;
    private float mImageHeight;

    public ScaleImageView(Context context) {
        this(context, null);
    }

    public ScaleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //将缩放类型设置为CENTER_INSIDE，表示把图片按比例扩大/缩小到View的宽度，居中显示
        setScaleType(ScaleType.CENTER_INSIDE);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        // TODO Auto-generated method stub
        super.setImageBitmap(bm);

        //设置完图片后，获取该图片的坐标变换矩阵
        mMatrix.set(getImageMatrix());
        float[] values = new float[9];
        mMatrix.getValues(values);
        //居中显示时图片的高度
        imageHeight = bm.getHeight();
        imageWidth = bm.getWidth();
        //图片宽度为屏幕宽度除缩放倍数
        mImageWidth = bm.getWidth() / values[Matrix.MSCALE_X];
        mImageHeight = (bm.getHeight() - values[Matrix.MTRANS_Y] * 2) / values[Matrix.MSCALE_Y];
    }

    public void setImageResource(int resId) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), resId);
        setImageBitmap(bm);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // TODO Auto-generated method stub
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                //设置拖动模式
                mMode = MODE_DRAG;
                startPoint.set(event.getX(), event.getY());
                Log.e("ceshi", "123");
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //禁止缩小
                if (checkScaleType() < 0) {
                    resetMatrix();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mMode == MODE_ZOOM) {
                    setZoomMatrix(event);
                } else if (mMode == MODE_DRAG) {
                    setDragMatrix(event);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (mMode == MODE_UNABLE) return true;
                mMode = MODE_ZOOM;
                mStartDis = distance(event);
                break;
            default:
                break;
        }

        return true;
    }

    /**
     * 图片被放大后可以拖动
     *
     * @param event
     */
    public void setDragMatrix(MotionEvent event) {
        if (checkScaleType() == 1) {
            getParent().requestDisallowInterceptTouchEvent(true);

            float dx = event.getX() - startPoint.x; // 得到x轴的移动距离
            float dy = event.getY() - startPoint.y; // 得到x轴的移动距离

            //避免和双击冲突,大于10f才算是拖动
            if (Math.sqrt(dx * dx + dy * dy) > 10f) {
                startPoint.set(event.getX(), event.getY());
                //在当前基础上移动
                mCurrentMatrix.set(getImageMatrix());
                float[] values = new float[9];
                mCurrentMatrix.getValues(values);
                dx = checkDxBound(values, dx);
                dy = checkDyBound(values, dy);

                if (dx == 0 && dy == 0) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }

                mCurrentMatrix.postTranslate(dx, dy);
                setImageMatrix(mCurrentMatrix);
            }
        }
    }

    /**
     * 设置缩放Matrix
     *
     * @param event
     */
    private void setZoomMatrix(MotionEvent event) {

        setScaleType(ScaleType.MATRIX);

        //只有同时触屏两个点的时候才执行
        if (event.getPointerCount() < 2) {
            return;
        }

        float endDis = distance(event);// 结束距离
        if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10
            float scale = endDis / mStartDis;// 得到缩放倍数
            mStartDis = endDis;//重置距离
            mCurrentMatrix.set(getImageMatrix());//初始化Matrix
            float[] values = new float[9];
            mCurrentMatrix.getValues(values);
            scale = checkMaxScale(scale, values);
            mCurrentMatrix.postScale(scale, scale, getWidth() / 2, getHeight() / 2);
            setImageMatrix(mCurrentMatrix);
        }
    }

    /**
     * 检验scale，使图像缩放后不会超出最大倍数
     *
     * @param scale
     * @param values
     * @return
     */
    private float checkMaxScale(float scale, float[] values) {
//		float maxScale=getHeight()*1.0f/imageHeight;
        if (scale * values[Matrix.MSCALE_X] > maxScale) {
            scale = maxScale / values[Matrix.MSCALE_X];
        }
        return scale;
    }

    /**
     * 重置Matrix
     */
    public void resetMatrix() {
        setScaleType(ScaleType.CENTER_INSIDE);
        setImageMatrix(mMatrix);
    }

    /**
     * 判断当前缩放的类别
     *
     * @return -1缩小 0未缩放 1放大
     */
    private int checkScaleType() {
        // TODO Auto-generated method stub
        float[] curValues = new float[9];
        float[] values = new float[9];
        //获取当前X轴缩放级别
        getImageMatrix().getValues(curValues);
        //获取模板的X轴缩放级别，两者做比较
        mMatrix.getValues(values);
        float scale = curValues[Matrix.MSCALE_X] - values[Matrix.MSCALE_X];
        if (scale < 0) {
            return -1;
        } else if (scale > 0) {
            return 1;
        }
        return 0;
    }

    /**
     * 和当前矩阵对比，检验dy，使图像移动后不会超出ImageView边界
     *
     * @param values
     * @param dy
     * @return
     */
    private float checkDyBound(float[] values, float dy) {
        float height = getHeight();
        if (mImageHeight * values[Matrix.MSCALE_Y] < height) {
            //缩放后没有超出view的高度
            return 0;
        }
        if (values[Matrix.MTRANS_Y] + dy > 0)
            dy = -values[Matrix.MTRANS_Y];
        else if (values[Matrix.MTRANS_Y] + dy < -(imageHeight * values[Matrix.MSCALE_Y] - height))
            dy = -(imageHeight * values[Matrix.MSCALE_Y] - height) - values[Matrix.MTRANS_Y];
        return dy;
    }

    /**
     * 和当前矩阵对比，检验dx，使图像移动后不会超出ImageView边界
     *
     * @param values
     * @param dx
     * @return
     */
    private float checkDxBound(float[] values, float dx) {
        float width = getWidth();
        if (mImageWidth * values[Matrix.MSCALE_X] < width) {
            //缩放后没有超出view的宽度
            return 0;
        }
        if (values[Matrix.MTRANS_X] + dx > 0)
            dx = -values[Matrix.MTRANS_X];
        else if (values[Matrix.MTRANS_X] + dx < -(imageWidth * values[Matrix.MSCALE_X] - width))
            dx = -(imageWidth * values[Matrix.MSCALE_X] - width) - values[Matrix.MTRANS_X];
        return dx;
    }

    /**
     * 计算两个手指间的距离
     *
     * @param event
     * @return
     */
    private float distance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        /** 使用勾股定理返回两点之间的距离 */
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}


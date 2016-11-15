package com.foo.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * @desc: TODO
 * @author: Major
 * @since: 2016/11/16 0:41
 */
public class SinView extends View {

    public SinView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 背景
        Paint screenPaint = new Paint();
        screenPaint.setStyle(Paint.Style.FILL);
        screenPaint.setColor(Color.parseColor("#011801"));
        canvas.drawRect(30, 60, 450, 400, screenPaint);

        Paint netPaint = new Paint();
        netPaint.setStyle(Paint.Style.FILL);
        netPaint.setColor(Color.parseColor("#D4D3C3"));
        //横向网格
        for (int i = 1; i < 5; i++) {
            canvas.drawLine(30, 230 - 40 * i, 450, 230 - 40 * i, netPaint);
            canvas.drawLine(30, 230 + 40 * i, 450, 230 + 40 * i, netPaint);
        }
        //纵向网格
        for (int i = 1; i < 11; i++) {
            canvas.drawLine(40 + 40 * i, 400, 40 + 40 * i, 60, netPaint);
        }

        // XY轴
        Paint xyPaint = new Paint();
        xyPaint.setStyle(Paint.Style.STROKE);
        xyPaint.setColor(Color.parseColor("#EDA54F"));
        xyPaint.setStrokeWidth(3);
        canvas.drawLine(30, 230, 450, 230, xyPaint);//X轴
        canvas.drawLine(40, 60, 40, 400, xyPaint);//Y轴

        //绘制sin曲线
        Paint sinPaint = new Paint();
        sinPaint.setStyle(Paint.Style.FILL);
        sinPaint.setColor(Color.parseColor("#1FF421"));
        sinPaint.setAntiAlias(true);
        sinPaint.setStrokeWidth(2);
        for (int i = 0; i < 360; i++) {
            double x = getValue(i);//获取sin值
            double y = getValue(i + 1);
            canvas.drawLine(40 + (float)i * (float)((320 * 1.0) / 360), (float)(230 - x * 80),
                    40 + (float)(i + 1) * (float)((320 * 1.0) / 360), (float)(230 - y * 80), sinPaint);
        }
    }

    public double getValue(int i) {
        double result;
        result = Math.sin(i * Math.PI / 180);
//        result = Math.cos(i * Math.PI / 180);
//        result = Math.tan(i * Math.PI / 180);
        return result;
    }
}
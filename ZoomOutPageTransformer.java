package com.hongfans.viewpagerdemo;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * ViewPager 切换动画
 * 调用时 viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
 */
public class ZoomOutPageTransformer implements ViewPager.PageTransformer {

    public static final float MIN_SCALE = .9F;
    public static final float MIN_ALPHA = .7F;

    /**
     *
     * @param view
     * @param position 这个position并不是我们引导页页面的position
     */
    @Override
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();
        if (position < -1) {
            // 向左滑动，当前页面的前一页.
            view.setAlpha(0);
        } else if (position <= 1) { // [-1,1]
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2);
            }
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
            view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
        } else {
            //向右滑动，当前页面的后一页
            view.setAlpha(0);
        }
    }
}

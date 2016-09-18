import android.util.Log;
import android.view.View;

import java.util.Date;

/**
 * @Desc: TODO
 * @Author: Major
 * @Since: 2016/9/18 10:09
 */
public abstract class XOnClickListener implements View.OnClickListener {

    private static final String TAG = "XOnClickListener";

    private long mLatestClick;
    private long mDuration;

    /**
     * @param duration 多少时间内不允许重复点击
     */
    public XOnClickListener(long duration) {
        mDuration = duration;
    }

    public XOnClickListener() {

    }

    @Override
    public void onClick(View v) {
        long current = System.currentTimeMillis();
        if (current - mLatestClick > mDuration) {
            record(v);
            mLatestClick = current;
            XOnClick(v);
        } else {
            onFastClick(v);
        }
    }

    // 记录到文本或数据库
    // 记录操作，标签为为 view 设置的 tag
    private void record(View v) {
        Object obj = v.getTag();
        if (obj != null) {
            if (obj instanceof String) {
                String tag = (String)obj;
                Log.w(TAG, new Date().toLocaleString() + " TAG " + tag);
            } else {
                String s = obj.toString();
                Log.w(TAG, new Date().toLocaleString() + " TAG " + s);
            }
        } else {
            String className = v.getClass().getSimpleName();
            Log.w(TAG, new Date().toLocaleString() + " CLASS " + className);
        }
    }

    /**
     * 即 View.OnClickListener#onClick()
     */
    public abstract void XOnClick(View v);

    /**
     * 快速点击时的调用方法
     */
    public void onFastClick(View v) {

    }
}

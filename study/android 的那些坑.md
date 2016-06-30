1. recyclerview item 不能居中

* 方法一
如果是垂直居中，设置 item 根布局 
```xml
    android:layout_gravity="center_vertical"
    android:layout_width="wrap_content"
    android:layout_height="match_parent"
```
在 onCreateViewHolder 中设置
```java
    View view = LayoutInflater.from(mActivity).inflate(R.layout.h_grid_item,parent, false);
    return new VH(view);
```
    
* 方法二
设置 根布局都为 `match_parent`
在 onCreateViewHolder 中设置 view 的 layoutParams
```java
    View view = View.inflate(mActivity, R.layout.h_grid_item, null); // 也可以 View view = LayoutInflater.from(mActivity).inflate(R.layout.h_grid_item,parent, false);
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
    view.setLayoutParams(params);
```

2.当设置 activity 为横屏，添加 fragment（fragment 中添加可滑动的组件以便观察） 到该 activity 中，然后锁屏再解锁，会发现有两个 fragment

设置 activity android:configChanges="keyboardHidden|orientation|screenSize"

1. recyclerview item 不能居中

* 方法一
如果是垂直居中，设置 item 根布局 
```xml
    android:layout_gravity="center_vertical"
    android:layout_width="wrap_content"
    android:layout_height="match_parent"
```
    
* 方法二
在 onCreateViewHolder 中设置 view 的 layoutParams
```java
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
    view.setLayoutParams(params);
```

2.

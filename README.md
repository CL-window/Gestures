## Gesture
手势识别，主要是用的Google的GestureLibrary库
```
http://blog.csdn.net/i_do_can/article/details/53185654
```
## 第一步，加载手势库，(没有就新建一个空白的)
    new File(Environment.getExternalStorageDirectory(), "gestures");
    GestureLibraries.fromFile(File/filePath); // 从文件 适合开发时
    GestureLibraries.fromRawResource(this,R.raw.gestures);// raw文件
## 第二步，手势的画板
GestureOverlayView
```
    <!--
    GestureOverlayView：一种用于手势输入的透明覆盖层，可覆盖在其他控件的上方，也可包含其他控件。
    android:gestureStrokeType 定义笔画（定义为手势）的类型
    android:gestureStrokeWidth 画手势时，笔划的宽度
    -->
    <android.gesture.GestureOverlayView
        android:id="@+id/gestures_overlay"
        android:layout_width="match_parent"
        android:layout_height="0dip"
        android:layout_weight="1.0"
        android:gestureColor="@color/gesture_color"
        android:gestureStrokeWidth="10"
        android:gestureStrokeType="multiple" />

```

## 第三步，手势监听
OnGesturePerformedListener监听器监听一种手势(一笔画完)
识别手势
```
    ArrayList predictions = GestureBuilderActivity.getStore().recognize(gesture);
    if (predictions.size() > 0) {
        //拿到相似度最高的对象
        Prediction prediction = (Prediction) predictions.get(0);
        Log.i("slack","prediction.score : "+ prediction.score);
        // We want at least some confidence in the result 大于2，基本相似
        if (prediction.score > 2.0) {
            // Show the spell
            Toast.makeText(CreateGestureActivity.this, prediction.name, Toast.LENGTH_SHORT).show();
        }
    }
```

## 第四步，保存手势
GestureLibrary.addGesture(name, Gesture);
GestureLibrary..save();


## 导出SD卡里的gestures文件
adb pull sdcard/gestures

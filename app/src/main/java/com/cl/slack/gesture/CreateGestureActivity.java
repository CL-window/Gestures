/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cl.slack.gesture;

import java.util.ArrayList;

import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CreateGestureActivity extends Activity {
    private static final float LENGTH_THRESHOLD = 120.0f;

    private Gesture mGesture;
    private View mDoneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_gesture);

        mDoneButton = findViewById(R.id.done);

        GestureOverlayView overlay = (GestureOverlayView) findViewById(R.id.gestures_overlay);
        overlay.addOnGestureListener(new GesturesProcessor());
        overlay.addOnGesturePerformedListener(new GesturePerformedListener());

        /*手势绘制完成后淡出屏幕的时间间隔，即绘制完手指离开屏幕后相隔多长时间手势从屏幕上消失；
         * 可以理解为手势绘制完成手指离开屏幕后到调用onGesturePerformed的时间间隔
         * 默认值为420毫秒，这里设置为0.5秒
         */
        overlay.setFadeOffset(500);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mGesture != null) {
            outState.putParcelable("gesture", mGesture);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mGesture = savedInstanceState.getParcelable("gesture");
        if (mGesture != null) {
            final GestureOverlayView overlay =
                    (GestureOverlayView) findViewById(R.id.gestures_overlay);
            overlay.post(new Runnable() {
                public void run() {
                    overlay.setGesture(mGesture);
                }
            });

            mDoneButton.setEnabled(true);
        }
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void addGesture(View v) {
        if (mGesture != null) {
            final TextView input = (TextView) findViewById(R.id.gesture_name);
            final CharSequence name = input.getText();
            if (name.length() == 0) {
                input.setError(getString(R.string.error_missing_name));
                return;
            }

            final GestureLibrary store = GestureBuilderActivity.getStore();
            store.addGesture(name.toString(), mGesture);
            store.save();

            setResult(RESULT_OK);

//            final String path = new File(Environment.getExternalStorageDirectory(),
//                    "gestures").getAbsolutePath();
//            Toast.makeText(this, getString(R.string.save_success, path), Toast.LENGTH_LONG).show();
        } else {
            setResult(RESULT_CANCELED);
        }

        finish();

    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void cancelGesture(View v) {
        setResult(RESULT_CANCELED);
        finish();
    }

    private class GesturesProcessor implements GestureOverlayView.OnGestureListener {
        public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
//            Log.i("slack","onGestureStarted...");
            mDoneButton.setEnabled(false);
            mGesture = null;
        }

        public void onGesture(GestureOverlayView overlay, MotionEvent event) {
//            Log.i("slack","onGesture...");
        }

        public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
            Log.i("slack","onGestureEnded...");
            mGesture = overlay.getGesture();
            if (mGesture.getLength() < LENGTH_THRESHOLD) {
                overlay.clear(false);
            }
            mDoneButton.setEnabled(true);
        }

        public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
            Log.i("slack","onGestureCancelled...");
        }
    }

    //OnGesturePerformedListener监听器监听一种手势(一笔画完)
    class GesturePerformedListener implements GestureOverlayView.OnGesturePerformedListener{

        @Override
        public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
            Log.i("slack","onGesturePerformed...");
            mGesture = gesture;
            if (mGesture.getLength() < LENGTH_THRESHOLD) {
                overlay.clear(false);
            }
            mDoneButton.setEnabled(true);
            // 识别手势 Prediction是一个相似度对象,集合中的相似度是从高到低进行排列
            ArrayList predictions = GestureBuilderActivity.getStore().recognize(gesture);

            // We want at least one prediction
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
        }
    }
}


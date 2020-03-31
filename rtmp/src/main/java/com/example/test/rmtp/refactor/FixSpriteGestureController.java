package com.example.test.rmtp.refactor;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

import com.pedro.encoder.input.gl.SpriteGestureController;
import com.pedro.encoder.input.video.CameraHelper;

public class FixSpriteGestureController extends SpriteGestureController {
    private float lastDistance;

    @Override
    public void scaleSprite(MotionEvent motionEvent) {
        if (getBaseObjectFilterRender() == null) return;
        if (motionEvent.getPointerCount() > 1) {
            float distance = CameraHelper.getFingerSpacing(motionEvent);
            float percent = distance >= lastDistance ? 1 : -1;
            PointF scale = getBaseObjectFilterRender().getScale();
            scale.x *= 1+percent/100;
            scale.y *= 1+percent/100;
            getBaseObjectFilterRender().setScale(scale.x, scale.y);
            lastDistance = distance;
        }
    }
}

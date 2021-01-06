package com.example.test.rmtp.filterReuse.record;

import android.graphics.Bitmap;

public class ReuseCustomGPUImageChromaKeyBlendFilterRecord extends NonReleaseObjectFilterRecord {
    private Bitmap replaceBitmap;

    public ReuseCustomGPUImageChromaKeyBlendFilterRecord() {
        super(false);
        setGestureMove(false);
        setGestureScale(false);
    }

    public Bitmap getReplaceBitmap() {
        return replaceBitmap;
    }

    public void setReplaceBitmap(Bitmap replaceBitmap) {
        this.replaceBitmap = replaceBitmap;
    }
}

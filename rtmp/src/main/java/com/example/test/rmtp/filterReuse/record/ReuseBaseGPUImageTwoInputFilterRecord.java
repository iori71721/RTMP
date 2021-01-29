package com.example.test.rmtp.filterReuse.record;

import android.graphics.Bitmap;

public class ReuseBaseGPUImageTwoInputFilterRecord extends NonReleaseObjectFilterRecord{
    private Bitmap replaceBitmap;

    public ReuseBaseGPUImageTwoInputFilterRecord() {
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

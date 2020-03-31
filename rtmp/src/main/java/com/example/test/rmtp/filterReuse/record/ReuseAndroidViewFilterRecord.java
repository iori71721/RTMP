package com.example.test.rmtp.filterReuse.record;

import android.view.View;

public class ReuseAndroidViewFilterRecord extends ReuseBaseObjectFilterRecord {
    private View attatchView;

    @Override
    public void release() {
        attatchView=null;
    }

    public View getAttatchView() {
        return attatchView;
    }

    public void setAttatchView(View attatchView) {
        this.attatchView = attatchView;
    }
}

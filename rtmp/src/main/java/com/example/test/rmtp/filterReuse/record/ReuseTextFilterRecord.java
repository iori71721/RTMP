package com.example.test.rmtp.filterReuse.record;

import android.graphics.PointF;
import android.graphics.Typeface;
import android.util.Log;

import com.iori.custom.common.string.StringTool;

public class ReuseTextFilterRecord extends NonReleaseObjectFilterRecord{
    public static final String BLANK_STRING="   ";
    private String text= BLANK_STRING;
    public float textSize=100;
    public int textColor;
    public Typeface typeface;
    private PointF initScale;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if(StringTool.isEmpty(text)){
            text=BLANK_STRING;
        }
        this.text = text;
    }

    public void setInitScale(PointF initScale) {
        if(this.initScale == null){
            this.initScale=new PointF(DEFAULT_SCALE,DEFAULT_SCALE);
        }
        this.initScale.x = initScale.x;
        this.initScale.y = initScale.y;
    }

    public PointF getInitScale() {
        return initScale;
    }
}

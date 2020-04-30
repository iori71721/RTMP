package com.example.test.rmtp.filterReuse.record;

import android.graphics.Typeface;
import android.util.Log;

import com.iori.custom.common.string.StringTool;

public class ReuseTextFilterRecord extends NonReleaseObjectFilterRecord{
    public static final String BLANK_STRING="   ";
    private String text= BLANK_STRING;
    public float textSize=100;
    public int textColor;
    public Typeface typeface;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if(StringTool.isEmpty(text)){
            text=BLANK_STRING;
        }
        this.text = text;
    }
}

package com.example.test.rmtp.filterReuse;

import android.graphics.PointF;
import android.graphics.Typeface;

import com.example.test.rmtp.filterReuse.record.ReuseTextFilterRecord;
import com.pedro.encoder.input.gl.render.filters.object.TextObjectFilterRender;
import com.pedro.rtplibrary.rtmp.RtmpCamera2;

public class TextFilterReuse extends BaseObjectFilterReuse<TextObjectFilterRender, ReuseTextFilterRecord>{
    public TextFilterReuse(ReuseTextFilterRecord record) {
        super(record);
    }

    @Override
    public TextObjectFilterRender generateFilter(ReuseTextFilterRecord record) {
        TextObjectFilterRender generateFilter=new TextObjectFilterRender();
        setupText(generateFilter,record);
        generateFilter.setDefaultScale(record.getDefaultOutputSize().x,record.getDefaultOutputSize().y);
        record.setScale(new PointF(generateFilter.getScale().x,generateFilter.getScale().y));
        generateFilter.setPosition(record.getPosition().x,record.getPosition().y);
        return generateFilter;
    }

    private void setupText(TextObjectFilterRender textObjectFilterRender,ReuseTextFilterRecord textFilterRecord){
        if(textFilterRecord.typeface == null){
            textObjectFilterRender.setText(textFilterRecord.getText(),textFilterRecord.textSize,textFilterRecord.textColor);
        }else{
            textObjectFilterRender.setText(textFilterRecord.getText(),textFilterRecord.textSize,textFilterRecord.textColor
                    ,textFilterRecord.typeface);
        }
    }

    public void setText(String text) {
        setText(text,getFilterRecord().textSize,getFilterRecord().textColor,getFilterRecord().typeface);
    }

    public void setTextSize(float textSize){
        setText(getFilterRecord().getText(),textSize,getFilterRecord().textColor,getFilterRecord().typeface);
    }

    public void setTextColor(int textColor){
        setText(getFilterRecord().getText(),getFilterRecord().textSize,textColor,getFilterRecord().typeface);
    }

    public void setText(String text, float textSize, int textColor, Typeface typeface) {
        getFilterRecord().setText(text);
        getFilterRecord().textSize=textSize;
        getFilterRecord().textColor=textColor;
        getFilterRecord().typeface=typeface;
        setupText(getReusedFilter(),getFilterRecord());

        reuse(attachCamera);
    }

    @Override
    public void reuse(RtmpCamera2 attachCamera) {
        TextObjectFilterRender reusedFilter=generateFilter(getFilterRecord());
        setReusedFilter(reusedFilter);
        attachCameraAndRender(attachCamera,getReusedFilter(),getFilterRecord().getReuseIndex());
    }
}

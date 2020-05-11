package com.example.test.rmtp.filterReuse;

import android.graphics.PointF;
import android.graphics.Typeface;
import android.util.Log;

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
        generateFilter.setPosition(record.getPosition().x,record.getPosition().y);
        return generateFilter;
    }

    private PointF calcScale(PointF currentScale,PointF initScale){
        float scaleX=currentScale.x/initScale.x;
        float scaleY=currentScale.y/initScale.y;
        return new PointF(scaleX,scaleY);
    }

    private void setupText(TextObjectFilterRender textObjectFilterRender, ReuseTextFilterRecord textFilterRecord){
        PointF fixScale=null;
        if(textFilterRecord.getInitScale() != null){
                fixScale = calcScale(textFilterRecord.getScale(), textFilterRecord.getInitScale());
        }

        if(textFilterRecord.typeface == null){
            textObjectFilterRender.setText(textFilterRecord.getText(),textFilterRecord.textSize,textFilterRecord.textColor);
        }else{
            textObjectFilterRender.setText(textFilterRecord.getText(),textFilterRecord.textSize,textFilterRecord.textColor
                    ,textFilterRecord.typeface);
        }

        textObjectFilterRender.setDefaultScale(textFilterRecord.getDefaultOutputSize().x,textFilterRecord.getDefaultOutputSize().y);
        textFilterRecord.setInitScale(textObjectFilterRender.getScale());

        if(fixScale != null){
            float fixScaleX=textFilterRecord.getInitScale().x*fixScale.x;
            float fixScaleY=textFilterRecord.getInitScale().y*fixScale.y;
            textObjectFilterRender.setScale(fixScaleX,fixScaleY);
        }

        textFilterRecord.setScale(new PointF(textObjectFilterRender.getScale().x,textObjectFilterRender.getScale().y));
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
        getFilterRecord().updateInformation(getReusedFilter(),isVisible());
    }

    @Override
    public void reuse(RtmpCamera2 attachCamera) {
        TextObjectFilterRender reusedFilter=new TextObjectFilterRender();
        setupText(reusedFilter,getFilterRecord());
        reusedFilter.setPosition(getFilterRecord().getPosition().x,getFilterRecord().getPosition().y);
        setReusedFilter(reusedFilter);
        attachCameraAndRender(attachCamera,getReusedFilter(),getFilterRecord().getReuseIndex());
    }
}

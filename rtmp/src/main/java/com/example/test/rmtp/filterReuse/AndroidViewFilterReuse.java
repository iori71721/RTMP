package com.example.test.rmtp.filterReuse;

import android.graphics.PointF;
import android.view.View;

import com.example.test.rmtp.filterReuse.record.ReuseAndroidViewFilterRecord;
import com.pedro.encoder.input.gl.render.filters.AndroidViewFilterRender;
import com.pedro.encoder.utils.gl.TranslateTo;
import com.pedro.rtplibrary.rtmp.RtmpCamera2;

import java.lang.reflect.Field;

/**
 * 1.setup View,default preViewSize
 * 2.{@link #setupDefaultAttachViewSizeAndSetupRecord(AndroidViewFilterRender, float, float)}
 * 3.add filter and attch see {@link FilterReusedManager#addFilter(BaseObjectFilterReuse, String)}
 */
public class AndroidViewFilterReuse extends BaseObjectFilterReuse<AndroidViewFilterRender, ReuseAndroidViewFilterRecord>{

    public AndroidViewFilterReuse(ReuseAndroidViewFilterRecord record) {
        super(record);
        getFilterRecord().setGestureMove(false);
        getFilterRecord().setGestureScale(false);
    }

    @Override
    public AndroidViewFilterRender generateFilter(ReuseAndroidViewFilterRecord record) {
        AndroidViewFilterRender generateFilter=new AndroidViewFilterRender();
        generateFilter.setView(getFilterRecord().getAttatchView());
        generateFilter.setPreviewSize(getFilterRecord().getDefaultOutputSize().x,getFilterRecord().getDefaultOutputSize().y);
        setupDefaultAttachViewSizeAndSetupRecord(generateFilter,1f,1f);
        setPosition(generateFilter,TranslateTo.CENTER);
        return generateFilter;
    }

    /**
     * after call attach see{@link #setupDefaultAttachViewSizeAndSetupRecord(AndroidViewFilterRender, float, float)}
     * @param setupRender
     * @param scalex
     * @param scaley
     */
    public void scaleAttachViewAndSetupRecord(AndroidViewFilterRender setupRender , float scalex, float scaley){
        float beforeScalex=getFilterRecord().getScale().x;
        float beforeScaley=getFilterRecord().getScale().y;
        float setupScalex=beforeScalex * scalex;
        float setupScaley=beforeScaley * scaley;
        setupRender.setScale(setupScalex,setupScaley);
        getFilterRecord().setScale(new PointF(setupScalex,setupScaley));
    }

    /**
     * {@link AndroidViewFilterRender} scale x is full previewWidth when is 1
     * @param setupRender
     * @param scalex scale default width,example 2 is default width *2
     * @param scaley
     */
    public void setupDefaultAttachViewSizeAndSetupRecord(AndroidViewFilterRender setupRender , float scalex, float scaley){
        View setupView=getFilterRecord().getAttatchView();
        float setupScalex=(float)setupView.getWidth()/getFilterRecord().getDefaultOutputSize().x;
        float setupScaley=(float)setupView.getHeight()/getFilterRecord().getDefaultOutputSize().y;
//        init scale is default size
        initScale();
        setupScalex *= scalex;
        setupScaley *= scaley;
        scaleAttachViewAndSetupRecord(setupRender,setupScalex,setupScaley);
    }

    private void initScale(){
        getFilterRecord().setScale(new PointF(1,1));
    }


    @Override
    void setScale(float scaleX, float scaleY) {
        scaleAttachViewAndSetupRecord(getReusedFilter(),scaleX,scaleY);
    }

    @Override
    void setPosition(float x, float y) {
        setPosition(getReusedFilter(),x,y);
    }

    @Override
    void setPosition(TranslateTo positionTo) {
        setPosition(getReusedFilter(),positionTo);
    }

    /**
     * lib setPosition is inferior,so refactor it
     * @param setupRender
     * @param x
     * @param y
     */
    void setPosition(AndroidViewFilterRender setupRender, float x, float y){
        Field positionX, positionY;
        try {
            positionX=setupRender.getClass().getDeclaredField("positionX");
            positionY=setupRender.getClass().getDeclaredField("positionY");
            positionX.setAccessible(true);
            positionY.setAccessible(true);
            positionX.setFloat(setupRender,x);
            positionY.setFloat(setupRender,y);
            getFilterRecord().setPosition(new PointF(x,y));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * copy from {@link AndroidViewFilterRender#setPosition(TranslateTo)}
     * @param setupRender
     * @param positionTo
     */
    void setPosition(AndroidViewFilterRender setupRender, TranslateTo positionTo){
        int previewX = getFilterRecord().getDefaultOutputSize().x;
        int previewY = getFilterRecord().getDefaultOutputSize().y;
        int viewX=getFilterRecord().getAttatchView().getWidth();
        int viewY=getFilterRecord().getAttatchView().getHeight();
        float setupPositionX,setupPositionY;

        switch (positionTo) {
            case TOP:
                setupPositionX = previewX / 2f - (viewX / 2f);
                setupPositionY = 0f;
                break;
            case LEFT:
                setupPositionX = 0;
                setupPositionY = previewY / 2f - (viewY / 2f);
                break;
            case RIGHT:
                setupPositionX = previewX - viewX;
                setupPositionY = previewY / 2f - (viewY / 2f);
                break;
            case BOTTOM:
                setupPositionX = previewX / 2f - (viewX / 2f);
                setupPositionY = previewY - viewY;
                break;
            case CENTER:
                setupPositionX = previewX / 2f - (viewX / 2f);
                setupPositionY = previewY / 2f - (viewY / 2f);
                break;
            case TOP_RIGHT:
                setupPositionX = previewX - viewX;
                setupPositionY = 0;
                break;
            case BOTTOM_LEFT:
                setupPositionX = 0;
                setupPositionY = previewY - viewY;
                break;
            case BOTTOM_RIGHT:
                setupPositionX = previewX - viewX;
                setupPositionY = previewY - viewY;
                break;
            case TOP_LEFT:
            default:
                setupPositionX = 0;
                setupPositionY = 0;
                break;
        }
        setPosition(setupRender,setupPositionX,setupPositionY);

    }

    @Override
    public void reuse(RtmpCamera2 attachCamera) {
        setReusedFilter(new AndroidViewFilterRender());
        getReusedFilter().setView(getFilterRecord().getAttatchView());
        setPosition(getReusedFilter(),getFilterRecord().getPosition().x,getFilterRecord().getPosition().y);
        float beforeScalex=getFilterRecord().getScale().x;
        float beforeScaley=getFilterRecord().getScale().y;
        initScale();
        scaleAttachViewAndSetupRecord(getReusedFilter(),beforeScalex,beforeScaley);
        attachCamera.getGlInterface().setFilter(getFilterRecord().getReuseIndex(),getReusedFilter());
    }
}

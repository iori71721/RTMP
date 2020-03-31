package com.example.test.rmtp.filterReuse;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.IntDef;

import com.example.test.rmtp.filterReuse.record.ReuseImageObjectFilterRecord;
import com.pedro.encoder.input.gl.render.filters.object.ImageObjectFilterRender;
import com.pedro.encoder.utils.gl.TranslateTo;
import com.pedro.rtplibrary.rtmp.RtmpCamera2;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * usage
 * 1.setup record info
 * 2.reuse
 */
public class ImageFilterReuse extends BaseObjectFilterReuse<ImageObjectFilterRender, ReuseImageObjectFilterRecord> {
    private @LoadImageType.EnumRange
    int loadImageType = LoadImageType.RESOURCE;

    public ImageFilterReuse(ReuseImageObjectFilterRecord record) {
        super(record);
    }

    public void init(Context context){
        setContext(context);
    }

    @Override
    public ImageObjectFilterRender generateFilter(ReuseImageObjectFilterRecord record) {
        ImageObjectFilterRender generateFilter=new ImageObjectFilterRender();
        loadImageAndSetupRecord(getLoadImageType(),generateFilter,record);
        generateFilter.setDefaultScale(record.getDefaultOutputSize().x,record.getDefaultOutputSize().y);

        float xScale=generateFilter.getScale().x;
        float yScale=generateFilter.getScale().y;
        generateFilter.setPosition(TranslateTo.CENTER);
        return generateFilter;
    }

    private Bitmap createBitmap(@LoadImageType.EnumRange int loadType, ReuseImageObjectFilterRecord record){
        Bitmap createBitmap=null;
        switch (loadType){
            case LoadImageType.RESOURCE:
                if(getContext() != null) {
                    createBitmap = BitmapFactory.decodeResource(getContext().getResources(), record.getResID());
                }
                break;
            default:
                throw new NonSupportLoadImageException();
        }
        return createBitmap;
    }

    private void setupRecord(@LoadImageType.EnumRange int loadType, ReuseImageObjectFilterRecord record){
//        setup before info
        int beforeResID=record.getResID();

        record.clearLoadInfo();
        switch (loadType) {
            case LoadImageType.RESOURCE:
                record.setResID(beforeResID);
                break;
        }
    }

    private Bitmap loadImageAndSetupRecord(@LoadImageType.EnumRange int loadType,ImageObjectFilterRender setupRender, ReuseImageObjectFilterRecord record){
        Bitmap createBitmap=createBitmap(loadType,record);
        loadImageToImageFilterRender(setupRender,createBitmap);
        setupRecord(loadType,record);
        return createBitmap;
    }

    private void loadImageToImageFilterRender(ImageObjectFilterRender setupRender,Bitmap loadBitmap){
        setupRender.setImage(loadBitmap);
    }

    public Bitmap loadImageByResourceAndSetupRecord(int resID){
        getFilterRecord().setResID(resID);
        Bitmap loadBitmap=loadImageAndSetupRecord(LoadImageType.RESOURCE,getReusedFilter(),getFilterRecord());
           return loadBitmap;
    }

    @Override
    public void reuse(RtmpCamera2 attachCamera) {
//        because filter will be released when start stream,so create new
        setReusedFilter(new ImageObjectFilterRender());
        loadImageAndSetupRecord(getLoadImageType(),getReusedFilter(),getFilterRecord());
        getReusedFilter().setScale(getFilterRecord().getScale().x,getFilterRecord().getScale().y);
        getReusedFilter().setPosition(getFilterRecord().getPosition().x,getFilterRecord().getPosition().y);
        attachCameraAndRender(attachCamera,getReusedFilter(),getFilterRecord().getReuseIndex());
    }

    public @LoadImageType.EnumRange
    int getLoadImageType() {
        return loadImageType;
    }

    private void setLoadImageType(@LoadImageType.EnumRange int loadImageType) {
        this.loadImageType = loadImageType;
    }

    private static class LoadImageType {
        private static final int RESOURCE=1;

        @IntDef({RESOURCE})
        @Retention(RetentionPolicy.SOURCE)
        public @interface EnumRange {}
    }

    public static class NonSupportLoadImageException extends RuntimeException{
        public NonSupportLoadImageException() {
            super("non support load type");
        }
    }
}

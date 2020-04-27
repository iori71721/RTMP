package com.example.test.rmtp.filterReuse;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.Log;

import com.example.test.rmtp.filterReuse.record.NonReleaseObjectFilterRecord;
import com.pedro.encoder.input.gl.render.filters.object.ImageObjectFilterRender;
import com.pedro.rtplibrary.rtmp.RtmpCamera2;

/**
 * usage
 * 1.setup record info
 * 2.reuse
 */
public class ImageFilterReuse extends BaseObjectFilterReuse<ImageObjectFilterRender, NonReleaseObjectFilterRecord> {
    private LoadBitmapBehavior loadBitmapBehavior;

    public ImageFilterReuse(NonReleaseObjectFilterRecord record, LoadBitmapBehavior loadBitmapBehavior) {
        super(record);
        this.loadBitmapBehavior=loadBitmapBehavior;
    }

    public void init(Context context){
        setContext(context);
    }

    @Override
    public ImageObjectFilterRender generateFilter(NonReleaseObjectFilterRecord record) {
        ImageObjectFilterRender generateFilter=new ImageObjectFilterRender();
        loadImage(generateFilter);
        generateFilter.setDefaultScale(record.getDefaultOutputSize().x,record.getDefaultOutputSize().y);
        record.setScale(new PointF(generateFilter.getScale().x,generateFilter.getScale().y));
        generateFilter.setPosition(record.getPosition().x,record.getPosition().y);
        return generateFilter;
    }

    private Bitmap createBitmap(LoadBitmapBehavior loadBitmapBehavior){
        Bitmap createBitmap=null;
        if(loadBitmapBehavior != null) {
            createBitmap= loadBitmapBehavior.loadBitmap();
        }
        return createBitmap;
    }

    private Bitmap loadImage(ImageObjectFilterRender setupRender){
        Bitmap createBitmap=createBitmap(loadBitmapBehavior);
        loadImageToImageFilterRender(setupRender,createBitmap);
        return createBitmap;
    }

    private void loadImageToImageFilterRender(ImageObjectFilterRender setupRender,Bitmap loadBitmap){
        setupRender.setImage(loadBitmap);
    }

    public void changeImage(LoadBitmapBehavior loadBitmapBehavior){
        this.loadBitmapBehavior=loadBitmapBehavior;
        Bitmap createBitmap = loadBitmapBehavior.loadBitmap();
        getReusedFilter().setImage(createBitmap);
    }

    @Override
    public void reuse(RtmpCamera2 attachCamera) {
//        because filter will be released when start stream,so create new
        setReusedFilter(new ImageObjectFilterRender());
        loadImage(getReusedFilter());
        getReusedFilter().setScale(getFilterRecord().getScale().x,getFilterRecord().getScale().y);
        getReusedFilter().setPosition(getFilterRecord().getPosition().x,getFilterRecord().getPosition().y);
        attachCameraAndRender(attachCamera,getReusedFilter(),getFilterRecord().getReuseIndex());
    }

    public static interface LoadBitmapBehavior{
        Bitmap loadBitmap();
    }
}

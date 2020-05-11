package com.example.test.rmtp.filterReuse;

import android.content.Context;

import com.example.test.rmtp.filterReuse.record.ReuseBaseObjectFilterRecord;
import com.pedro.encoder.input.gl.render.filters.BaseFilterRender;
import com.pedro.encoder.input.gl.render.filters.object.BaseObjectFilterRender;
import com.pedro.encoder.utils.gl.TranslateTo;
import com.pedro.rtplibrary.rtmp.RtmpCamera2;

/**
 * defined object filter how to reuse
 * useage:
 * 1.setup record
 * 2.generate filter and load data see {@link FilterReusedManager#addFilter(BaseObjectFilterReuse, String)}
 * 3.attach camera
 * 4.call loadxxxbyzzzz()  after attachCamera() to change load type,example loadImageByResource.
 * 4.reuse filter
 * @param <T> reused filter type
 * @param <I> generate filter information
 */
public abstract class BaseObjectFilterReuse<T extends BaseFilterRender,I extends ReuseBaseObjectFilterRecord> implements FilterReuse,Comparable<BaseObjectFilterReuse>{
    private I filterRecord;
    private T reusedFilter;
    private Context context;
    private String identifyKey="";
    private boolean deprecated;
    private boolean visible=true;
    protected RtmpCamera2 attachCamera;

    public BaseObjectFilterReuse(I record) {
        this.filterRecord = record;
    }

    public abstract T generateFilter(I record);

    void setScale(float scaleX, float scaleY) {
        if(reusedFilter instanceof BaseObjectFilterRender){
            BaseObjectFilterRender baseObjectFilterRender=(BaseObjectFilterRender)reusedFilter;

            float xScalePercentage=filterRecord.getScale().x*scaleX;
            float yScalePercentage=filterRecord.getScale().y*scaleY;
            baseObjectFilterRender.setScale(xScalePercentage,yScalePercentage);
            filterRecord.updateInformation(baseObjectFilterRender, visible);
        }
    }

    void setPosition(float x, float y) {
        if(reusedFilter instanceof BaseObjectFilterRender){
            BaseObjectFilterRender baseObjectFilterRender=(BaseObjectFilterRender)reusedFilter;
            float xPercentage=x/filterRecord.getDefaultOutputSize().x*100;
            float yPercentage=y/filterRecord.getDefaultOutputSize().y*100;

            baseObjectFilterRender.setPosition(xPercentage,yPercentage);
            filterRecord.updateInformation(baseObjectFilterRender, visible);
        }
    }

    void setPosition(TranslateTo positionTo) {
        if(reusedFilter instanceof BaseObjectFilterRender){
            BaseObjectFilterRender baseObjectFilterRender=(BaseObjectFilterRender)reusedFilter;
            baseObjectFilterRender.setPosition(positionTo);
            if(positionTo == TranslateTo.CENTER){
//                fix lib bug
                float fixY=50f - baseObjectFilterRender.getScale().y / 2f;
                baseObjectFilterRender.setPosition(baseObjectFilterRender.getPosition().x,fixY);
            }
            filterRecord.updateInformation(baseObjectFilterRender, visible);
        }
    }

    @Override
    public int compareTo(BaseObjectFilterReuse o) {
        if(filterRecord != null && o.getFilterRecord() != null){
            return filterRecord.compareTo(o.getFilterRecord());
        }else{
            return -1;
        }
    }

    public void release(){
        if(getFilterRecord() != null) {
            getFilterRecord().release();
        }
    }

    public void attachCameraAndRender(RtmpCamera2 attachCamera, T attachFilter, int attachIndex){
        attachCamera.getGlInterface().setFilter(attachIndex,attachFilter);
        this.attachCamera=attachCamera;
        reusedFilter=attachFilter;
        filterRecord.setAddIndex(attachIndex);
        filterRecord.setReuseIndex(attachIndex);
    }

    @Override
    public void reuse(RtmpCamera2 attachCamera) {
        attachCamera.getGlInterface().setFilter(getFilterRecord().getReuseIndex(),getReusedFilter());
    }

    public boolean canOperate(){
        return isVisible() && !isDeprecated();
    }

    public I getFilterRecord() {
        return filterRecord;
    }

    public void setFilterRecord(I filterRecord) {
        this.filterRecord = filterRecord;
    }

    public T getReusedFilter() {
        return reusedFilter;
    }

    public void setReusedFilter(T reusedFilter) {
        this.reusedFilter = reusedFilter;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getIdentifyKey() {
        return identifyKey;
    }

    public void setIdentifyKey(String identifyKey) {
        this.identifyKey = identifyKey;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public boolean isVisible() {
        return visible;
    }

    void setVisible(boolean visible) {
        this.visible = visible;
    }
}

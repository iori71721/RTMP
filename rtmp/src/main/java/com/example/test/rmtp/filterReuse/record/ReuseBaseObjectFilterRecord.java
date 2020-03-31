package com.example.test.rmtp.filterReuse.record;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

import com.pedro.encoder.input.gl.render.filters.BaseFilterRender;
import com.pedro.encoder.input.gl.render.filters.object.BaseObjectFilterRender;

/**
 * record filter information to reuse
 */
public abstract class ReuseBaseObjectFilterRecord implements Comparable{
    protected PointF position=new PointF(0,0);
    /**
     * x is width scale ,y is height scale,x 100 is full preview width
     */
    protected PointF Scale=new PointF(100,100);

    /**
     * x is width,y is height
     */
    protected Point defaultOutputSize=new Point(100,100);

    protected int addIndex;

    protected int reuseIndex;

    /**
     * true:can update
     */
    private final boolean canUpdate;

    public ReuseBaseObjectFilterRecord() {
        this(true);
    }

    public ReuseBaseObjectFilterRecord(boolean canUpdate) {
        this.canUpdate = canUpdate;
    }

    public abstract void release();

    @Override
    public int compareTo(Object o) {
        int result=0;
        if(o instanceof ReuseBaseObjectFilterRecord){
            ReuseBaseObjectFilterRecord compareRecord=(ReuseBaseObjectFilterRecord)o;
            if(addIndex > compareRecord.addIndex){
                result=1;
            }else if(addIndex < compareRecord.addIndex){
                result=-1;
            }
        }else{
            result=-1;
        }
        return result;
    }

    public void updateInformation(BaseFilterRender updateRender){
        if(!canUpdate || !(updateRender instanceof BaseObjectFilterRender)){
            return;
        }
        BaseObjectFilterRender updateObjectRender=(BaseObjectFilterRender)updateRender;
        PointF updatePosition=new PointF(updateObjectRender.getPosition().x,updateObjectRender.getPosition().y);
        setPosition(updatePosition);
        PointF updateScale=new PointF(updateObjectRender.getScale().x,updateObjectRender.getScale().y);
        setScale(updateScale);
    }

    public PointF getPosition() {
        return position;
    }

    public void setPosition(PointF position) {
        this.position = position;
    }

    public PointF getScale() {
        return Scale;
    }

    public void setScale(PointF scale) {
        Scale = scale;
    }

    public int getAddIndex() {
        return addIndex;
    }

    public void setAddIndex(int addIndex) {
        this.addIndex = addIndex;
    }

    public Point getDefaultOutputSize() {
        return defaultOutputSize;
    }

    public void setDefaultOutputSize(Point defaultOutputSize) {
        this.defaultOutputSize = defaultOutputSize;
    }

    public int getReuseIndex() {
        return reuseIndex;
    }

    public void setReuseIndex(int reuseIndex) {
        this.reuseIndex = reuseIndex;
    }
}

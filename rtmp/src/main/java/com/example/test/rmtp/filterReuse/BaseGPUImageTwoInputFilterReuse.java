package com.example.test.rmtp.filterReuse;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.test.rmtp.filter.gpuimage.BaseGPUImageTwoInputFilter;
import com.example.test.rmtp.filterReuse.record.ReuseBaseGPUImageTwoInputFilterRecord;
import com.example.test.rmtp.filterReuse.record.ReuseBaseObjectFilterRecord;
import com.pedro.encoder.input.gl.render.filters.BaseFilterRender;

public abstract class BaseGPUImageTwoInputFilterReuse<T extends BaseGPUImageTwoInputFilter,I extends ReuseBaseGPUImageTwoInputFilterRecord> extends BaseObjectFilterReuse{
    public BaseGPUImageTwoInputFilterReuse(I record) {
        super(record);
    }

    public void setFilterRecord(I filterRecord) {
        super.setFilterRecord(filterRecord);
    }

    public I getFilterRecord() {
        if(super.getFilterRecord() != null) {
            return (I) super.getFilterRecord();
        }else{
            return null;
        }
    }

    public abstract T generateGPUImageTwoInputFilter(I record);

    public int compareTo(BaseObjectFilterReuse o) {
        if(getFilterRecord() != null && o.getFilterRecord() != null){
            return getFilterRecord().compareTo(o.getFilterRecord());
        }else{
            return -1;
        }
    }


    @Override
    public BaseFilterRender generateFilter(ReuseBaseObjectFilterRecord record) {
        if(record instanceof ReuseBaseGPUImageTwoInputFilterRecord){
            I filterRecord=(I)record;
            return generateGPUImageTwoInputFilter(filterRecord);
        }
        return null;
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof BaseObjectFilterReuse){
            BaseObjectFilterReuse filterReuse=(BaseObjectFilterReuse)o;
            return compareTo(filterReuse);
        }
        return -1;
    }

    public void setReplaceBitmap(Bitmap bitmap){
        getFilterRecord().setReplaceBitmap(bitmap);
        if(getReusedFilter() != null) {
            getReusedFilter().release();
            setReusedFilter(generateFilter(getFilterRecord()));
            if(isVisible()) {
                reuse(attachCamera);
            }
        }
    }
}

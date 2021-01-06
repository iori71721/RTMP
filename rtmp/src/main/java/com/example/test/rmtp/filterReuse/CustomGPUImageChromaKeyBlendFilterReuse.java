package com.example.test.rmtp.filterReuse;

import android.graphics.Bitmap;

import com.example.test.rmtp.filter.gpuimage.CustomGPUImageChromaKeyBlendFilter;
import com.example.test.rmtp.filterReuse.record.ReuseCustomGPUImageChromaKeyBlendFilterRecord;

public class CustomGPUImageChromaKeyBlendFilterReuse extends BaseObjectFilterReuse<CustomGPUImageChromaKeyBlendFilter, ReuseCustomGPUImageChromaKeyBlendFilterRecord>{
    public CustomGPUImageChromaKeyBlendFilterReuse(ReuseCustomGPUImageChromaKeyBlendFilterRecord record) {
        super(record);
    }

    @Override
    public CustomGPUImageChromaKeyBlendFilter generateFilter(ReuseCustomGPUImageChromaKeyBlendFilterRecord record) {
        CustomGPUImageChromaKeyBlendFilter filter=new CustomGPUImageChromaKeyBlendFilter();
        filter.setBitmapValue(record.getReplaceBitmap());
        setReusedFilter(filter);
        return filter;
    }

    public void setReplaceBitmap(Bitmap bitmap){
        if(getReusedFilter() != null) {
            getReusedFilter().setBitmapValue(bitmap);
        }
        getFilterRecord().setReplaceBitmap(bitmap);
    }
}

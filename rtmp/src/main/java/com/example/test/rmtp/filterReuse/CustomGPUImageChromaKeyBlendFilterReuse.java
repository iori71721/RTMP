package com.example.test.rmtp.filterReuse;

import android.util.Log;

import com.example.test.rmtp.filter.gpuimage.CustomGPUImageChromaKeyBlendFilter;
import com.example.test.rmtp.filterReuse.record.ReuseCustomGPUImageChromaKeyBlendFilterRecord;

public class CustomGPUImageChromaKeyBlendFilterReuse extends BaseGPUImageTwoInputFilterReuse<CustomGPUImageChromaKeyBlendFilter, ReuseCustomGPUImageChromaKeyBlendFilterRecord>{
    public CustomGPUImageChromaKeyBlendFilterReuse(ReuseCustomGPUImageChromaKeyBlendFilterRecord record) {
        super(record);
    }

    @Override
    public CustomGPUImageChromaKeyBlendFilter generateGPUImageTwoInputFilter(ReuseCustomGPUImageChromaKeyBlendFilterRecord record) {
        CustomGPUImageChromaKeyBlendFilter filter=new CustomGPUImageChromaKeyBlendFilter();
        filter.setBitmapValue(record.getReplaceBitmap());
        setReusedFilter(filter);
        return filter;
    }
}

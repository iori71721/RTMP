package com.example.test.rmtp.filter.gpuimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.test.R;
import com.example.test.rmtp.util.OpenglUtil;

public class CustomGPUImageSoftLightFilter extends BaseGPUImageTwoInputFilter{
    public CustomGPUImageSoftLightFilter() {
        super("");
    }

    @Override
    protected String generateFragmentShader(Context context) {
        Bitmap softLightBitmap=BitmapFactory.decodeResource(context.getResources(),R.drawable.com_example_test_rmtp_gpuimage_soft_light_filter);
        setBitmapValue(softLightBitmap);
        return OpenglUtil.getStringFromRaw(context, R.raw.com_example_test_rmtp_util_gpuimage_soft_light_filter_fragment);
    }
}

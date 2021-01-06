package com.example.test.rmtp.filter.gpuimage;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.example.test.R;
import com.example.test.rmtp.util.OpenglUtil;

public class CusotmGPUImageBrightnessFilter extends BaseGPUImageFilter{
    private int brightnessLocation=12345;
    private float brightness;

    public CusotmGPUImageBrightnessFilter() {
        this(0.8f);
    }

    public CusotmGPUImageBrightnessFilter(final float brightness) {
        this.brightness = brightness;
    }

    @Override
    public void onInit() {
        super.onInit();
        brightnessLocation = GLES20.glGetUniformLocation(getProgram(), "brightness");
    }

    @Override
    protected void drawGpuImageFilter() {
        setBrightness(brightness);
    }

    public void setBrightnessValue(final float brightness) {
        this.brightness = brightness;
    }

    private void setBrightness(final float brightness) {
        this.brightness = brightness;
        setFloat(brightnessLocation, this.brightness);
    }

    @Override
    protected String generateVertexShader(Context context) {
        return OpenglUtil.getStringFromRaw(context, R.raw.com_example_test_rmtp_util_simple_vertex);
    }

    @Override
    protected String generateFragmentShader(Context context) {
        return OpenglUtil.getStringFromRaw(context,R.raw.com_example_test_rmtp_util_gpuimage_brightness_filter_fragment);
    }
}

package com.example.test.rmtp.filter.gpuimage;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.example.test.R;
import com.example.test.rmtp.util.OpenglUtil;

public class BeautifulFaceFilter extends BaseGPUImageFilter {
    public static float MAX_BEAUTIFUL=2;
    public static float MIN_BEAUTIFUL=0;
    private float toneLevel=0.47f;
    private float beautyLevel=0.42f;
    private float brightLevel=0.34f;
    private int paramsLocation;
    private int brightnessLocation;
    private int singleStepOffsetLocation;

    @Override
    public void onInit() {
        super.onInit();

        paramsLocation = GLES20.glGetUniformLocation(getProgram(), "params");
        brightnessLocation = GLES20.glGetUniformLocation(getProgram(), "brightness");
        singleStepOffsetLocation = GLES20.glGetUniformLocation(getProgram(), "singleStepOffset");
        setParams(beautyLevel, toneLevel);
        setBrightLevel(brightLevel);
    }

    @Override
    protected void drawGpuImageFilter() {
        setBeautyLevel(beautyLevel);
    }

    public static float fixedBeautiful(float setupBeautiful){
        if(setupBeautiful < MIN_BEAUTIFUL){
            return MIN_BEAUTIFUL;
        }else if(setupBeautiful > MAX_BEAUTIFUL){
            return MAX_BEAUTIFUL;
        }else{
            return setupBeautiful;
        }
    }

    public void setBeautyLevelValue(float beautyLevel) {
        beautyLevel=fixedBeautiful(beautyLevel);
        this.beautyLevel = beautyLevel;
    }

    private void setBeautyLevel(float beautyLevel) {
        beautyLevel=fixedBeautiful(beautyLevel);
        this.beautyLevel = beautyLevel;
        setParams(beautyLevel, toneLevel);
    }

    private void setBrightLevel(float brightLevel) {
        this.brightLevel = brightLevel;
        setFloat(brightnessLocation, 0.6f * (-0.5f + brightLevel));
    }

    private void setParams(float beauty, float tone) {
        float[] vector = new float[4];
        vector[0] = 1.0f - 0.6f * beauty;
        vector[1] = 1.0f - 0.3f * beauty;
        vector[2] = 0.1f + 0.3f * tone;
        vector[3] = 0.1f + 0.3f * tone;
        setFloatVec4(paramsLocation, vector);
    }

    private void setTexelSize(final float w, final float h) {
        setFloatVec2(singleStepOffsetLocation, new float[] {2.0f / w, 2.0f / h});
    }

    @Override
    public void onOutputSizeChanged(final int width, final int height) {
        super.onOutputSizeChanged(width, height);
        setTexelSize(width, height);
    }

    @Override
    protected String generateVertexShader(Context context) {
        return OpenglUtil.getStringFromRaw(context, R.raw.com_example_test_rmtp_util_simple_vertex);
    }

    @Override
    protected String generateFragmentShader(Context context) {
        return OpenglUtil.getStringFromRaw(context,R.raw.com_example_test_rmtp_util_gpuimage_beautiful_face_filter_fragment);
    }
}

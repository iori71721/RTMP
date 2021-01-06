package com.example.test.rmtp.filter.gpuimage;

import android.content.Context;
import android.opengl.GLES20;

import com.example.test.R;
import com.example.test.rmtp.util.OpenglUtil;

public class CustomGPUImageChromaKeyBlendFilter extends BaseGPUImageTwoInputFilter{
    private int thresholdSensitivityLocation;
    private int smoothingLocation;
    private int colorToReplaceLocation;
    private float thresholdSensitivity = 0.4f;
    private float smoothing = 0.1f;
    private float[] colorToReplace = new float[]{0.0f, 1.0f, 0.0f};

    public CustomGPUImageChromaKeyBlendFilter() {
        super("");
    }

    @Override
    public void onInit() {
        super.onInit();
        thresholdSensitivityLocation = GLES20.glGetUniformLocation(getProgram(), "thresholdSensitivity");
        smoothingLocation = GLES20.glGetUniformLocation(getProgram(), "smoothing");
        colorToReplaceLocation = GLES20.glGetUniformLocation(getProgram(), "colorToReplace");
    }

    public void setSmoothingValue(final float smoothing) {
        this.smoothing=smoothing;
    }

    /**
     * The degree of smoothing controls how gradually similar colors are replaced in the image
     * The default value is 0.1
     */
    protected void setSmoothing(final float smoothing) {
        setSmoothingValue(smoothing);
        setFloat(smoothingLocation, this.smoothing);
    }

    public void setThresholdSensitivityValue(final float thresholdSensitivity) {
        this.thresholdSensitivity = thresholdSensitivity;
    }

    /**
     * The threshold sensitivity controls how similar pixels need to be colored to be replaced
     * The default value is 0.3
     */
    protected void setThresholdSensitivity(final float thresholdSensitivity) {
        setThresholdSensitivityValue(thresholdSensitivity);
        setFloat(thresholdSensitivityLocation, this.thresholdSensitivity);
    }

    /**
     * The color to be replaced is specified using individual red, green, and blue components (normalized to 1.0).
     * The default is green: (0.0, 1.0, 0.0).
     *
     * @param redComponent   Red component of color to be replaced
     * @param greenComponent Green component of color to be replaced
     * @param blueComponent  Blue component of color to be replaced
     */
    protected void setColorToReplace(float redComponent, float greenComponent, float blueComponent) {
        colorToReplace = new float[]{redComponent, greenComponent, blueComponent};
        setFloatVec3(colorToReplaceLocation, colorToReplace);
    }

    @Override
    protected void drawGpuImageFilter() {
        super.drawGpuImageFilter();
        setSmoothing(smoothing);
        setThresholdSensitivity(thresholdSensitivity);
        setColorToReplace(colorToReplace[0], colorToReplace[1], colorToReplace[2]);
    }

    @Override
    protected String generateFragmentShader(Context context) {
        return OpenglUtil.getStringFromRaw(context, R.raw.com_example_test_rmtp_util_custom_gpuimage_chroma_key_blend_filter_fragment);
    }
}

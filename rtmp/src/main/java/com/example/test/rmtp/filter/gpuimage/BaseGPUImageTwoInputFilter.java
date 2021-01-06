package com.example.test.rmtp.filter.gpuimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.Log;

import com.example.test.R;
import com.example.test.rmtp.util.OpenglUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import jp.co.cyberagent.android.gpuimage.util.OpenGlUtils;
import jp.co.cyberagent.android.gpuimage.util.Rotation;
import jp.co.cyberagent.android.gpuimage.util.TextureRotationUtil;

public abstract class BaseGPUImageTwoInputFilter extends BaseGPUImageFilter{
    private int filterSecondTextureCoordinateAttribute;
    private int filterInputTextureUniform2;
    private int filterSourceTexture2 = OpenGlUtils.NO_TEXTURE;
    private ByteBuffer texture2CoordinatesBuffer;
    private Bitmap bitmap;

    public BaseGPUImageTwoInputFilter(String fragmentShader) {
        this("","");
    }

    public BaseGPUImageTwoInputFilter(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader);
        setRotation(Rotation.NORMAL, false, false);
    }

    @Override
    public void onInit() {
        super.onInit();

        filterSecondTextureCoordinateAttribute = GLES20.glGetAttribLocation(getProgram(), "inputTextureCoordinate2");
        filterInputTextureUniform2 = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture2"); // This does assume a name of "inputImageTexture2" for second input texture in the fragment shader
        GLES20.glEnableVertexAttribArray(filterSecondTextureCoordinateAttribute);
    }

    public void setBitmapValue(final Bitmap bitmap){
        if (bitmap != null && bitmap.isRecycled()) {
            return;
        }
        this.bitmap = bitmap;
    }

    protected void setBitmap(final Bitmap bitmap) {
        setBitmapValue(bitmap);
        if (this.bitmap == null) {
            return;
        }
        runOnDraw(new Runnable() {
            public void run() {
//                force setup to change bitmap
                filterSourceTexture2=OpenGlUtils.NO_TEXTURE;
                if (filterSourceTexture2 == OpenGlUtils.NO_TEXTURE) {
                    if (bitmap == null || bitmap.isRecycled()) {
                        return;
                    }
                    GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
                    filterSourceTexture2 = OpenGlUtils.loadTexture(bitmap, OpenGlUtils.NO_TEXTURE, false);
                }
            }
        });
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void recycleBitmap() {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    public void onDestroy() {
        super.onDestroy();
        GLES20.glDeleteTextures(1, new int[]{
                filterSourceTexture2
        }, 0);
        filterSourceTexture2 = OpenGlUtils.NO_TEXTURE;
    }

    @Override
    protected void onDrawArraysPre() {
        GLES20.glEnableVertexAttribArray(filterSecondTextureCoordinateAttribute);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, filterSourceTexture2);
        GLES20.glUniform1i(filterInputTextureUniform2, 3);

        texture2CoordinatesBuffer.position(0);
        GLES20.glVertexAttribPointer(filterSecondTextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, texture2CoordinatesBuffer);
    }

    public void setRotation(final Rotation rotation, final boolean flipHorizontal, final boolean flipVertical) {
        float[] buffer = TextureRotationUtil.getRotation(rotation, flipHorizontal, flipVertical);

        ByteBuffer bBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder());
        FloatBuffer fBuffer = bBuffer.asFloatBuffer();
        fBuffer.put(buffer);
        fBuffer.flip();

        texture2CoordinatesBuffer = bBuffer;
    }

    @Override
    protected void drawGpuImageFilter() {
        if (bitmap != null && !bitmap.isRecycled()) {
            setBitmap(bitmap);
        }
    }

    @Override
    protected String generateVertexShader(Context context) {
        return OpenglUtil.getStringFromRaw(context, R.raw.com_example_test_rmtp_util_base_gpu_image_two_input_filter_vertex);
    }
}

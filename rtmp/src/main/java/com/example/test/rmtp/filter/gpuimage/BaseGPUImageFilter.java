package com.example.test.rmtp.filter.gpuimage;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.util.Log;

import com.example.test.rmtp.filter.BaseCustomFilterRender;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.LinkedList;

/**
 * copy from GPUImageFilter
 */
public abstract class BaseGPUImageFilter extends BaseCustomFilterRender {
    private final LinkedList<Runnable> runOnDraw;
    private int glProgId;
    private int glAttribPosition;
    private int glUniformTexture;
    private int glAttribTextureCoordinate;
    private int outputWidth;
    private int outputHeight;
    private boolean isInitialized;

    public BaseGPUImageFilter() {
        this("","");
    }

    public BaseGPUImageFilter(final String vertexShader, final String fragmentShader) {
        runOnDraw = new LinkedList<>();
    }

    @Override
    protected void initGlFilter(Context context) {
        super.initGlFilter(context);
        init();
    }

    private final void init() {
        onInit();
        onInitialized();
    }

    public void onInit() {
        glProgId = program;
        glAttribPosition = aPositionHandle;
        glUniformTexture = uSamplerHandle;
        glAttribTextureCoordinate = aTextureHandle;
        isInitialized = true;
    }

    public void onInitialized() {
    }

    public void ifNeedInit() {
        if (!isInitialized) init();
    }

    public final void destroy() {
        isInitialized = false;
        GLES20.glDeleteProgram(glProgId);
        onDestroy();
    }

    public void onDestroy() {
    }

    protected abstract void drawGpuImageFilter();

    public void onDraw() {
        GLES20.glUseProgram(glProgId);
        runPendingOnDrawTasks();
        if (!isInitialized) {
            return;
        }
        drawGpuImageFilter();
        onDrawArraysPre();
    }

    protected void onDrawArraysPre() {
    }

    protected void runPendingOnDrawTasks() {
        while (!runOnDraw.isEmpty()) {
            runOnDraw.removeFirst().run();
        }
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public int getOutputWidth() {
        return outputWidth;
    }

    public int getOutputHeight() {
        return outputHeight;
    }

    public int getProgram() {
        return glProgId;
    }

    public int getAttribPosition() {
        return glAttribPosition;
    }

    public int getAttribTextureCoordinate() {
        return glAttribTextureCoordinate;
    }

    public int getUniformTexture() {
        return glUniformTexture;
    }

    protected void setInteger(final int location, final int intValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                ifNeedInit();
                GLES20.glUniform1i(location, intValue);
            }
        });
    }

    protected void setFloat(final int location, final float floatValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                ifNeedInit();
                GLES20.glUniform1f(location, floatValue);
            }
        });
    }

    protected void setFloatVec2(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                ifNeedInit();
                GLES20.glUniform2fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatVec3(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                ifNeedInit();
                GLES20.glUniform3fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatVec4(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                ifNeedInit();
                GLES20.glUniform4fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatArray(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                ifNeedInit();
                GLES20.glUniform1fv(location, arrayValue.length, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setPoint(final int location, final PointF point) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                ifNeedInit();
                float[] vec2 = new float[2];
                vec2[0] = point.x;
                vec2[1] = point.y;
                GLES20.glUniform2fv(location, 1, vec2, 0);
            }
        });
    }

    protected void setUniformMatrix3f(final int location, final float[] matrix) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                ifNeedInit();
                GLES20.glUniformMatrix3fv(location, 1, false, matrix, 0);
            }
        });
    }

    protected void setUniformMatrix4f(final int location, final float[] matrix) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                ifNeedInit();
                GLES20.glUniformMatrix4fv(location, 1, false, matrix, 0);
            }
        });
    }

    protected void runOnDraw(final Runnable runnable) {
        synchronized (runOnDraw) {
            runOnDraw.addLast(runnable);
        }
    }

    public static String loadShader(String file, Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream ims = assetManager.open(file);

            String re = convertStreamToString(ims);
            ims.close();
            return re;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public void onOutputSizeChanged(final int width, final int height) {
        outputWidth = width;
        outputHeight = height;
    }

    @Override
    protected void drawCustomFilter() {
        onOutputSizeChanged(getWidth(),getHeight());
        onDraw();
    }
}

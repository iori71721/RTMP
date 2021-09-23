package com.example.test.rmtp.filter;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.pedro.encoder.input.gl.render.filters.BaseFilterRender;
import com.pedro.encoder.utils.gl.GlUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class BaseCustomFilterRender extends BaseFilterRender {
    protected float[] squareVertexDataFilter = generateSquareVertexDataFilter();

    protected String vertexShader="";
    protected String fragmentShader="";

    protected int program = -1;
    protected int aPositionHandle = -1;
    protected int aTextureHandle = -1;
    protected int uMVPMatrixHandle = -1;
    protected int uSTMatrixHandle = -1;
    protected int uSamplerHandle = -1;

    public BaseCustomFilterRender() {
        baseInit();
    }

    protected void baseInit(){
        Matrix.setIdentityM(MVPMatrix, 0);
        Matrix.setIdentityM(STMatrix, 0);
        squareVertex = ByteBuffer.allocateDirect(squareVertexDataFilter.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        squareVertex.put(squareVertexDataFilter).position(0);
    }

    protected float[] generateSquareVertexDataFilter(){
        return new float[]{
                // X, Y, Z, U, V
                -1f, -1f, 0f, 0f, 0f, //bottom left
                1f, -1f, 0f, 1f, 0f, //bottom right
                -1f, 1f, 0f, 0f, 1f, //top left
                1f, 1f, 0f, 1f, 1f, //top right
        };
    }

    protected abstract String generateVertexShader(Context context);

    protected abstract String generateFragmentShader(Context context);

    protected void baseInitGl(Context context){
        vertexShader= generateVertexShader(context);
        fragmentShader=generateFragmentShader(context);
        program = GlUtil.createProgram(vertexShader, fragmentShader);
        aPositionHandle = GLES20.glGetAttribLocation(program, "aPosition");
        aTextureHandle = GLES20.glGetAttribLocation(program, "aTextureCoord");
        uMVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        uSTMatrixHandle = GLES20.glGetUniformLocation(program, "uSTMatrix");
        uSamplerHandle = GLES20.glGetUniformLocation(program, "uSampler");
    }

    protected void baseDrawFilter(){
        GLES20.glUseProgram(program);

        squareVertex.position(SQUARE_VERTEX_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(aPositionHandle, 3, GLES20.GL_FLOAT, false,
                SQUARE_VERTEX_DATA_STRIDE_BYTES, squareVertex);
        GLES20.glEnableVertexAttribArray(aPositionHandle);

        squareVertex.position(SQUARE_VERTEX_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(aTextureHandle, 2, GLES20.GL_FLOAT, false,
                SQUARE_VERTEX_DATA_STRIDE_BYTES, squareVertex);

        GLES20.glEnableVertexAttribArray(aTextureHandle);

        GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, MVPMatrix, 0);
        GLES20.glUniformMatrix4fv(uSTMatrixHandle, 1, false, STMatrix, 0);
    }

    protected void bindBaseTexture(){
        GLES20.glUniform1i(uSamplerHandle, 4);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, previousTexId);
    }

    protected abstract void drawCustomFilter();

    @Override
    protected void initGlFilter(Context context) {
        baseInitGl(context);
    }

    @Override
    protected void drawFilter() {
        baseDrawFilter();
        drawCustomFilter();
        bindBaseTexture();
    }

    @Override
    public void release() {
        GLES20.glDeleteProgram(program);
    }

    protected int getSquareVertexDataCount(){
        return 4;
    }

    /**
     * ref {@link BaseCustomFilterRender#baseDrawFilter()}
     */
    protected void reloadVertex(){
        squareVertex = ByteBuffer.allocateDirect(squareVertexDataFilter.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        squareVertex.put(squareVertexDataFilter).position(0);

        squareVertex.position(SQUARE_VERTEX_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(aPositionHandle, 3, GLES20.GL_FLOAT, false,
                SQUARE_VERTEX_DATA_STRIDE_BYTES, squareVertex);
        GLES20.glEnableVertexAttribArray(aPositionHandle);
    }

    @Override
    /**
     * ref super
     */
    public void draw() {
        GlUtil.checkGlError("drawFilter start");
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, getRenderHandler().getFboId()[0]);
        GLES20.glViewport(0, 0, getWidth(), getHeight());
        drawFilter();
        reloadVertex();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, getSquareVertexDataCount());
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GlUtil.checkGlError("drawFilter end");
    }
}

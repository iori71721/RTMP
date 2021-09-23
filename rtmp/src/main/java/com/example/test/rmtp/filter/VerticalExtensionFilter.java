package com.example.test.rmtp.filter;

import android.content.Context;

import com.example.test.R;
import com.example.test.rmtp.util.OpenglUtil;

public class VerticalExtensionFilter extends BaseCustomFilterRender{
    public static final float DEFAULT_EXTENSION=0.025f;

    private float extension;

    @Override
    protected float[] generateSquareVertexDataFilter() {
        return new float[]{
                // X, Y, Z, U, V
                -1f, 1f, 0f, 0f, 1f, //top left
                1.0f,1.0f, 0.0f, 1f, 1f, //top right
                -1f,  -1f, 0.0f,0f, 0f, //bottom left
                1.0f,-1.0f, 0.0f, 1f, 0f //bottom right
        };
    }

    /**
     *
     * @param extension
     */
    private void calcAndReplaceNewVertex(float extension) {
        float[] originalVertex=generateSquareVertexDataFilter();

//        left top
        float[] leftTop=new float[]{originalVertex[0],originalVertex[1]};
        leftTop[1]+=extension;
//        right top
        float[] rightTop=new float[]{originalVertex[5],originalVertex[6]};
        rightTop[1]+=extension;
//        left bottom
        float[] leftBottom=new float[]{originalVertex[10],originalVertex[11]};
        leftBottom[1]-=extension;
//        right bottom
        float[] rightBottom=new float[]{originalVertex[15],originalVertex[16]};
        rightBottom[1]-=extension;

        squareVertexDataFilter[1]=leftTop[1];
        squareVertexDataFilter[6]=rightTop[1];
        squareVertexDataFilter[11]=leftBottom[1];
        squareVertexDataFilter[16]=rightBottom[1];
    }

    public void incExtension(){
        setExtension(extension+DEFAULT_EXTENSION);
    }

    public void decExtension(){
        setExtension(extension-DEFAULT_EXTENSION);
    }

    @Override
    protected String generateVertexShader(Context context) {
        return OpenglUtil.getStringFromRaw(context, R.raw.com_example_test_rmtp_util_simple_vertex);
    }

    @Override
    protected String generateFragmentShader(Context context) {
        return OpenglUtil.getStringFromRaw(context,R.raw.com_example_test_rmtp_util_simple_fragment);
    }

    @Override
    protected int getSquareVertexDataCount() {
        return 4;
    }

    @Override
    protected void drawCustomFilter() {
        calcAndReplaceNewVertex(extension);
    }

    public float getExtension() {
        return extension;
    }

    public void setExtension(float extension) {
        if(extension < 0){
            extension=0;
        }
        this.extension = extension;
    }
}

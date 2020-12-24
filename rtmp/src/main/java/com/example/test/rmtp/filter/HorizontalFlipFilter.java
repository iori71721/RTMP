package com.example.test.rmtp.filter;

import android.content.Context;

import com.example.test.R;
import com.example.test.rmtp.util.OpenglUtil;

public class HorizontalFlipFilter extends BaseCustomFilterRender {

    @Override
    protected float[] generateSquareVertexDataFilter(){
        return new float[]{
                // X, Y, Z, U, V
                -1f, -1f, 0f, 1f, 0f, //bottom left
                1f, -1f, 0f, 0f, 0f, //bottom right
                -1f, 1f, 0f, 1f, 1f, //top left
                1f, 1f, 0f, 0f, 1f, //top right
        };
    }

    @Override
    protected String generateVertexShader(Context context) {
        return OpenglUtil.getStringFromRaw(context, R.raw.com_example_test_rmtp_util_simple_vertex);
    }

    @Override
    protected String generateFragmentShader(Context context) {
        return OpenglUtil.getStringFromRaw(context,R.raw.com_example_test_rmtp_util_simple_fragment);
    }

    protected void drawCustomFilter(){

    }
}

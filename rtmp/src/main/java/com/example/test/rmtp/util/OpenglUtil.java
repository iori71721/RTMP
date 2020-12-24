package com.example.test.rmtp.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class OpenglUtil {
    public static Bitmap decodeBitmapFromAssets(Context context, String filename){
        Bitmap bitmap=null;
        BitmapFactory.Options options= new BitmapFactory.Options();
        options.inSampleSize=1;
        try {
            bitmap=BitmapFactory.decodeStream(context.getAssets().open(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static String getStringFromRaw(Context context, int id) {
        String str;
        try {
            Resources r = context.getResources();
            InputStream is = r.openRawResource(id);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                baos.write(i);
                i = is.read();
            }
            str = baos.toString();
            is.close();
        } catch (IOException e) {
            str = "";
        }
        return str;
    }

    public static void checkGLError(){
        checkGLError("glError");
    }

    public static void checkGLError(String msg){
        int error = GLES20.glGetError();
        if(error != GLES20.GL_NO_ERROR){
            String hexErrorCode=Integer.toHexString(error);
            Log.e(msg, "glError: "+hexErrorCode);
            throw new RuntimeException("GLError");
        }
    }

    public static int createTexture(){
        int[] textureID=new int[]{0};
        GLES20.glGenTextures(textureID.length, textureID, 0);
        int createTextureID = textureID[0];
        return createTextureID;
    }

    /**
     * Frame buffer id
     * @return
     */
    public static int createFrameBuffer(){
        int[] frameBuffers = new int[]{0};
        GLES20.glGenFramebuffers(frameBuffers.length, frameBuffers, 0);
        int frameBuffer = frameBuffers[0];
        return frameBuffer;
    }

    /**
     * texture id
     * @return
     */
    public static int createImageTexture(){
        int imageTextureID=createTexture();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imageTextureID);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        return imageTextureID;
    }

    /**
     *
     * @param vertexShaderCode
     * @param fragmentShaderCode
     * @return program id
     */
    public static int createGLProgram(String vertexShaderCode,String fragmentShaderCode){
        int programId = GLES20.glCreateProgram();

        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        int fragmentShader= GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(vertexShader, vertexShaderCode);
        GLES20.glShaderSource(fragmentShader, fragmentShaderCode);
        GLES20.glCompileShader(vertexShader);
        GLES20.glCompileShader(fragmentShader);

        GLES20.glAttachShader(programId, vertexShader);
        GLES20.glAttachShader(programId, fragmentShader);

        GLES20.glLinkProgram(programId);

        OpenglUtil.checkGLError();

        return programId;
    }

    /**
     *
     * @param imageTextureWidth
     * @param imageTextureHeight
     * @param framebufferID
     * @param imageTextureID
     * @return frame buffer id
     */
    public static int bindImageTextureToFrameBuffer(int imageTextureWidth,int imageTextureHeight,int framebufferID,int imageTextureID){
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imageTextureID);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, imageTextureWidth, imageTextureHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebufferID);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, imageTextureID, 0);
        return framebufferID;
    }

    public static int loadBitmapFromAssets(Context context, String filePathByAssets, int imageTextureID){
        Bitmap bitmap = OpenglUtil.decodeBitmapFromAssets(context,filePathByAssets);
        ByteBuffer b = ByteBuffer.allocateDirect(bitmap.getWidth() * bitmap.getHeight() * 4);
        bitmap.copyPixelsToBuffer(b);
        b.position(0);

//            load bitmap to texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imageTextureID);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,
                0,
                GLES20.GL_RGBA,
                bitmap.getWidth(),
                bitmap.getHeight(),
                0,
                GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE,
                b);
        return imageTextureID;
    }

    public static int loadDataToAttribute(int programId, String attributeName, int attributeComponentCount, int attrTypeByOpengl, Buffer loadBuffer){
        GLES20.glUseProgram(programId);
        int attributeLocation = GLES20.glGetAttribLocation(programId, attributeName);
        GLES20.glEnableVertexAttribArray(attributeLocation);
        GLES20.glVertexAttribPointer(attributeLocation, attributeComponentCount, attrTypeByOpengl, false,0, loadBuffer);
        return attributeLocation;
    }

    public static FloatBuffer createFloatBuffer(float[] data){
        FloatBuffer createBuffer=ByteBuffer.allocateDirect(data.length*4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        createBuffer.put(data);
        createBuffer.position(0);
        return createBuffer;
    }
}

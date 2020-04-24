package org.yanzi.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import java.io.IOException;

public class CameraInterface {
    private static final String TAG="CameraInstance";
    private Camera mCamera;
    private Camera.Parameters mParams;
    private static CameraInterface mCameraInstance;
    private boolean isPreviewing=false;
    private boolean isOpened=false;
    private Camera.Size mPreviewSize;
    private Camera.Size mPictureSize;
    public interface CamOpenedCallback{
        public void cameraHasOpened();
    }
    public boolean isPreviewing(){
        return isPreviewing;
    }
    public boolean isOpened(){
        return isOpened;
    }
    public Camera.Size getmPreviewSize(){return mPreviewSize;}

    private CameraInterface(){

    }
    public static synchronized CameraInterface getInstance(){
        if(mCameraInstance == null){
            mCameraInstance = new CameraInterface();
        }
        return mCameraInstance;
    }

    public void doOpenCamera(CamOpenedCallback callback){
//        will do opengl render
        Log.i(TAG, "doOpenCamera....");
        if(mCamera == null){
            mCamera = Camera.open();
            isOpened=true;
            Log.i(TAG, "Camera open over....");
            if(callback != null){
                callback.cameraHasOpened();
            }
        }else{
            Log.i(TAG, "Camera is in open status");
        }


    }

    public void doStartPreview(SurfaceTexture surface, float previewRate){
        Log.i(TAG, "doStartPreview...");
        if(isPreviewing){
            Log.e(TAG,"camera is in previewing state");
            return ;
        }
        if(mCamera != null){
            try {
                mCamera.setPreviewTexture(surface);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            initCameraParams();
        }

    }
    /**
     * 停止预览，释放Camera
     */
    public void doStopCamera(){
        if(null != mCamera)
        {
            isOpened=false;
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            isPreviewing = false;
            mCamera.release();
            mCamera = null;
        }
    }
    public void doStopPreview(){
        Log.e(TAG,"doStopPreview....");
        if (isPreviewing && null!=mCamera){
            mCamera.stopPreview();
            isPreviewing=false;
        }else{
            Log.e(TAG,"camera is in not in previewing status");
        }
    }

    public void doTakePicture(){
        if(isPreviewing && (mCamera != null)){
            mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
        }
    }
//    private void initCameraParams(){
//        if(mCamera != null){
//
//            mParams = mCamera.getParameters();
//            mParams.setPictureFormat(PixelFormat.JPEG);
//
//            mPictureSize = CamParaUtil.getInstance().getPropPictureSize(
//                    mParams.getSupportedPictureSizes(),30, 800);
//            mParams.setPictureSize(mPictureSize.width, mPictureSize.height);
//            mPreviewSize = CamParaUtil.getInstance().getPropPreviewSize(
//                    mParams.getSupportedPreviewSizes(), 30, 800);
//            mParams.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
//
//            mCamera.setDisplayOrientation(90);
//
//            List<String> focusModes = mParams.getSupportedFocusModes();
//            if(focusModes.contains("continuous-video")){
//                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
//            }
//            mCamera.setParameters(mParams);
//            mCamera.startPreview();
//
//            isPreviewing = true;
//            mParams = mCamera.getParameters();
//
//        }
//    }

    Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback()
            //快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
    {
        public void onShutter() {
            // TODO Auto-generated method stub
            Log.i(TAG, "myShutterCallback:onShutter...");
        }
    };
    Camera.PictureCallback mRawCallback = new Camera.PictureCallback()
            // 拍摄的未压缩原数据的回调,可以为null
    {

        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Log.i(TAG, "myRawCallback:onPictureTaken...");

        }
    };
    Camera.PictureCallback mJpegPictureCallback = new Camera.PictureCallback ()
            //对jpeg图像数据的回调,最重要的一个回调
    {
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Log.i(TAG, "myJpegCallback:onPictureTaken...");
            Bitmap b = null;
            if(null != data){
                b = BitmapFactory.decodeByteArray(data, 0, data.length);
                mCamera.stopPreview();
                isPreviewing = false;
            }

            if(null != b)
            {
                Bitmap rotaBitmap =null;
//                        ImageUtil.getRotateBitmap(b, 90.0f);
//                FileUtil.saveBitmap(rotaBitmap);
            }

            mCamera.startPreview();
            isPreviewing = true;
        }
    };
}

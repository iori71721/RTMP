package com.example.test.rmtp;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.test.rmtp.filterReuse.FilterReusedManager;
import com.pedro.encoder.input.video.CameraHelper;
import com.pedro.rtplibrary.rtmp.RtmpCamera2;
import com.pedro.rtplibrary.view.OpenGlView;

import net.ossrs.rtmp.ConnectCheckerRtmp;

public abstract class BaseRtmpActivity extends AppCompatActivity implements ConnectCheckerRtmp, SurfaceHolder.Callback{
    public static int REQUEST_CODE_PERMISSIONS=11;
    protected OpenGlView surfaceView;
    protected RtmpCamera2 rtmpCamera1;
    private int fixedRotation;

    private final int defaultWidth=720;
    private final int defaultHeight=1280;

    protected CameraHelper.Facing usedCameraFacing=CameraHelper.Facing.FRONT;
    protected int usedCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private int streamWidth=480;
    private int streamHeight=854;
    private int preViewWidth=defaultWidth;
    private int preViewHeight=defaultHeight;

    private Button start_stream;

    private Button button_stop_stream;

    private Button button_switch_camera;

    protected FilterReusedManager filterReusedManager=new FilterReusedManager();

    protected String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Process result from permission request dialog box, has the request
     * been granted? If yes, start Camera. Otherwise display a toast
     */
    protected boolean allPermissionsGranted(){
        for(String checkPermission:PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this,checkPermission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    public static int fetchCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        int result=0;
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();

        String rotationString="";

        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        rotationString+=degrees;

        Log.d("iori_rotation", "setCameraDisplayOrientation: view rotation "+rotationString+" degrees "+degrees);
        Log.d("iori_rotation", "setCameraDisplayOrientation: camera original rotation "+info.orientation);

//        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror

            Log.d("iori_rotation", "setCameraDisplayOrientation: front camera fix "+result);

        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;

            Log.d("iori_rotation", "setCameraDisplayOrientation: back camera fix "+result);

        }
        return result;
    }

    protected abstract @LayoutRes int generateContentViewID();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(generateContentViewID());

        surfaceView=findViewById(R.id.surfaceView);
        start_stream=findViewById(R.id.button_start_stream);
        button_stop_stream=findViewById(R.id.button_stop_stream);
        button_switch_camera=findViewById(R.id.button_switch_camera);

        surfaceView.getHolder().addCallback(this);

        final String fbRmtpServer="rtmps://live-api-s.facebook.com:443/rtmp/";
        final String streamID="106631727788299?s_bl=1&s_ps=1&s_psm=1&s_sw=0&s_vt=api-s&a=Abxqgg7u0LOy8Kpx";
        final String url=fbRmtpServer+streamID;

// Request camera permissions
        if(!allPermissionsGranted()){
            ActivityCompat.requestPermissions(this,PERMISSIONS,REQUEST_CODE_PERMISSIONS);
        }
        rtmpCamera1=new RtmpCamera2(surfaceView,this);
        filterReusedManager.init(rtmpCamera1);

        start_stream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fixedRotation = CameraHelper.getCameraOrientation(BaseRtmpActivity.this);
                fixedRotation=fetchCameraDisplayOrientation(BaseRtmpActivity.this, usedCameraID,null);
                if (rtmpCamera1.prepareAudio() && rtmpCamera1.prepareVideo(streamHeight, streamWidth, 24, 1200 * 1024, false, fixedRotation)) {
                    rtmpCamera1.startStream(url);
                    reuseFilters();
                } else {
                    /**This device cant init encoders, this could be for 2 reasons: The encoder selected doesnt support any configuration setted or your device hasnt a H264 or AAC encoder (in this case you can see log error valid encoder not found)*/
                }
            }
        });

        button_stop_stream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rtmpCamera1.isStreaming()){
                    rtmpCamera1.stopStream();
                }
            }
        });

        button_switch_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rtmpCamera1.switchCamera();
            }
        });


    }

    private void reuseFilters(){
        filterReusedManager.reusedFiltersByAddIndex();
        filterReusedManager.resetOperateFilter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        filterReusedManager.destory();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        fixedRotation=fetchCameraDisplayOrientation(this, usedCameraID,null);
        rtmpCamera1.startPreview(usedCameraFacing,preViewHeight,preViewWidth,fixedRotation);
        fetchCameraDisplayOrientation(this, usedCameraID,null);
        reuseFilters();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (rtmpCamera1.isStreaming()) {
            rtmpCamera1.stopStream();
        }
        rtmpCamera1.stopPreview();
    }

    @Override
    public void onConnectionSuccessRtmp() {
    }

    @Override
    public void onConnectionFailedRtmp(@NonNull String reason) {
    }

    @Override
    public void onNewBitrateRtmp(long bitrate) {

    }

    @Override
    public void onDisconnectRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseRtmpActivity.this,"connection error",Toast.LENGTH_SHORT).show();
            }
        });
        rtmpCamera1.stopStream();
    }

    @Override
    public void onAuthErrorRtmp() {

    }

    @Override
    public void onAuthSuccessRtmp() {

    }
}

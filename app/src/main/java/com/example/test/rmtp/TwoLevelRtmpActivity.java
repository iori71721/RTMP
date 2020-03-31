package com.example.test.rmtp;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.test.rmtp.filterReuse.FilterReusedManager;
import com.example.test.rmtp.refactor.FixSpriteGestureController;
import com.example.test.rmtp.service.StreamService;
import com.pedro.encoder.input.gl.SpriteGestureController;
import com.pedro.encoder.input.video.CameraHelper;
import com.pedro.rtplibrary.rtmp.RtmpCamera2;
import com.pedro.rtplibrary.view.OpenGlView;

import net.ossrs.rtmp.ConnectCheckerRtmp;

public class TwoLevelRtmpActivity extends AppCompatActivity implements ConnectCheckerRtmp, SurfaceHolder.Callback
        , View.OnTouchListener{
    public static int REQUEST_CODE_PERMISSIONS=10;

    private SpriteGestureController spriteGestureController = new FixSpriteGestureController();

    private RtmpCamera2 previewCamera;
    private OpenGlView previewSurfaceView;

//    private RtmpCamera2 streamCamera;
    private OpenGlView streamSurfaceView;

    private int fixedRotation;

    private String streamUrl;

    private int fps=24;
    private final int defaultWidth=720;
    private final int defaultHeight=1280;

    private final CameraHelper.Facing usedCameraFacing=CameraHelper.Facing.FRONT;
    private final int usedCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private int streamWidth=480;
    private int streamHeight=854;
    private int preViewWidth=defaultWidth;
    private int preViewHeight=defaultHeight;

    private Button start_stream;
    private Button add_image1;
    private Button add_image2;

    private Button add_snow;
    private Button remove_snow;
    private Button add_adnroid_button;
    private Button image1_visible_setup;

    private final FilterReusedManager filterReusedManager=new FilterReusedManager();

    private final String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("iori", "onCreate: 1");
        Toast.makeText(this,"preview width "+preViewWidth+" height "+preViewHeight,Toast.LENGTH_LONG).show();
        setContentView(R.layout.activity_two_level_rtmp);

        initLayout();

        final String fbRmtpServer="rtmps://live-api-s.facebook.com:443/rtmp/";
        final String streamID="120447366230813?s_bl=1&s_ps=1&s_sw=0&s_vt=api-s&a=AbzYN8Q9TUqDmsNd";

        streamUrl=fbRmtpServer+streamID;

        checkPermissions();
        detectScreenRotation();
        setupCamera();



//        new code
        StreamService.init_example(this);
        start_stream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),StreamService.class);


                previewCamera.stopPreview();


                startForegroundService(intent);


                previewSurfaceView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("iori_stream_service", "run: thread delay start preview camera preview");
                        previewCamera.startPreview();
                    }
                },20000);


            }
        });

        streamSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d("iori_surfaceChanged", "surfaceCreated: streamSurfaceView ");
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.d("iori_surfaceChanged", "surfaceChanged: streamSurfaceView ");
                StreamService.setView_example(streamSurfaceView);
//                StreamService.startPreview_example();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d("iori_surfaceChanged", "surfaceDestroyed: streamSurfaceView ");
                StreamService.setView_example(getApplicationContext());
                StreamService.stopPreview_example();
            }
        });

        previewSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d("iori_surfaceChanged", "surfaceCreated: previewSurfaceView ");
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.d("iori_surfaceChanged", "surfaceChanged: previewSurfaceView ");
                previewCamera.startPreview();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d("iori_surfaceChanged", "surfaceDestroyed: previewSurfaceView ");
                previewCamera.stopPreview();
            }
        });


    }

    @Override
    public void onResume(){
        super.onResume();
        if(isMyServiceRunning(StreamService.class)){

        }
    }

    private void initLayout(){
        previewSurfaceView =findViewById(R.id.preview_surfaceView);
        streamSurfaceView=findViewById(R.id.stream_surfaceView);
        start_stream=findViewById(R.id.button_start_stream);
        add_image1=findViewById(R.id.add_image1);
        add_image2=findViewById(R.id.add_image2);
        add_snow=findViewById(R.id.add_snow);
        remove_snow=findViewById(R.id.remove_snow);
        add_adnroid_button=findViewById(R.id.add_android_button);
        image1_visible_setup=findViewById(R.id.image1_visible_setup);
        setupTrigger();
    }

    private void setupTrigger(){
        start_stream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStream(streamUrl);
            }
        });
    }

    private void startStream(String url){
        Log.d("iori_stream_service", "startStream: ");
        startForegroundService(new Intent(this, StreamService.class));

        previewCamera.stopPreview();

        Log.d("iori_stream_service", "preview camera stop preview ");

        previewSurfaceView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("iori_stream_service", "run: preview camera start preview ");
                previewCamera.startPreview();
            }
        },30000);
    }

    /**
     * fixed by lib
     * @param rotation
     * @param width
     * @param height
     * @return
     */
    private Point fixOutputSpec(int rotation,int width,int height){
        if((rotation == 90 || rotation == 270)){
            return new Point(height,width);
        }else{
            return new Point(width,height);
        }
    }



    private void checkPermissions(){
// Request camera permissions
        if(!allPermissionsGranted()){
            ActivityCompat.requestPermissions(this,PERMISSIONS,REQUEST_CODE_PERMISSIONS);
        }
    }

    private void setupCamera(){
        previewCamera=new RtmpCamera2(previewSurfaceView,this);
    }

    private void detectScreenRotation(){
        fixedRotation=RtmpActivity.fetchCameraDisplayOrientation(this, usedCameraID,null);
    }

    /**
     * Process result from permission request dialog box, has the request
     * been granted? If yes, start Camera. Otherwise display a toast
     */
    private boolean allPermissionsGranted(){
        for(String checkPermission:PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this,checkPermission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("iori", "surfaceChanged: ");
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopStream();
        stopPreview();
    }

    private void startPreview(){
        Point fixOutputSpec=fixOutputSpec(fixedRotation,preViewWidth,preViewHeight);
        previewCamera.startPreview(usedCameraFacing,fixOutputSpec.x,fixOutputSpec.y,fixedRotation);
        Point fixStreamSpec=fixOutputSpec(fixedRotation,streamWidth,streamHeight);
//        streamCamera.startPreview(usedCameraFacing,fixStreamSpec.x,fixStreamSpec.y,fixedRotation);
    }

    private void stopPreview(){
        previewCamera.stopPreview();
//        streamCamera.stopPreview();
    }

    private void stopStream(){
//        if(streamCamera.isStreaming()) {
//            streamCamera.stopStream();
//        }

//        previewCamera.stopStream();

    }

    private boolean isMyServiceRunning(Class serviceClass){
        ActivityManager manager=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service:manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onConnectionSuccessRtmp() {

    }

    @Override
    public void onConnectionFailedRtmp(@NonNull String reason) {
        stopStream();
    }

    @Override
    public void onNewBitrateRtmp(long bitrate) {

    }

    @Override
    public void onDisconnectRtmp() {

    }

    @Override
    public void onAuthErrorRtmp() {

    }

    @Override
    public void onAuthSuccessRtmp() {

    }

    private class FilterName{
        private static final String addAndroidView="addAndroidView";
        private static final String addImageButton1="addImageButton1";
        private static final String addImageButton2="addImageButton2";
        private static final String addSnow="addSnow";
    }
}

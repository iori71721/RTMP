package com.example.test.rmtp;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.test.rmtp.adapter.FriendAdapter;
import com.example.test.rmtp.filterReuse.AndroidViewFilterReuse;
import com.example.test.rmtp.filterReuse.BaseObjectFilterReuse;
import com.example.test.rmtp.filterReuse.FilterReusedManager;
import com.example.test.rmtp.filterReuse.ImageFilterReuse;
import com.example.test.rmtp.filterReuse.NonFilterReuse;
import com.example.test.rmtp.filterReuse.record.ReuseAndroidViewFilterRecord;
import com.example.test.rmtp.filterReuse.record.NonReleaseObjectFilterRecord;
import com.example.test.rmtp.filterReuse.record.ReuseNonUpdateFilterRecord;
import com.pedro.encoder.input.gl.render.filters.AndroidViewFilterRender;
import com.pedro.encoder.input.gl.render.filters.SnowFilterRender;
import com.pedro.encoder.input.gl.render.filters.object.SurfaceFilterRender;
import com.pedro.encoder.input.video.Camera1ApiManager;
import com.pedro.encoder.input.video.CameraHelper;
import com.pedro.encoder.utils.gl.TranslateTo;
import com.pedro.rtplibrary.rtmp.RtmpCamera2;
import com.pedro.rtplibrary.view.OpenGlView;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import java.util.ArrayList;
import java.util.List;

public class RtmpActivity extends AppCompatActivity implements ConnectCheckerRtmp, SurfaceHolder.Callback
    , View.OnTouchListener {
    public static int REQUEST_CODE_PERMISSIONS=10;

    private OpenGlView surfaceView;
    private RtmpCamera2 rtmpCamera1;
    private RtmpCamera2 onlyPreviewCamera;

    private int fixedRotation;

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

    private Button add_fly_button;
    private Button fly_button;

    private final FilterReusedManager filterReusedManager=new FilterReusedManager();

    private final String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private int moveCount=100;

    private ListView list_friend;
    private FriendAdapter friendAdapter;
    private List<FriendAdapter.Item> friendAdapterItems=new ArrayList<>(100);
    private Button show_friends;

    private SurfaceFilterRender cameraRender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("iori", "onCreate: 2");
        Toast.makeText(this,"preview width "+preViewWidth+" height "+preViewHeight,Toast.LENGTH_LONG).show();
        setContentView(R.layout.activity_rmtp);

        surfaceView=findViewById(R.id.surfaceView);
        start_stream=findViewById(R.id.button_start_stream);
        add_image1=findViewById(R.id.add_image1);
        add_image2=findViewById(R.id.add_image2);
        add_snow=findViewById(R.id.add_snow);
        remove_snow=findViewById(R.id.remove_snow);
        add_adnroid_button=findViewById(R.id.add_android_button);
        image1_visible_setup=findViewById(R.id.image1_visible_setup);
        add_fly_button =findViewById(R.id.add_fly_button);
        fly_button=findViewById(R.id.fly_button);
        list_friend=findViewById(R.id.list_friend);
        show_friends=findViewById(R.id.show_friends);

        surfaceView.getHolder().addCallback(this);
        surfaceView.setOnTouchListener(this);

//        autoSetupStreamSize(usedCameraID);
//        autosetupPreViewSize(usedCameraID);

        final String fbRmtpServer="rtmps://live-api-s.facebook.com:443/rtmp/";
        final String streamID="122127136062836?s_bl=1&s_ps=1&s_sw=0&s_vt=api-s&a=Abyqk4b7aMH_bWnH";
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
                fixedRotation = CameraHelper.getCameraOrientation(RtmpActivity.this);
                fixedRotation=fetchCameraDisplayOrientation(RtmpActivity.this, usedCameraID,null);
                Log.d("iori", "onClick: fixedRotation "+fixedRotation);
                Log.d("iori", "onClick: surfaceView "+surfaceView.getWidth()+" height "+surfaceView.getHeight());
                Log.d("iori_FilterReusedManager_update", "onClick: will start stream update filter info");
                if (rtmpCamera1.prepareAudio() && rtmpCamera1.prepareVideo(streamHeight, streamWidth, 24, 1200 * 1024, false, fixedRotation)) {
                    rtmpCamera1.startStream(url);
                    filterReusedManager.reusedFiltersByAddIndex();
                    filterReusedManager.resetOperateFilter();
                } else {
                    /**This device cant init encoders, this could be for 2 reasons: The encoder selected doesnt support any configuration setted or your device hasnt a H264 or AAC encoder (in this case you can see log error valid encoder not found)*/
                }
            }
        });

        add_image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImageToStream();
            }
        });

        add_image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImageToStream2();
            }
        });

        add_snow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSnow(v);
            }
        });

        remove_snow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterReusedManager.deprecatedFilter(FilterName.ADD_SNOW);
            }
        });

        add_adnroid_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAndroidView(v);
            }
        });

        image1_visible_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseObjectFilterReuse controlFilter=filterReusedManager.fetchFilter(FilterName.ADD_IMAGE_BUTTON1);
                if(controlFilter != null){
                    if(controlFilter.isVisible()){
                        filterReusedManager.visibleFilter(FilterName.ADD_IMAGE_BUTTON1,false);
                        image1_visible_setup.setText("image1 visible true");
                    }else{
                        filterReusedManager.visibleFilter(FilterName.ADD_IMAGE_BUTTON1,true);
                        image1_visible_setup.setText("image1 visible false");
                    }
                }
            }
        });

        add_fly_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFlyButton(v);
            }
        });

        show_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list_friend.setVisibility(View.VISIBLE);
                list_friend.setMinimumHeight(600);
                addAndroidView(list_friend,FilterName.LIST_FRIEND);
                list_friend.setX(0);
                list_friend.setY(0);
                int fixy=-1;

                setupFriendListFilterPosition(50,0+fixy);
            }
        });

        initFriendList();
//        will do camera in surface
//        initCameraSurface();
//        start_stream.post(new Runnable() {
//            @Override
//            public void run() {
//                setupPreviewCamerainCamerax();
//            }
//        });


    }

    private void initCameraSurface(){
//        SurfaceFilterRender surfaceFilterRender = new SurfaceFilterRender();
//        rtmpCamera1.getGlInterface().setFilter(surfaceFilterRender);
//        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.big_bunny_240p);
//        mediaPlayer.setSurface(surfaceFilterRender.getSurface());
//        mediaPlayer.start();
//        //Video is 360x240 so select a percent to keep aspect ratio (50% x 33.3% screen)
//        surfaceFilterRender.setScale(50f, 33.3f);
//        spriteGestureController.setBaseObjectFilterRender(surfaceFilterRender); //Optional


        cameraRender=new SurfaceFilterRender(new SurfaceFilterRender.SurfaceReadyCallback(){

            @Override
            public void surfaceReady() {
                Camera1ApiManager camera1ApiManager =
                        new Camera1ApiManager(cameraRender.getSurfaceTexture(), getApplicationContext());
                camera1ApiManager.start(CameraHelper.Facing.FRONT, 640, 480, 30);
            }
        });

        cameraRender.getSurface();

//        Camera1ApiManager camera1ApiManager =
//                new Camera1ApiManager(cameraRender.getSurfaceTexture(), getApplicationContext());
//        camera1ApiManager.start(CameraHelper.Facing.FRONT, 640, 480, 30);




//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Camera1ApiManager camera1ApiManager =
//                        new Camera1ApiManager(cameraRender.getSurfaceTexture(), getApplicationContext());
//                camera1ApiManager.start(CameraHelper.Facing.BACK, 640, 480, 30);
//            }
//        }, 3000);




//        cameraRender.setScale(50f,33.3f);
        cameraRender.setScale(30f,20f);
        cameraRender.setPosition(0,0);
        NonFilterReuse<SurfaceFilterRender> cameraFilterReuse=new NonFilterReuse<>(new ReuseNonUpdateFilterRecord(),SurfaceFilterRender.class);
        filterReusedManager.addFilter(cameraFilterReuse,FilterName.CAMERA_SURFACE);
    }

    private TextureView viewFinder;

    private void updateTransform(){

        Matrix matrix = new Matrix();

//        val matrix = Matrix()

        // Compute the center of the view finder
        float centerX=viewFinder.getWidth()/2f;
        float centerY=viewFinder.getHeight()/2f;

//        val centerX = viewFinder.width / 2f
//        val centerY = viewFinder.height / 2f

        // Correct preview output to account for display rotation
        int rotationDegress =-999;
        switch (viewFinder.getDisplay().getRotation()) {
            case Surface.ROTATION_0:
                rotationDegress = 0;
                break;
            case Surface.ROTATION_90:
                rotationDegress = 90;
                break;
            case Surface.ROTATION_180:
                rotationDegress = 180;
                break;
            case Surface.ROTATION_270:
                rotationDegress = 270;
                break;
        }
        matrix.postRotate(new Float(-rotationDegress),centerX,centerY);


        /*val rotationDegrees = when(viewFinder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
        else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)*/




        // Finally, apply transformations to our TextureView
        viewFinder.setTransform(matrix);


    }

    private void setupPreviewCamerainCamerax(){
//        viewFinder=findViewById(R.id.view_finder);
        viewFinder.bringToFront();
        cameraRender=new SurfaceFilterRender();
        final PreviewConfig previewConfig=new PreviewConfig.Builder().setTargetResolution(new Size(600,480))
                .setLensFacing(CameraX.LensFacing.BACK)
                .build();


        /*val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(640, 480))
        }.build()*/


        // Build the viewfinder use case

        final Preview preview=new Preview(previewConfig);

        // Every time the viewfinder is updated, recompute layout
        preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
            @Override
            public void onUpdated(@NonNull Preview.PreviewOutput output) {
                ViewParent parent=viewFinder.getParent();
                if(parent instanceof ViewGroup){
                    ViewGroup parentGroup=(ViewGroup)parent;
                    parentGroup.removeView(viewFinder);
                    parentGroup.addView(viewFinder,0);
                    viewFinder.setSurfaceTexture(output.getSurfaceTexture());
                    updateTransform();
                }
            }
        });

//        viewFinder=cameraRender.getSurfaceTexture();
        CameraX.bindToLifecycle(this,preview);

    }

    private void setupFriendListFilterPosition(float x,float y){
        AndroidViewFilterRender setupFilter=(AndroidViewFilterRender)filterReusedManager.fetchFilter(FilterName.LIST_FRIEND).getReusedFilter();
        setupFilter.setPosition(x,y);
    }

    private void initFriendList(){
        int maxFriends=300;
        for(int i=0;i<maxFriends;i++) {
            friendAdapterItems.add(new FriendAdapter.Item("abcd"+i));
        }
        friendAdapter=new FriendAdapter(this);
        friendAdapter.reloadFriends(friendAdapterItems);
        list_friend.setAdapter(friendAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        filterReusedManager.destory();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onlyMoveButton(View v, MotionEvent event){
        if (event.getPointerCount() == 1) {
            float eventx=event.getX();
            float eventy=event.getY();
            Log.d("iori_move", "onlyMoveButton: event x "+eventx+" y "+eventy+" view width "+v.getWidth()+" height "+v.getHeight());
            float xPercent = eventx * 100 / v.getWidth();
            float yPercent = eventy * 100 / v.getHeight();
            v.setTranslationX(xPercent);
            v.setTranslationY(yPercent);
        }
    }

    private void addFlyButton(View v){
        fly_button.setVisibility(View.VISIBLE);
        fly_button.setText(fly_button.getText());
        if(filterReusedManager.fetchFilter(FilterName.FLY_BUTTON) == null){
            addAndroidView(fly_button,FilterName.FLY_BUTTON);
            final AndroidViewFilterReuse androidViewFilterReuse=(AndroidViewFilterReuse)filterReusedManager.fetchFilter(FilterName.FLY_BUTTON);
            float fixY=-2f;
            filterReusedManager.setPosition(FilterName.FLY_BUTTON,fly_button.getX(),fly_button.getY()+fixY);
            fly_button.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                        onlyMoveButton(v, event);
                    }
                    return false;
                }
            });

            moveButton();
        }
    }

    private void moveButton(){
        final AndroidViewFilterReuse androidViewFilterReuse=(AndroidViewFilterReuse)filterReusedManager.fetchFilter(FilterName.FLY_BUTTON);
        filterReusedManager.setPosition(FilterName.FLY_BUTTON,fly_button.getX(),fly_button.getY());
        fly_button.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int moveY=100;
                        float moveStep=5f;
                        Log.d("iori_move", "before move fly button y"+fly_button.getY());
                        fly_button.setY(fly_button.getY()+moveStep);
//                        fly_button.setTranslationY(moveStep);
                        Log.d("iori_move", "after move fly button y"+fly_button.getY());
                        filterReusedManager.setPosition(FilterName.FLY_BUTTON,fly_button.getX(),fly_button.getY());
                        Log.d("iori_move", "run: fly button x"+fly_button.getX()+" y "+fly_button.getY()+" open gl button x "+androidViewFilterReuse.getFilterRecord().getPosition().x+" y "+androidViewFilterReuse.getFilterRecord().getPosition().y);
                        moveCount--;
                        if(moveCount > 0) {
                            moveButton();
                        }
                    }
                });
                }
        },1000);
    }

    private void addAndroidView(View view,String key){
        if(filterReusedManager.fetchFilter(key) == null ){
            AndroidViewFilterReuse addFilter=new AndroidViewFilterReuse(new ReuseAndroidViewFilterRecord());
            addFilter.getFilterRecord().setDefaultOutputSize(new Point(preViewWidth,preViewHeight));
            addFilter.getFilterRecord().setAttatchView(view);
            filterReusedManager.addFilter(addFilter,key);
        }
    }

    private void addAndroidView(View view){
        if(filterReusedManager.fetchFilter(FilterName.ADD_ANDROID_VIEW) == null ){
            AndroidViewFilterReuse addFilter=new AndroidViewFilterReuse(new ReuseAndroidViewFilterRecord());
            addFilter.getFilterRecord().setDefaultOutputSize(new Point(preViewWidth,preViewHeight));
            addFilter.getFilterRecord().setAttatchView(view);
            add_adnroid_button.setText(add_adnroid_button.getText());
            filterReusedManager.addFilter(addFilter,FilterName.ADD_ANDROID_VIEW);

            filterReusedManager.setPosition(FilterName.ADD_ANDROID_VIEW,200,200);
            filterReusedManager.setPosition(FilterName.ADD_ANDROID_VIEW,400,400);
            filterReusedManager.setPosition(FilterName.ADD_ANDROID_VIEW,TranslateTo.CENTER);
//
            filterReusedManager.setScale(FilterName.ADD_ANDROID_VIEW,1f,1f);
//            filterReusedManager.setScale(FilterName.ADD_ANDROID_VIEW,2f,2f);
//            filterReusedManager.setScale(FilterName.ADD_ANDROID_VIEW,3f,3f);
        }
    }

    private void addSnow(View v){
        if(filterReusedManager.fetchFilter(FilterName.ADD_SNOW) == null){
            ReuseNonUpdateFilterRecord addRecord=new ReuseNonUpdateFilterRecord();
            NonFilterReuse<SnowFilterRender> addFilterReuse=new NonFilterReuse<SnowFilterRender>(addRecord,SnowFilterRender.class);
            filterReusedManager.addFilter(addFilterReuse,FilterName.ADD_SNOW);
        }
    }

    private void autoSetupStreamSize(int cameraID){
        List<Camera.Size> mPictureSizes = new ArrayList<>(10);
        Camera mCamera = Camera.open(cameraID);
        Camera.Parameters mCameraParameters = mCamera.getParameters();
        for (Camera.Size mSize : mCameraParameters.getSupportedPictureSizes()){
            mPictureSizes.add(mCamera.new Size(mSize.width, mSize.height));
//            Log.d("iori_camera", "autosetupPicture size: width "+mSize.width+" height "+mSize.height);
        }
    }

    private void autosetupPreViewSize(int cameraID){
        List<Camera.Size> mPreviewSizes = new ArrayList<>(10);
        Camera mCamera = Camera.open(cameraID);
        Camera.Parameters mCameraParameters = mCamera.getParameters();
        for (Camera.Size mSize : mCameraParameters.getSupportedPreviewSizes()){
            mPreviewSizes.add(mCamera.new Size(mSize.width, mSize.height));
//            Log.d("iori_camera", "autosetupPreViewSize: width "+mSize.width+" height "+mSize.height);
        }
    }

    private void setImageToStream() {
        final int outWidth=100;
        final int outHeight=100;
        if(filterReusedManager.fetchFilter(FilterName.ADD_IMAGE_BUTTON1) == null){
            NonReleaseObjectFilterRecord addRecord=new NonReleaseObjectFilterRecord();
            addRecord.setDefaultOutputSize(new Point(streamWidth,streamHeight));
            final ImageFilterReuse addFilterReuse=new ImageFilterReuse(addRecord, new ImageFilterReuse.LoadBitmapBehavior() {
                @Override
                public Bitmap loadBitmap() {
                    BitmapFactory.Options options=new BitmapFactory.Options();
                    options.outWidth=outWidth;
                    options.outHeight=outHeight;

                    Bitmap createBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.start_stream);
                    return createBitmap;
                }
            });
            addFilterReuse.init(this);
            filterReusedManager.addFilter(addFilterReuse,FilterName.ADD_IMAGE_BUTTON1);
            image1_visible_setup.setVisibility(View.VISIBLE);
            image1_visible_setup.setText(image1_visible_setup.getText()+" false ");

            filterReusedManager.setPosition(FilterName.ADD_IMAGE_BUTTON1,300,300);
            filterReusedManager.setPosition(FilterName.ADD_IMAGE_BUTTON1,400,400);
            filterReusedManager.setPosition(FilterName.ADD_IMAGE_BUTTON1,TranslateTo.CENTER);

            filterReusedManager.setScale(FilterName.ADD_IMAGE_BUTTON1,1f,1f);
        }
    }

    private void setImageToStream2() {
        if(filterReusedManager.fetchFilter(FilterName.ADD_IMAGE_BUTTON2) == null){
            NonReleaseObjectFilterRecord addRecord=new NonReleaseObjectFilterRecord();
            addRecord.setDefaultOutputSize(new Point(streamWidth,streamHeight));
            ImageFilterReuse addFilterReuse=new ImageFilterReuse(addRecord, new ImageFilterReuse.LoadBitmapBehavior() {
                @Override
                public Bitmap loadBitmap() {
                    Bitmap createBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.start_stream);
                    return createBitmap;
                }
            });
            addFilterReuse.init(this);
            filterReusedManager.addFilter(addFilterReuse,FilterName.ADD_IMAGE_BUTTON2);
        }
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
    public void onConnectionSuccessRtmp() {
        Log.d("iori_rtmp", "onConnectionSuccessRtmp: ");
    }

    @Override
    public void onConnectionFailedRtmp(@NonNull String reason) {
        Log.d("iori_rtmp", "onConnectionFailedRtmp: ");
        rtmpCamera1.stopStream();
    }

    @Override
    public void onNewBitrateRtmp(long bitrate) {
//        Log.d("iori_rtmp", "onNewBitrateRtmp: ");
    }

    @Override
    public void onDisconnectRtmp() {
        Log.d("iori_rtmp", "onDisconnectRtmp: ");
    }

    @Override
    public void onAuthErrorRtmp() {
        Log.d("iori_rtmp", "onAuthErrorRtmp: ");
    }

    @Override
    public void onAuthSuccessRtmp() {
        Log.d("iori_rtmp", "onAuthSuccessRtmp: ");
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("iori_surfaceChanged", "surfaceCreated:");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        fixedRotation=fetchCameraDisplayOrientation(this, usedCameraID,null);
        Log.d("iori_surfaceChanged", "surfaceChanged: start preview width "+width+" height "+height);
        rtmpCamera1.startPreview(usedCameraFacing,preViewHeight,preViewWidth,fixedRotation);
        fetchCameraDisplayOrientation(this, usedCameraID,null);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("iori_surfaceChanged", "surfaceDestroyed:");

        if (rtmpCamera1.isStreaming()) {
            rtmpCamera1.stopStream();
        }
        rtmpCamera1.stopPreview();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return filterReusedManager.dispatchOnTouch(v,event);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        1 back
//        0 front
//        setCameraDisplayOrientation(this, Camera.CameraInfo.CAMERA_FACING_BACK,null);
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


    private class FilterName{
        private static final String ADD_ANDROID_VIEW ="addAndroidView";
        private static final String ADD_IMAGE_BUTTON1 ="addImageButton1";
        private static final String ADD_IMAGE_BUTTON2 ="addImageButton2";
        private static final String ADD_SNOW ="addSnow";
        private static final String FLY_BUTTON ="fly_button";
        private static final String LIST_FRIEND="list_friend";
        private static final String CAMERA_SURFACE="CAMERA_SURFACE";
    }

}

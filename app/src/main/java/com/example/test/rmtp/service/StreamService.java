package com.example.test.rmtp.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.test.rmtp.R;
import com.pedro.rtplibrary.rtmp.RtmpCamera2;
import com.pedro.rtplibrary.view.OpenGlView;

import net.ossrs.rtmp.ConnectCheckerRtmp;

public class StreamService extends Service {
    private static RtmpCamera2 streamCamera_example;
//    default
    private RtmpCamera2 streamCamera_custom;
    static NotificationManager notificationManager;
    String notificationId = "channelId";
    String notificationName = "channelName";
    private String TAG="RtpService";
    private static String channelId = "rtpStreamChannel";
    private static int notifyId = 123456;
    private static OpenGlView openGlView;
    private static Context contextApp;
    final String fbRmtpServer="rtmps://live-api-s.facebook.com:443/rtmp/";
    final String streamID="121248802817336?s_bl=1&s_sc=121248822817334&s_sw=0&s_vt=api-s&a=AbyFsHYY9OVb2WqB";
    final String streamUrl=fbRmtpServer+streamID;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel(channelId,channelId,NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        keepAliveTrick_example();
        streamCamera_custom=new RtmpCamera2(this, true, new ConnectCheckerRtmp() {
            @Override
            public void onConnectionSuccessRtmp() {
                Log.d("iori_stream_service", "onConnectionSuccessRtmp: service camera");
            }

            @Override
            public void onConnectionFailedRtmp(@NonNull String reason) {
                Log.d("iori_stream_service", "onConnectionFailedRtmp: service camera");
//                startStream();
//                Log.d("iori_stream_service", "onConnectionFailedRtmp: service camera restart stream");
            }

            @Override
            public void onNewBitrateRtmp(long bitrate) {

            }

            @Override
            public void onDisconnectRtmp() {
                Log.d("iori_stream_service", "onDisconnectRtmp: service camera");
            }

            @Override
            public void onAuthErrorRtmp() {
                Log.d("iori_stream_service", "onAuthErrorRtmp: service camera");
            }

            @Override
            public void onAuthSuccessRtmp() {
                Log.d("iori_stream_service", "onAuthSuccessRtmp: service camera");
            }
        });
    }

    private void keepAliveTrick_example(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            Notification notification=new NotificationCompat.Builder(this,channelId)
                    .setOngoing(true)
                    .setContentTitle("")
                    .setContentText("").build();
            startForeground(1,notification);
        }else{
            startForeground(1,new Notification());
        }
    }

    private void startStream(){
        if (streamCamera_custom.prepareAudio() && streamCamera_custom.prepareVideo(854, 480, 24, 1200 * 1024, false, 90)) {
            streamCamera_custom.startStream(streamUrl);
            Log.d("iori_stream_service", "onStartCommand: ");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            prepareStreamRtp_example();
            startStreamRtp_example(streamUrl);
        return START_STICKY;
    }

    public static void setView_example(OpenGlView openGlView){
        StreamService.openGlView=openGlView;
        streamCamera_example.replaceView(openGlView);
    }

    public static void setView_example(Context context){
        contextApp=context;
        openGlView=null;
        streamCamera_example.replaceView(context);
    }

    public static void startPreview_example(){
        streamCamera_example.startPreview();
    }

    public static void init_example(Context context){
        contextApp=context;
        if(streamCamera_example == null){
            streamCamera_example =new RtmpCamera2(context,true, connectCheckerRtp_example);
        }
    }

    void stopStream_example(){
        if(streamCamera_example != null){
            if(streamCamera_example.isStreaming()){
                streamCamera_example.stopStream();
            }
        }
    }

    public static void stopPreview_example(){
        if(streamCamera_example != null){
            if(streamCamera_example.isOnPreview()){
                streamCamera_example.stopPreview();;
            }
        }
    }

    private static ConnectCheckerRtmp connectCheckerRtp_example =new ConnectCheckerRtmp() {
        @Override
        public void onConnectionSuccessRtmp() {
            showNotification_example("Stream started");
            Log.i("iori", "RTP service connection success");
        }

        @Override
        public void onConnectionFailedRtmp(@NonNull String reason) {
            showNotification_example("Stream connection failed");
            Log.i("iori", "RTP service connection fail");
        }

        @Override
        public void onNewBitrateRtmp(long bitrate) {

        }

        @Override
        public void onDisconnectRtmp() {
            showNotification_example("Stream stopped");
            Log.i("iori", "RTP service connection disconnect");
        }

        @Override
        public void onAuthErrorRtmp() {
            showNotification_example("Stream auth error");
            Log.i("iori", "RTP service connection autherror");
        }

        @Override
        public void onAuthSuccessRtmp() {
            showNotification_example("Stream auth success");
            Log.i("iori", "RTP service connection authsuccess");
        }
    };

    private static void showNotification_example(String text){
        if(contextApp != null){
            Notification notification=new NotificationCompat.Builder(contextApp,channelId)
                    .setSmallIcon(R.drawable.rtmp_icon)
                    .setContentTitle("RTP Stream")
                    .setContentText(text).build();
            if(notificationManager != null){
                notificationManager.notify(notifyId,notification);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("iori", "RTP service destroy");
        stopStream_example();
    }


    private void prepareStreamRtp_example(){
        stopStream_example();
        stopPreview_example();
        if(openGlView == null){
            streamCamera_example =new RtmpCamera2(getBaseContext(),true, connectCheckerRtp_example);
        }else{
            streamCamera_example =new RtmpCamera2(openGlView, connectCheckerRtp_example);
        }
    }

    private void startStreamRtp_example(String url){
        if(streamCamera_example != null){
            if(streamCamera_example.prepareAudio() && streamCamera_example.prepareVideo()){
                streamCamera_example.startStream(url);
            }
        }
    }

    private void startForegroundService() {

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //创建NotificationChannel

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(notificationId, notificationName, NotificationManager.IMPORTANCE_HIGH);

            notificationManager.createNotificationChannel(channel);

        }
        startForeground(1,getNotification());

    }

    private Notification getNotification() {
        Notification.Builder builder = new Notification.Builder(this)

                .setSmallIcon(R.drawable.rtmp_icon)

                .setContentTitle("投屏服务")

                .setContentText("投屏服务正在运行...");

        //设置Notification的ChannelID,否则不能正常显示

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setChannelId(notificationId);

        }

        Notification notification = builder.build();

        return notification;

    }
}

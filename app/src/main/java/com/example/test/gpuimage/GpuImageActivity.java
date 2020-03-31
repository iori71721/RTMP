package com.example.test.gpuimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test.rmtp.R;

import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSepiaToneFilter;

public class GpuImageActivity extends AppCompatActivity {
    private GPUImageView gpuImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpuimage);
        gpuImageView = findViewById(R.id.gpuimage);
        Bitmap loadBitmap= BitmapFactory.decodeResource(getResources(),R.drawable.rtmp_icon);
        gpuImageView.setImage(loadBitmap);
        gpuImageView.setFilter(new GPUImageSepiaToneFilter());
    }
}

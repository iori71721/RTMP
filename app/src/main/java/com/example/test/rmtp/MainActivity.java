package com.example.test.rmtp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.test.gpuimage.GpuImageActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button testRtmp;
    private Button testGpuimage;
    private Button test_two_level_rtmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testRtmp=findViewById(R.id.test_rtmp);
        testGpuimage=findViewById(R.id.test_gpuimage);
        test_two_level_rtmp=findViewById(R.id.test_two_level_rtmp);

        testRtmp.setOnClickListener(this);
        testGpuimage.setOnClickListener(this);
        test_two_level_rtmp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.test_rtmp:
                startActivity(new Intent(this, RtmpActivity.class));
                break;
            case R.id.test_gpuimage:
                startActivity(new Intent(this, GpuImageActivity.class));
                break;
            case R.id.test_two_level_rtmp:
                startActivity(new Intent(this,TwoLevelRtmpActivity.class));
                break;
        }
    }
}

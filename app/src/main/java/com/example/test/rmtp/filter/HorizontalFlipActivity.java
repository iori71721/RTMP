package com.example.test.rmtp.filter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.example.test.rmtp.BaseRtmpActivity;
import com.example.test.rmtp.R;
import com.example.test.rmtp.filterReuse.BrightnessFilterReuse;
import com.example.test.rmtp.filterReuse.NonFilterReuse;
import com.example.test.rmtp.filterReuse.record.ReuseBrightnessFilterRecord;
import com.example.test.rmtp.filterReuse.record.ReuseNonUpdateFilterRecord;

public class HorizontalFlipActivity extends BaseRtmpActivity {
    private Button button_HorizontalFlip_on;
    private Button button_HorizontalFlip_off;
    private Button button_Brightness_on;
    private Button button_Brightness_off;
    private Button button_Brightness_inc;
    private Button button_Brightness_dec;

    @Override
    protected int generateContentViewID() {
        return R.layout.horizontal_flip_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        button_HorizontalFlip_on=findViewById(R.id.button_HorizontalFlip_on);
        button_HorizontalFlip_off=findViewById(R.id.button_HorizontalFlip_off);
        button_Brightness_on=findViewById(R.id.button_Brightness_on);
        button_Brightness_off=findViewById(R.id.button_Brightness_off);
        button_Brightness_inc=findViewById(R.id.button_Brightness_inc);
        button_Brightness_dec=findViewById(R.id.button_Brightness_dec);

        button_HorizontalFlip_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableHorizontalFlip(true);
            }
        });

        button_HorizontalFlip_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableHorizontalFlip(false);
            }
        });

        button_Brightness_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterReusedManager.visibleFilter(FilterName.BRIGHTNESS,true);
            }
        });

        button_Brightness_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterReusedManager.visibleFilter(FilterName.BRIGHTNESS,false);
            }
        });

        button_Brightness_inc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBrightness(0.1f);
            }
        });

        button_Brightness_dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBrightness(-0.1f);
            }
        });

        addHorizontalFlipFilter();
        addBrightnessFilter();
    }

    private void changeBrightness(float brightness){
        if(filterReusedManager.fetchFilter(FilterName.BRIGHTNESS)!=null && filterReusedManager.fetchFilter(FilterName.BRIGHTNESS).isVisible()){
            BrightnessFilterReuse changeFilter=(BrightnessFilterReuse) filterReusedManager.fetchFilter(FilterName.BRIGHTNESS);
            float changeBrightness=changeFilter.getBrightness()+brightness;
            if(changeBrightness<0){
                changeBrightness=0;
            }
            changeFilter.setBrightness(changeBrightness);
            Log.d("iori", "changeBrightness: "+changeBrightness);
        }
    }

    private void addHorizontalFlipFilter(){
        if(filterReusedManager.fetchFilter(FilterName.HORIZONTAL_FLIP)==null){
            NonFilterReuse<HorizontalFlipFilter> addFilter=new NonFilterReuse<>(new ReuseNonUpdateFilterRecord(),HorizontalFlipFilter.class);
            filterReusedManager.addFilter(addFilter,FilterName.HORIZONTAL_FLIP);
            enableHorizontalFlip(false);
        }
    }

    private void addBrightnessFilter(){
        if(filterReusedManager.fetchFilter(FilterName.BRIGHTNESS)==null){
            BrightnessFilterReuse addFilter=new BrightnessFilterReuse(new ReuseBrightnessFilterRecord());
            filterReusedManager.addFilter(addFilter,FilterName.BRIGHTNESS);
        }
    }

    private void enableHorizontalFlip(boolean enable){
        filterReusedManager.visibleFilter(FilterName.HORIZONTAL_FLIP,enable);
    }

    private class FilterName{
        private static final String HORIZONTAL_FLIP ="HORIZONTAL_FLIP";
        private static final String BRIGHTNESS="BRIGHTNESS";
    }
}

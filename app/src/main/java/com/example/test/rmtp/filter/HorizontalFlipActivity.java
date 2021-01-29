package com.example.test.rmtp.filter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.example.test.rmtp.BaseRtmpActivity;
import com.example.test.rmtp.R;
import com.example.test.rmtp.filterReuse.BeautifulFaceFilterReuse;
import com.example.test.rmtp.filterReuse.BrightnessFilterReuse;
import com.example.test.rmtp.filterReuse.CustomGPUImageChromaKeyBlendFilterReuse;
import com.example.test.rmtp.filterReuse.NonFilterReuse;
import com.example.test.rmtp.filterReuse.record.ReuseBeautifulFaceFilterRecord;
import com.example.test.rmtp.filterReuse.record.ReuseBrightnessFilterRecord;
import com.example.test.rmtp.filterReuse.record.ReuseCustomGPUImageChromaKeyBlendFilterRecord;
import com.example.test.rmtp.filterReuse.record.ReuseNonUpdateFilterRecord;

public class HorizontalFlipActivity extends BaseRtmpActivity {
    private Button button_HorizontalFlip_on;
    private Button button_HorizontalFlip_off;


    private Button button_Brightness_on;
    private Button button_Brightness_off;
    private Button button_Brightness_inc;
    private Button button_Brightness_dec;


    private Button button_beautiful_on;
    private Button button_beautiful_off;
    private Button button_beautiful_inc;
    private Button button_beautiful_dec;


    private Button button_green_on;
    private Button button_green_off;
    private Button button_green_change;
    private int greenChangeCount;

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
        button_beautiful_on=findViewById(R.id.button_beautiful_on);
        button_beautiful_off=findViewById(R.id.button_beautiful_off);
        button_beautiful_inc=findViewById(R.id.button_beautiful_inc);
        button_beautiful_dec=findViewById(R.id.button_beautiful_dec);
        button_green_on=findViewById(R.id.button_green_on);
        button_green_off=findViewById(R.id.button_green_off);
        button_green_change=findViewById(R.id.button_green_change);

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

        button_beautiful_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterReusedManager.visibleFilter(FilterName.BEAUTIFUL,true);
            }
        });

        button_beautiful_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterReusedManager.visibleFilter(FilterName.BEAUTIFUL,false);
            }
        });

        button_beautiful_inc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changBeautiful(0.4f);
            }
        });

        button_beautiful_dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changBeautiful(-0.4f);
            }
        });

        initGreenLayout();

        addFilters();
    }

    private void addFilters(){
        addHorizontalFlipFilter();
        addBrightnessFilter();
        addBeautifulFilter();
        addGreenScreenFilter();
    }

    private void initGreenLayout(){
        button_green_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterReusedManager.visibleFilter(FilterName.GREEN_SCREEN,true);
            }
        });

        button_green_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterReusedManager.visibleFilter(FilterName.GREEN_SCREEN,false);
            }
        });

        button_green_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeGreenBitmap(greenChangeCount);
            }
        });
    }

    private void changeGreenBitmap(int greenChangeCount) {
        if (filterReusedManager.fetchFilter(FilterName.GREEN_SCREEN) != null) {
            CustomGPUImageChromaKeyBlendFilterReuse filterReuse = (CustomGPUImageChromaKeyBlendFilterReuse) filterReusedManager.fetchFilter(FilterName.GREEN_SCREEN);
            greenChangeCount++;
            this.greenChangeCount = greenChangeCount;
            Bitmap changeBitmap;
            if (greenChangeCount % 2 == 0) {
                changeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.start_stream);
            } else {
                changeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.password);
            }
            filterReuse.setReplaceBitmap(changeBitmap);
        }
    }

    private void changBeautiful(float beautiful){
        if(filterReusedManager.fetchFilter(FilterName.BEAUTIFUL) != null && filterReusedManager.fetchFilter(FilterName.BEAUTIFUL).isVisible()){
            BeautifulFaceFilterReuse beautifulFaceFilterReuse=(BeautifulFaceFilterReuse) filterReusedManager.fetchFilter(FilterName.BEAUTIFUL);
            float changeBeautiful=beautifulFaceFilterReuse.getFilterRecord().getBeautiful()+beautiful;
            beautifulFaceFilterReuse.setBeautyLevelValue(changeBeautiful);
        }
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

    private void addGreenScreenFilter(){
        if(filterReusedManager.fetchFilter(FilterName.GREEN_SCREEN) == null){
            CustomGPUImageChromaKeyBlendFilterReuse customGPUImageChromaKeyBlendFilterReuse
                    =new CustomGPUImageChromaKeyBlendFilterReuse(new ReuseCustomGPUImageChromaKeyBlendFilterRecord());
            customGPUImageChromaKeyBlendFilterReuse.setReplaceBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.user));
            filterReusedManager.addFilter(customGPUImageChromaKeyBlendFilterReuse,FilterName.GREEN_SCREEN);
        }
    }

    private void addBeautifulFilter(){
        if(filterReusedManager.fetchFilter(FilterName.BEAUTIFUL)==null){
            BeautifulFaceFilterReuse addFilter=new BeautifulFaceFilterReuse(new ReuseBeautifulFaceFilterRecord());
            filterReusedManager.addFilter(addFilter,FilterName.BEAUTIFUL);
        }
    }

    private void enableHorizontalFlip(boolean enable){
        filterReusedManager.visibleFilter(FilterName.HORIZONTAL_FLIP,enable);
    }

    private class FilterName{
        private static final String HORIZONTAL_FLIP ="HORIZONTAL_FLIP";
        private static final String BRIGHTNESS="BRIGHTNESS";
        private static final String BEAUTIFUL="BEAUTIFUL";
        private static final String GREEN_SCREEN ="GREEN_SCREEN";
    }
}

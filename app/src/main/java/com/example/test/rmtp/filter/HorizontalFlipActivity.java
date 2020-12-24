package com.example.test.rmtp.filter;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.example.test.rmtp.BaseRtmpActivity;
import com.example.test.rmtp.R;
import com.example.test.rmtp.filterReuse.NonFilterReuse;
import com.example.test.rmtp.filterReuse.record.ReuseNonUpdateFilterRecord;

public class HorizontalFlipActivity extends BaseRtmpActivity {
    private Button button_HorizontalFlip_on;
    private Button button_HorizontalFlip_off;

    @Override
    protected int generateContentViewID() {
        return R.layout.horizontal_flip_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        button_HorizontalFlip_on=findViewById(R.id.button_HorizontalFlip_on);
        button_HorizontalFlip_off=findViewById(R.id.button_HorizontalFlip_off);

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

        addHorizontalFlipFilter();
    }

    private void addHorizontalFlipFilter(){
        if(filterReusedManager.fetchFilter(FilterName.HORIZONTAL_FLIP)==null){
            NonFilterReuse<HorizontalFlipFilter> addFilter=new NonFilterReuse<>(new ReuseNonUpdateFilterRecord(),HorizontalFlipFilter.class);
            filterReusedManager.addFilter(addFilter,FilterName.HORIZONTAL_FLIP);
        }
        enableHorizontalFlip(false);
    }

    private void enableHorizontalFlip(boolean enable){
        filterReusedManager.visibleFilter(FilterName.HORIZONTAL_FLIP,enable);
    }

    private class FilterName{
        private static final String HORIZONTAL_FLIP ="HorizontalFlip";
    }
}

package com.example.test.rmtp.filterReuse;

import com.example.test.rmtp.filter.gpuimage.BeautifulFaceFilter;
import com.example.test.rmtp.filterReuse.record.ReuseBeautifulFaceFilterRecord;

public class BeautifulFaceFilterReuse extends BaseObjectFilterReuse<BeautifulFaceFilter, ReuseBeautifulFaceFilterRecord> {

    public BeautifulFaceFilterReuse(ReuseBeautifulFaceFilterRecord record) {
        super(record);
    }

    public void setBeautyLevelValue(float beautyLevel) {
        beautyLevel=BeautifulFaceFilter.fixedBeautiful(beautyLevel);
        getReusedFilter().setBeautyLevelValue(beautyLevel);
        getFilterRecord().setBeautiful(beautyLevel);
    }

    @Override
    public BeautifulFaceFilter generateFilter(ReuseBeautifulFaceFilterRecord record) {
        BeautifulFaceFilter filter=new BeautifulFaceFilter();
        filter.setBeautyLevelValue(record.getBeautiful());
        setReusedFilter(filter);
        return filter;
    }
}

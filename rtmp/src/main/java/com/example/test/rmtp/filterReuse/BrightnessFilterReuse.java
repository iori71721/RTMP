package com.example.test.rmtp.filterReuse;

import com.example.test.rmtp.filterReuse.record.ReuseBrightnessFilterRecord;
import com.pedro.encoder.input.gl.render.filters.BrightnessFilterRender;

public class BrightnessFilterReuse extends BaseObjectFilterReuse<BrightnessFilterRender, ReuseBrightnessFilterRecord>{

    public BrightnessFilterReuse(ReuseBrightnessFilterRecord record) {
        super(record);
        getFilterRecord().setGestureMove(false);
        getFilterRecord().setGestureScale(false);
    }

    @Override
    public BrightnessFilterRender generateFilter(ReuseBrightnessFilterRecord record) {
        BrightnessFilterRender generateFilter=new BrightnessFilterRender();
        generateFilter.setBrightness(record.getBrightness());
        setReusedFilter(generateFilter);
        return generateFilter;
    }


    public float getBrightness() {
        return getFilterRecord().getBrightness();
    }

    /**
     * @param brightness Range should be between 0.1 - 2.0 with 0.0 being normal.
     */
    public void setBrightness(float brightness) {
        getReusedFilter().setBrightness(brightness);
        getFilterRecord().setBrightness(brightness);
    }

}

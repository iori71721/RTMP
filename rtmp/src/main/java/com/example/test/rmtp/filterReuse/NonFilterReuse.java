package com.example.test.rmtp.filterReuse;

import android.util.Log;

import com.example.test.rmtp.filterReuse.record.ReuseNonUpdateFilterRecord;
import com.pedro.encoder.input.gl.render.filters.BaseFilterRender;

/**
 *
 * @param <T> render type
 */
public class NonFilterReuse<T extends BaseFilterRender> extends BaseObjectFilterReuse<BaseFilterRender, ReuseNonUpdateFilterRecord> {
    private Class<T> type;

    public NonFilterReuse(ReuseNonUpdateFilterRecord record, Class<T> type) {
        super(record);
        this.type = type;
    }

    @Override
    public BaseFilterRender generateFilter(ReuseNonUpdateFilterRecord record) {
        BaseFilterRender generateFilter=null;
        try {
            generateFilter=type.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return generateFilter;
    }
}

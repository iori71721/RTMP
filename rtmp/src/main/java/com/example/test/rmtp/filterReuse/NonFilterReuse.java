package com.example.test.rmtp.filterReuse;

import android.util.Log;

import com.example.test.rmtp.filterReuse.record.ReuseNonUpdateFilterRecord;
import com.pedro.encoder.input.gl.render.filters.BaseFilterRender;

/**
 *
 * @param <T> render type
 */
public class NonFilterReuse<T extends BaseFilterRender,I extends ReuseNonUpdateFilterRecord> extends BaseObjectFilterReuse<T,I> {
    private Class<T> type;

    public NonFilterReuse(I record, Class<T> type) {
        super(record);
        this.type = type;
    }

    @Override
    public T generateFilter(I record) {
        T generateFilter=null;
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

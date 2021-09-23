package com.example.test.rmtp.filterReuse;

import com.example.test.rmtp.filter.VerticalExtensionFilter;
import com.example.test.rmtp.filterReuse.record.ReuseNonUpdateFilterRecord;

public class VerticalExtensionFilterReuse extends NonFilterReuse<VerticalExtensionFilter,ReuseNonUpdateFilterRecord>{
    public VerticalExtensionFilterReuse() {
        super(new ReuseNonUpdateFilterRecord(), VerticalExtensionFilter.class);
    }
}

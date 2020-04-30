package com.example.test.rmtp.filterReuse.record;

public class ReuseNonUpdateFilterRecord extends ReuseBaseObjectFilterRecord {

    public ReuseNonUpdateFilterRecord() {
        this(false);
    }

    private ReuseNonUpdateFilterRecord(boolean canUpdate) {
        super(false);
    }

    @Override
    public void release() {

    }
}

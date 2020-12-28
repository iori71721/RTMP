package com.example.test.rmtp.filterReuse.record;

public class NonReleaseObjectFilterRecord extends ReuseBaseObjectFilterRecord {

    public NonReleaseObjectFilterRecord() {
        super(true);
    }

    public NonReleaseObjectFilterRecord(boolean canUpdate) {
        super(canUpdate);
    }

    @Override
    public void release() {
    }

}

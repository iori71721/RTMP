package com.example.test.rmtp.filterReuse.record;

public class ReuseImageObjectFilterRecord extends ReuseBaseObjectFilterRecord {
    private int resID;

    @Override
    public void release() {
    }

    public void clearLoadInfo(){
        resID=0;
    }

    public int getResID() {
        return resID;
    }

    public void setResID(int resID) {
        this.resID = resID;
    }
}

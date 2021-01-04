package com.example.test.rmtp.filterReuse.record;

public class ReuseBeautifulFaceFilterRecord extends NonReleaseObjectFilterRecord {
    private float beautiful=1f;

    public ReuseBeautifulFaceFilterRecord() {
        super(false);
        setGestureMove(false);
        setGestureScale(false);
    }

    public float getBeautiful() {
        return beautiful;
    }

    public void setBeautiful(float beautiful) {
        this.beautiful = beautiful;
    }
}

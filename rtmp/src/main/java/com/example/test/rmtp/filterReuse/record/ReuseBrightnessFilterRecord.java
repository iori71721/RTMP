package com.example.test.rmtp.filterReuse.record;

public class ReuseBrightnessFilterRecord extends NonReleaseObjectFilterRecord{
    private float brightness;

    public ReuseBrightnessFilterRecord() {
        super(false);
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }
}

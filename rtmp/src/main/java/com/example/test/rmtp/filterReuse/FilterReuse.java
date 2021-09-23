package com.example.test.rmtp.filterReuse;

import com.pedro.rtplibrary.rtmp.RtmpCamera2;

public interface FilterReuse {
    /**
     * must reload resource,like bitmap ... to reuse filter
     * @param attachCamera
     */
    void reuse(RtmpCamera2 attachCamera);
}
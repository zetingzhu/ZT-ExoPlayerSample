package com.example.zzt.sampleexomedia3.imp;

/**
 * @author: zeting
 * @date: 2023/12/6
 * 播放状态监听
 */
public interface PlayStateChangedListener {
    /**
     * 缓冲
     */
    void onStateBuffering();

    /**
     * 开始播放
     */
    void onStateReady();

    /**
     * 播放结束
     */
    void onStateEnded();

    /**
     * 失败
     */
    void onStateError();

    /**
     * 是否暂停播放
     *
     * @param isPlaying
     */
    void onIsPlayingChanged(boolean isPlaying);
}

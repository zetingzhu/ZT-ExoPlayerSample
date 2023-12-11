package com.example.zzt.sampleexomedia3.util;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.Timeline;
import androidx.media3.common.VideoSize;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.exoplayer.DefaultLivePlaybackSpeedControl;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.drm.FrameworkMediaDrm;
import androidx.media3.exoplayer.util.EventLogger;
import androidx.media3.ui.AspectRatioFrameLayout;
import androidx.media3.ui.PlayerView;

import com.example.zzt.sampleexomedia3.imp.PlayStateChangedListener;

/**
 * @author: zeting
 * @date: 2023/12/4
 * 视频播放器工具
 */
public class ExoPlayerUtil implements DefaultLifecycleObserver {
    private static final String TAG = "ExoPlayer-Util";

    private Context mContext;
    /**
     * 播放视图
     */
    protected PlayerView playerView;
    /**
     * 播放器
     */
    protected @Nullable ExoPlayer player;

    /**
     * 临时媒体信息
     */
    private MediaItem currentMediaItem;

    /**
     * 是否自动播放
     */
    private boolean startAutoPlay = true;

    /**
     * 媒体列表索引
     */
    private int startItemIndex;
    /**
     * 播放时间
     */
    private long startPosition;

    /**
     * 全屏按钮
     */
    private ImageView fullScreenButton;

    /**
     * 加载对话框
     */
    private View loadingView;

    /**
     * 播放状态监听
     */
    private PlayStateChangedListener playStateChangedListener;

    @OptIn(markerClass = UnstableApi.class)
    public ExoPlayerUtil(Context mContext, PlayerView playerView) {
        this.mContext = mContext;
        this.playerView = playerView;

        /**
         * 设置绑定之后就设置视图默认横宽比，不让视图显示最高视图
         */
        AspectRatioFrameLayout aspectRatioFrameLayout = playerView.findViewById(androidx.media3.ui.R.id.exo_content_frame);
        if (aspectRatioFrameLayout != null) {
            // 设置一个默认，不然视图一开始就全屏显示
            aspectRatioFrameLayout.setAspectRatio(1.778F);
        }
    }

    public MediaItem getCurrentMediaItem() {
        return currentMediaItem;
    }

    /**
     * 设置播放源
     *
     * @param currentMediaItem
     */
    public void setCurrentMediaItem(MediaItem currentMediaItem) {
        this.currentMediaItem = checkCreateMediaItems(currentMediaItem);
    }

    /**
     * 设置播放地址
     *
     * @param url
     */
    public void setCurrentMediaItemToPlayer(String url) {
        if (!TextUtils.isEmpty(url)) {
            MediaItem mediaItem = MediaItem.fromUri(url);
            setCurrentMediaItemToPlayer(mediaItem);
        }
    }

    /**
     * 设置播放地址
     *
     * @param url
     */
    public void setCurrentMediaItemToPlayer(Uri url) {
        if (url != null) {
            MediaItem mediaItem = MediaItem.fromUri(url);
            setCurrentMediaItemToPlayer(mediaItem);
        }
    }

    /**
     * 设置视频，并且播放
     *
     * @param currentMediaItem
     */
    public void setCurrentMediaItemToPlayer(MediaItem currentMediaItem) {
        this.currentMediaItem = checkCreateMediaItems(currentMediaItem);
        clearStartPosition();
        initializePlayer();
        if (playerView != null) {
            playerView.onResume();
        }
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        onCreate();
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        onStart();
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        onResume();
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        onPause();
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        onStop();
    }

    public void onCreate() {
        clearStartPosition();
    }

    public void onNewIntent() {
        releasePlayer();
        clearStartPosition();
    }

    public void onStart() {
        if (Build.VERSION.SDK_INT > 23) {
            initializePlayer();
            if (playerView != null) {
                playerView.onResume();
            }
        }
    }

    public void onResume() {
        if (Build.VERSION.SDK_INT <= 23 || player == null) {
            initializePlayer();
            if (playerView != null) {
                playerView.onResume();
            }
        }
    }

    public void onPause() {
        if (Build.VERSION.SDK_INT <= 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }
    }

    public void onStop() {
        if (Build.VERSION.SDK_INT > 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }
    }

    @Nullable
    public ExoPlayer getPlayer() {
        return player;
    }

    /**
     * 获取播放状态
     *
     * @return
     */
    public boolean isPlaying() {
        if (player != null) {
            return player.isPlaying();
        }
        return false;
    }

    /**
     * 关闭释放资源
     */
    public void releaseResource() {
        if (playerView != null) {
            playerView.onPause();
        }
        releasePlayer();
    }

    /**
     * 重播
     */
    public void replay() {
        clearStartPosition();
        initializePlayer();
        if (playerView != null) {
            playerView.onResume();
        }
    }

    /**
     * 播放还是暂停
     */
    @OptIn(markerClass = UnstableApi.class)
    public void setPlayAndPause(boolean playIfSuppressed) {
        if (player != null) {
            Util.handlePlayPauseButtonAction(player, playIfSuppressed);
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (playerView != null) {
            return playerView.dispatchKeyEvent(event);
        }
        return false;
    }

    /**
     * 检测当前所有媒体项目是否可用
     */
    public MediaItem checkCreateMediaItems(MediaItem mediaItem) {
        if (!Util.checkCleartextTrafficPermitted(mediaItem)) {
            Log.e(TAG, "Cleartext HTTP traffic not permitted. See https://developer.android.com/guide/topics/media/issues/cleartext-not-permitted");
            return null;
        }
        if (mediaItem.localConfiguration != null) {
            MediaItem.DrmConfiguration drmConfiguration = mediaItem.localConfiguration.drmConfiguration;
            if (drmConfiguration != null) {
                if (Build.VERSION.SDK_INT < 18) {
                    Log.e(TAG, "DRM content not supported on API levels below 18");
                    return null;
                } else if (!FrameworkMediaDrm.isCryptoSchemeSupported(drmConfiguration.scheme)) {
                    Log.e(TAG, "This device does not support the required DRM scheme");
                    return null;
                }
            }
        }

        return mediaItem;
    }

    /**
     * 初始化播放器
     */
    @OptIn(markerClass = UnstableApi.class)
    public void initializePlayer() {
        if (currentMediaItem == null) {
            return;
        }
        if (playerView == null) {
            return;
        }
        if (mContext == null) {
            return;
        }
        // 全屏按钮
        fullScreenButton = playerView.findViewById(androidx.media3.ui.R.id.exo_fullscreen);
        if (player == null) {
            ExoPlayer.Builder playerBuilder = new ExoPlayer.Builder(mContext);
            // 直播自定义播放速度调整算法,
            playerBuilder.setLivePlaybackSpeedControl(new DefaultLivePlaybackSpeedControl.Builder()
                    .setFallbackMaxPlaybackSpeed(1.04f)// 最大播放速度
                    .build());
            player = playerBuilder.build();
            // 添加播放监听
            player.addListener(new PlayerEventListener());
            // 添加日志监听
            player.addAnalyticsListener(new EventLogger());
            // 设置音频
            player.setAudioAttributes(AudioAttributes.DEFAULT, true);
            // 准备就绪自动播放
            player.setPlayWhenReady(startAutoPlay);
            // 设置重复模式，单曲循环
//            player.setRepeatMode(Player.REPEAT_MODE_ONE);
            // 绑定视图和播放器
            playerView.setPlayer(player);

            // 设置宽高比视图监听
            playerView.setAspectRatioListener(new AspectRatioFrameLayout.AspectRatioListener() {
                @Override
                public void onAspectRatioUpdated(float targetAspectRatio, float naturalAspectRatio, boolean aspectRatioMismatch) {
                    Log.d(TAG, "目标横纵比：" + targetAspectRatio + " 当前视图横纵比" + naturalAspectRatio + "横纵比是否设置：" + aspectRatioMismatch);
                }
            });
        }
        if (player != null) {
            boolean haveStartPosition = startItemIndex != C.INDEX_UNSET;
            if (haveStartPosition) {
                // 设置播放进度
                player.seekTo(startItemIndex, startPosition);
            }
            // 添加播放源
            player.setMediaItem(currentMediaItem, !haveStartPosition);
            // 播放器准备
            player.prepare();
        }
    }

    /**
     * 释放播放器
     */
    private void releasePlayer() {
        if (player != null) {
            updateStartPosition();
            player.release();
            player = null;
            playerView.setPlayer(null);
        }
    }

    /**
     * 更新当前视频起始位置
     */
    private void updateStartPosition() {
        if (player != null) {
            startAutoPlay = player.getPlayWhenReady();
            startItemIndex = player.getCurrentMediaItemIndex();
            startPosition = Math.max(0, player.getContentPosition());
        }
    }

    /**
     * 初始化其实位置
     */
    private void clearStartPosition() {
        startItemIndex = C.INDEX_UNSET;
        startPosition = C.TIME_UNSET;
    }

    /**
     * 各种监听
     */
    private class PlayerEventListener implements Player.Listener {

        @OptIn(markerClass = UnstableApi.class)
        @Override
        public void onVideoSizeChanged(VideoSize videoSize) {
            Player.Listener.super.onVideoSizeChanged(videoSize);
            Log.d(TAG, ">Player.Listener videoSize :" + videoSize.toBundle());
        }

        @OptIn(markerClass = UnstableApi.class)
        @Override
        public void onEvents(Player player, Player.Events events) {
            Player.Listener.super.onEvents(player, events);
            Log.d(TAG, ">Player.Listener onEvents player:" + player + " events:" + events);
        }

        @OptIn(markerClass = UnstableApi.class)
        @Override
        public void onTimelineChanged(Timeline timeline, int reason) {
//            Player.Listener.super.onTimelineChanged(timeline, reason);
//            if (player != null) {
//                Log.d(TAG, ">Player.Listener onTimelineChanged timeline:" + timeline + " reason:" + reason
//                        + "\n "
//                        + "\n Live:" + player.isCurrentMediaItemLive()  // 指示当前正在播放的媒体项是否为直播流。即使直播已结束，此值仍然为 true。
//                        + "\n Dynamic:" + player.isCurrentMediaItemDynamic()   // 指示当前播放的媒体项是否仍在更新。对于尚未结束的直播，通常也是如此。请注意，在某些情况下，此标志也适用于非直播。
//                        + "\n offset:" + player.getCurrentLiveOffset()  //  返回当前实时与播放位置（如果有）之间的偏移量。
//                        + "\n Duration:" + player.getDuration()  //  会返回当前实时窗口的长度。
//                        + "\n Pos:" + player.getCurrentPosition()  //  返回相对于直播窗口起始位置的播放位置。
//                        + "\n MediaItem:" + player.getCurrentMediaItem().toBundleIncludeLocalConfiguration()   //  返回当前媒体内容，其中 MediaItem.liveConfiguration 包含应用为目标实时偏移量和实时偏移量调整参数提供的替换项。
//                        + "\n timeline:" + player.getCurrentTimeline().toBundle()  //  用于在 Timeline 中返回当前媒体结构。
//                        + "\n ItemIndex:" + player.getCurrentMediaItemIndex()  // 可以使用 Player.getCurrentWindowIndex 和 Timeline.getWindow 从 Timeline 检索当前 Timeline.Window。在 Window 中：
//                );
//
////                Timeline.Window window = timeline.getWindow(player.getCurrentMediaItemIndex(), );
////                Log.d(TAG, ">Player.Listener onTimelineChanged window "
////                        + "\n " + window.liveConfiguration()  //  包含目标实时偏移量和实时偏移量调整参数。这些值基于媒体中的信息以及在 MediaItem.liveConfiguration 中设置的任何应用提供的替换项。
////                        + "\n " + window.windowStartTimeMs()  //  是自 Unix Epoch 以来的时间，即实时窗口的开始时间。
////                        + "\n " + window.getCurrentUnixTimeMs()  //  是自当前实时的 Unix 纪元以来经过的时间。可以根据服务器和客户端之间的已知时钟差值来更正此值。
////                        + "\n " + window.getDefaultPositionMs()  //  是直播窗口中默认播放器开始播放的位置。
////                );
//            }
        }

        @Override
        public void onPlaybackStateChanged(int playbackState) {
            Log.d(TAG, ">Player.Listener onPlaybackStateChanged:" + playbackState);
            switch (playbackState) {
                case Player.STATE_IDLE:       // 加载播放
                case Player.STATE_BUFFERING:       // 加载播放
                    if (playStateChangedListener != null) {
                        playStateChangedListener.onStateBuffering();
                    }
                    if (loadingView != null) {
                        loadingView.setVisibility(View.VISIBLE);
                    }
                    break;
                case Player.STATE_READY:   // 加载准备播放
                    if (playStateChangedListener != null) {
                        playStateChangedListener.onStateReady();
                    }
                    if (loadingView != null) {
                        loadingView.setVisibility(View.GONE);
                    }
                    break;
                case Player.STATE_ENDED:  // 播放结束
                    if (playStateChangedListener != null) {
                        playStateChangedListener.onStateEnded();
                    }
                    break;

            }
        }

        @Override
        public void onPlayerError(PlaybackException error) {
            Log.d(TAG, ">Player.Listener onPlayerError:" + error.errorCode);
            if (playStateChangedListener != null) {
                playStateChangedListener.onStateError();
            }
            if (error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {
                // 直播状态异常，恢复直播位置重新准备
                if (player != null) {
                    player.seekToDefaultPosition();
                    player.prepare();
                }
            }
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            Log.d(TAG, ">Player.Listener onIsPlayingChanged:" + isPlaying);
            if (playStateChangedListener != null) {
                playStateChangedListener.onIsPlayingChanged(isPlaying);
            }
        }

        @Override
        public void onIsLoadingChanged(boolean isLoading) {
            Log.d(TAG, ">Player.Listener onIsLoadingChanged:" + isLoading);
        }
    }


    /**
     * 添加监听
     *
     * @param mPlayerListener
     */
    public void addListener(Player.Listener mPlayerListener) {
        if (player != null) {
            player.addListener(mPlayerListener);
        }
    }

    /**
     * 设置是否自动播放
     *
     * @param startAutoPlay
     */
    public void setStartAutoPlay(boolean startAutoPlay) {
        this.startAutoPlay = startAutoPlay;
    }

    public void setPlayStateChangedListener(PlayStateChangedListener playStateChangedListener) {
        this.playStateChangedListener = playStateChangedListener;
    }

    /**
     * 全部状态点击
     */
    public void setFullScreenButtonClicked() {
        if (fullScreenButton != null) {
            fullScreenButton.performClick();
        }
    }

    public void setLoadingView(View loadingView) {
        this.loadingView = loadingView;
    }
}

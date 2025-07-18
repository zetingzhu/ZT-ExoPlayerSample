package com.example.zzt.sampleexomedia3.oneplay

/**
 * @author: zeting
 * @date: 2025/7/16
 *
 */

import android.content.Intent
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.add
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

class PlayerService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer

    companion object {
        const val ACTION_PLAY_VIDEO = "com.example.zzt.sampleexomedia3.oneplay.ACTION_PLAY_VIDEO"
        const val EXTRA_VIDEO_URL = "com.example.zzt.sampleexomedia3.oneplay.EXTRA_VIDEO_URL"
        const val COMMAND_PLAY_NEW_VIDEO = "COMMAND_PLAY_NEW_VIDEO" // 自定义命令
        const val BUNDLE_VIDEO_URL = "BUNDLE_VIDEO_URL"
    }

    // 在 onCreate 中创建 Player 和 MediaSession
    @OptIn(UnstableApi::class) // For MediaSession.Callback
    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this)
            .build()
            .apply {
                playWhenReady = true // 始终保持 play 状态，防止 Surface 丢失时自动暂停
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_ENDED) {
                            // 视频播放结束时的处理
                        }
                    }
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        if (!isPlaying && playWhenReady && player.playbackState == Player.STATE_READY) {
                            player.playWhenReady = true
                        }
                    }
                })
            }

        mediaSession = MediaSession.Builder(this, player)
            .setId("oneplay_session") // 保证 sessionId 唯一
            .setCallback(MyMediaSessionCallback())
            .build()
    }

    // MediaSessionService 需要这个方法来让客户端连接
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    // 在 Service 销毁时释放资源
    override fun onDestroy() {
        mediaSession?.run {
            // 在释放 session 之前释放 player
            // MediaController 会在 session 释放时收到通知
            this.player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    // 可选：处理来自 Activity 的命令，例如通过 Intent 启动播放
    // 如果 Activity 直接通过 MediaController 控制播放，这个可能不是必须的
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_PLAY_VIDEO) {
            val videoUrl = intent.getStringExtra(EXTRA_VIDEO_URL)
            if (videoUrl != null) {
                playVideoUrl(videoUrl)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun playVideoUrl(videoUrl: String) {
        val mediaItem = MediaItem.fromUri(videoUrl)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true // 或者 player.play()
    }

    // 自定义 MediaSession.Callback 来处理自定义命令
    @OptIn(UnstableApi::class)
    private inner class MyMediaSessionCallback : MediaSession.Callback {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            // 允许连接并添加自定义命令
            val availableSessionCommands =
                MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                    .add(SessionCommand(COMMAND_PLAY_NEW_VIDEO, Bundle.EMPTY))
                    .build()
            return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                .setAvailableSessionCommands(availableSessionCommands)
                .build()
        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            if (customCommand.customAction == COMMAND_PLAY_NEW_VIDEO) {
                val videoUrl = args.getString(BUNDLE_VIDEO_URL)
                if (!videoUrl.isNullOrEmpty()) {
                    playVideoUrl(videoUrl)
                    return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                }
                return Futures.immediateFuture(SessionResult(SessionResult.RESULT_ERROR_BAD_VALUE))
            }
            return Futures.immediateFuture(SessionResult(SessionResult.RESULT_ERROR_NOT_SUPPORTED))
        }

        // 可选：如果希望通知栏有关闭按钮，可以自定义布局
        // override fun getCustomLayout(
        //     session: MediaSession,
        //     controller: MediaSession.ControllerInfo
        // ): ListenableFuture<ImmutableList<CommandButton>> {
        //     // ...
        // }
    }
}

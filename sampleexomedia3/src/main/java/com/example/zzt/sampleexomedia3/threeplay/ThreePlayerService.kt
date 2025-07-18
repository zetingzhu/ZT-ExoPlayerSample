package com.example.zzt.sampleexomedia3.threeplay

import android.content.Intent
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class ThreePlayerService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer

    companion object {
        const val ACTION_PLAY_VIDEO = "com.example.zzt.sampleexomedia3.threeplay.ACTION_PLAY_VIDEO"
        const val EXTRA_VIDEO_URL = "com.example.zzt.sampleexomedia3.threeplay.EXTRA_VIDEO_URL"
    }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build().apply {
            playWhenReady = true
            addListener(object : androidx.media3.common.Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    // 如果因为 Surface 丢失导致暂停，自动恢复播放
                    if (!isPlaying && playWhenReady && playbackState == androidx.media3.common.Player.STATE_READY) {
                        playWhenReady = true
                    }
                }
            })
        }
        mediaSession = MediaSession.Builder(this, player)
            .setId("threeplay_session") // 保证 sessionId 唯一
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
        }
        mediaSession = null
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_PLAY_VIDEO) {
            val videoUrl = intent.getStringExtra(EXTRA_VIDEO_URL)
            if (!videoUrl.isNullOrEmpty()) {
                playVideoUrl(videoUrl)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun playVideoUrl(videoUrl: String) {
        val mediaItem = MediaItem.fromUri(videoUrl)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true
    }
}

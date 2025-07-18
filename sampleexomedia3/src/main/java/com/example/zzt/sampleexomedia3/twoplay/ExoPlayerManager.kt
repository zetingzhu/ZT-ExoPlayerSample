package com.example.zzt.sampleexomedia3.twoplay

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

/**
 * 单例 ExoPlayer 管理器，保证全局只有一个 ExoPlayer 实例
 */
object ExoPlayerManager {
    private var exoPlayer: ExoPlayer? = null

    fun getPlayer(context: Context): ExoPlayer {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context.applicationContext).build()
        }
        return exoPlayer!!
    }

    fun playUrl(context: Context, url: String) {
        val player = getPlayer(context)
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true
    }

    fun playUrl(context: Context, mediaItem: MediaItem) {
        val player = getPlayer(context)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true
    }

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
    }
}

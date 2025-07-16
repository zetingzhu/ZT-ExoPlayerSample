package com.example.zzt.sampleexomedia3.oneplay

/**
 * @author: zeting
 * @date: 2025/7/16
 *
 */


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionToken
import androidx.media3.ui.PlayerView
import com.example.zzt.sampleexomedia3.R // 假设你的 R 文件路径
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

class OnePlayAct : AppCompatActivity() {
    val TAG = OnePlayAct::class.java.simpleName
    var playerView: PlayerView? = null
    private var mediaControllerFuture: ListenableFuture<MediaController>? = null
    protected val mediaController: MediaController?
        get() = if (mediaControllerFuture?.isDone == true && mediaControllerFuture?.isCancelled == false) {
            try {
                mediaControllerFuture?.get()
            } catch (e: Exception) {
                Log.e(TAG, "Error getting MediaController", e)
                null
            }
        } else {
            null
        }

    companion object {
        // 示例视频 URL，替换为你自己的
        const val VIDEO_URL_1 = "https://oss.xtrendspeed.com/video/classRoom/how_use_credit-cn.mp4"

        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, OnePlayAct::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_play) // 确保这个布局文件存在且包含 PlayerView
        playerView = findViewById(R.id.player_view_one) // 确保 ID 正确

        findViewById<Button>(R.id.btn_play_one).setOnClickListener {
            // 通过 MediaController 播放，如果已连接
            // 或者通过 Service 的 onStartCommand (如果尚未连接或为了确保 Service 启动)
            // playNewVideo(VIDEO_URL_1) // BasePlayActivity 会在连接后尝试播放
            // 如果希望在点击按钮时立即尝试播放（即便controller可能还未完全连上），可以这样做：
            val controller = this.mediaController
            if (controller != null && controller.isConnected) {
                playNewVideo(VIDEO_URL_1)
            } else {
                // 如果控制器尚未连接，可以先启动 Service 并传递 URL
                // Service 内部的播放逻辑会处理
                val serviceIntent = Intent(this, PlayerService::class.java).apply {
                    action = PlayerService.ACTION_PLAY_VIDEO
                    putExtra(PlayerService.EXTRA_VIDEO_URL, VIDEO_URL_1)
                }
                startService(serviceIntent)
            }
        }

        findViewById<Button>(R.id.btn_to_two).setOnClickListener {
            TwoPlayAct.start(it.context)
        }
    }


    override fun onStart() {
        super.onStart()
        initializeMediaController()
    }

    override fun onStop() {
        super.onStop()
        // 将 PlayerView 与 Player 解绑，允许其他 Activity 的 PlayerView 接管
        playerView?.player = null
        releaseMediaController()
    }


    private fun initializeMediaController() {
        val sessionToken = SessionToken(this, ComponentName(this, PlayerService::class.java))
        mediaControllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        mediaControllerFuture?.addListener(
            {
                // MediaController 准备就绪
                val controller = this.mediaController
                if (controller != null) {
                    playerView?.player = controller // 将 Service 中的 Player 设置给当前页面的 PlayerView

                    if (controller.mediaItemCount > 0 && controller.playbackState == Player.STATE_IDLE) {
                        // 如果有媒体项但处于空闲状态 (例如 prepare 之后没有 play)
                        controller.prepare()
                        controller.play()
                    }
                }
            }, ContextCompat.getMainExecutor(this) // 或者 MoreExecutors.directExecutor() 如果回调很快
        )
    }

    private fun releaseMediaController() {
        mediaControllerFuture?.let {
            MediaController.releaseFuture(it)
        }
        mediaControllerFuture = null
    }

    protected fun playNewVideo(videoUrl: String) {
        val controller = this.mediaController ?: return

        controller.setMediaItem(MediaItem.fromUri(videoUrl))
        controller.prepare()
        controller.play()

    }
}


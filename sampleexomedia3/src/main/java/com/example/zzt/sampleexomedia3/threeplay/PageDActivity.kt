package com.example.zzt.sampleexomedia3.threeplay

import android.content.ComponentName
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.media3.ui.PlayerView
import com.example.zzt.sampleexomedia3.R
import com.google.common.util.concurrent.ListenableFuture

class PageDActivity : AppCompatActivity() {
    private var playerView: PlayerView? = null
    private var mediaControllerFuture: ListenableFuture<MediaController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_d)
        playerView = findViewById(R.id.player_view_d)
        findViewById<Button>(R.id.btn_to_c).setOnClickListener {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val sessionToken = SessionToken(this, ComponentName(this, ThreePlayerService::class.java))
        mediaControllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        mediaControllerFuture?.addListener({
            val controller = mediaControllerFuture?.get()
            if (controller != null) {
                playerView?.player = controller
                controller.play() // 主动恢复播放，防止被 pause
                // 延迟 seekTo，确保 Surface attach 完成，强制刷新画面
                playerView?.postDelayed({
                    val pos = controller.currentPosition
                    controller.seekTo(pos)
                }, 200)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onStop() {
        super.onStop()
        playerView?.player = null
    }
}

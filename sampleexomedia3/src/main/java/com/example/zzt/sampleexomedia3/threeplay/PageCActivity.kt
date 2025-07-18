package com.example.zzt.sampleexomedia3.threeplay

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.media3.ui.PlayerView
import com.example.zzt.sampleexomedia3.R
import com.example.zzt.sampleexomedia3.twoplay.PlayAActivity
import com.google.common.util.concurrent.ListenableFuture

class PageCActivity : AppCompatActivity() {
    private var playerView: PlayerView? = null
    private var mediaControllerFuture: ListenableFuture<MediaController>? = null
    private val videoUrl = "https://oss.xtrendspeed.com/video/classRoom/how_use_credit-cn.mp4"
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, PageCActivity::class.java)
            context.startActivity(starter)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_c)
        playerView = findViewById(R.id.player_view_c)
        findViewById<Button>(R.id.btn_to_d).setOnClickListener {
            startActivity(Intent(this, PageDActivity::class.java))
        }
        // 启动Service并播放
        val serviceIntent = Intent(this, ThreePlayerService::class.java).apply {
            action = ThreePlayerService.ACTION_PLAY_VIDEO
            putExtra(ThreePlayerService.EXTRA_VIDEO_URL, videoUrl)
        }
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    override fun onStart() {
        super.onStart()
        val sessionToken = SessionToken(this, ComponentName(this, ThreePlayerService::class.java))
        mediaControllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        mediaControllerFuture?.addListener({
            val controller = mediaControllerFuture?.get()
            if (controller != null) {
                playerView?.player = controller
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onStop() {
        super.onStop()
        playerView?.player = null
    }
}

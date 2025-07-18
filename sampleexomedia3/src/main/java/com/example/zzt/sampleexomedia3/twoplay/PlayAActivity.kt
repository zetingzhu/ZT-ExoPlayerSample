package com.example.zzt.sampleexomedia3.twoplay

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.ui.PlayerView
import com.example.zzt.sampleexomedia3.R

class PlayAActivity : AppCompatActivity() {
    private var playerView: PlayerView? = null
    private val videoUrl = "https://oss.xtrendspeed.com/video/classRoom/how_use_credit-cn.mp4"

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, PlayAActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_a)
        playerView = findViewById(R.id.player_view_a)
        findViewById<Button>(R.id.btn_to_b).setOnClickListener {
            startActivity(Intent(this, PlayBActivity::class.java))
        }
        // 开始播放
        ExoPlayerManager.playUrl(this, videoUrl)

        findViewById<Button>(R.id.btn_to_d).setOnClickListener {
            ExoPlayerManager.playUrl(this, "https://live.kilvn.com/iptv.m3u")
        }
    }

    override fun onStart() {
        super.onStart()
        playerView?.player = ExoPlayerManager.getPlayer(this)
    }

    override fun onStop() {
        super.onStop()
        playerView?.player = null // 解绑但不释放
    }

    override fun onDestroy() {
        super.onDestroy()
        // 不在这里释放 ExoPlayer，保证切换页面不断流
    }
}

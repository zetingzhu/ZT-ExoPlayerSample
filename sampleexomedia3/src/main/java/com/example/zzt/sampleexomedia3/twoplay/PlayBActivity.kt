package com.example.zzt.sampleexomedia3.twoplay

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.ui.PlayerView
import com.example.zzt.sampleexomedia3.R

class PlayBActivity : AppCompatActivity() {
    private var playerView: PlayerView? = null

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, PlayBActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_b)
        playerView = findViewById(R.id.player_view_b)
        findViewById<Button>(R.id.btn_to_a).setOnClickListener {
            finish()
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

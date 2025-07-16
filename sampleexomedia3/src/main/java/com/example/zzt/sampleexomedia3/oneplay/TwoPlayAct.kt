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
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionToken
import androidx.media3.ui.PlayerView
import com.example.zzt.sampleexomedia3.R
import com.example.zzt.sampleexomedia3.oneplay.OnePlayAct.Companion.VIDEO_URL_1
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

class TwoPlayAct : AppCompatActivity() {
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
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, TwoPlayAct::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two_play)
        playerView = findViewById(R.id.player_view_two)
        findViewById<Button>(R.id.btn_to_one).setOnClickListener {
            finish()
        }
    }


    override fun onStart() {
        super.onStart()
        initializeMediaController()
    }

    override fun onStop() {
        super.onStop()
        playerView?.player = null
        releaseMediaController()
    }

    private fun initializeMediaController() {
        val sessionToken = SessionToken(this, ComponentName(this, PlayerService::class.java))
        mediaControllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        mediaControllerFuture?.addListener(
            {
                val controller = this.mediaController
                if (controller != null) {
                    playerView?.player = controller // 将 Service 中的 Player 设置给当前页面的 PlayerView
                    controller.prepare()
                    controller.play()
                }
            },
            ContextCompat.getMainExecutor(this) // 或者 MoreExecutors.directExecutor() 如果回调很快
        )
    }

    private fun releaseMediaController() {
        mediaControllerFuture?.let {
            MediaController.releaseFuture(it)
            Log.d(TAG, "${this.javaClass.simpleName}: MediaController released.")
        }
        mediaControllerFuture = null
    }

}

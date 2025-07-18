package com.example.zzt.sampleexomedia3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.zzt.sampleexomedia3.act.ActExo
import com.example.zzt.sampleexomedia3.act.ExoFragAct
import com.example.zzt.sampleexomedia3.oneplay.OnePlayAct
import com.example.zzt.sampleexomedia3.threeplay.PageCActivity
import com.example.zzt.sampleexomedia3.twoplay.PlayAActivity
import com.example.zzt.sampleexomedia3.ui.theme.ZTExoPlayerSampleTheme

class ZtExoAct : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZTExoPlayerSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
        Button(onClick = {
            ActExo.start(context)
        }) {
            Text(text = "常规视频播放 ACT ")
        }

        Button(onClick = {
            ExoFragAct.start(context)
        }) {
            Text(text = "常规视频播放 Fragment ")
        }

        Button(onClick = {
            OnePlayAct.start(context)
        }) {
            Text(text = "一个视频两个页面共同播放 实现一 (失败) ")
        }

        Button(onClick = {
            PlayAActivity.start(context)
        }) {
            Text(text = "一个视频两个页面共同播放 实现二 （成功）")
        }

        Button(onClick = {
            PageCActivity.start(context)
        }) {
            Text(text = "一个视频两个页面共同播放 实现三 （失败）")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ZTExoPlayerSampleTheme {
        Greeting("Android")
    }
}
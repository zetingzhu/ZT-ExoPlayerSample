package com.example.zzt.sampleexomedia3.act;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static androidx.media3.common.util.Assertions.checkNotNull;
import static androidx.media3.common.util.Util.SDK_INT;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.media3.common.ErrorMessageProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.ui.PlayerView;

import com.example.zzt.sampleexomedia3.R;
import com.example.zzt.sampleexomedia3.util.ExoPlayerUtil;

import java.util.ArrayList;
import java.util.List;

public class ActExo extends AppCompatActivity {
    private static final String TAG = "ExoPlayer-Act";
    protected PlayerView playerView;

    private ExoPlayerUtil exoPlayerUtil;
    List<MediaItem> mediaItems;
    Button btn_frag;
    private int currentPos = 0;
    private ActivityResultLauncher<Intent> videoLocalFilePickerLauncher;
    public static final int FILE_PERMISSION_REQUEST_CODE = 1;
    private Uri localFileUri;

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_exo);

        videoLocalFilePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::videoLocalFilePickerLauncherResult);

        playerView = findViewById(R.id.player_view_1);
        btn_frag = findViewById(R.id.btn_frag);
        btn_frag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExoFragAct.start(view.getContext());
            }
        });

        findViewById(R.id.btn_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exoPlayerUtil != null) {
                    exoPlayerUtil.setPlayAndPause(true);
                }
            }
        });
        findViewById(R.id.btn_pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exoPlayerUtil != null) {
                    exoPlayerUtil.setPlayAndPause(false);
                }
            }
        });

        findViewById(R.id.btn_local).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 播放本地视频
                selectLocalFile(videoLocalFilePickerLauncher, "video/*");
            }
        });

//        playerView.setControllerVisibilityListener(new PlayerView.ControllerVisibilityListener() {
//            @Override
//            public void onVisibilityChanged(int visibility) {
//                Log.d(TAG, "> setControllerVisibilityListener onVisibilityChanged:" + visibility);
//            }
//        });
//        playerView.setErrorMessageProvider(new ErrorMessageProvider<PlaybackException>() {
//            @Override
//            public Pair<Integer, String> getErrorMessage(PlaybackException throwable) {
//                Log.e(TAG, "> PlaybackException " + throwable);
//                return new Pair<>(9999, "这是什么错误");
//            }
//        });
//        playerView.requestFocus();

        // 设置全屏按钮显示
//        playerView.setFullscreenButtonClickListener(new PlayerView.FullscreenButtonClickListener() {
//            @Override
//            public void onFullscreenButtonClick(boolean isFullScreen) {
//                Log.d(TAG, "> setFullscreenButtonClickListener onFullscreenButtonClick:" + isFullScreen);
//            }
//        });

        exoPlayerUtil = new ExoPlayerUtil(this, playerView);
        getLifecycle().addObserver(exoPlayerUtil);

        mediaItems = createMediaItems();

        exoPlayerUtil.setCurrentMediaItem(mediaItems.get(currentPos));

        View viewById = findViewById(R.id.ll_next);
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaItems != null) {
                    currentPos++;
                    if (currentPos > (mediaItems.size() - 1)) {
                        currentPos = 0;
                    }
                    exoPlayerUtil.setCurrentMediaItemToPlayer(mediaItems.get(currentPos));
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FILE_PERMISSION_REQUEST_CODE && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            launchLocalFilePicker(videoLocalFilePickerLauncher, "video/*");
        }
    }

    // Activity input
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (exoPlayerUtil != null) {
            boolean exoBoo = exoPlayerUtil.dispatchKeyEvent(event);
            if (exoBoo) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @OptIn(markerClass = UnstableApi.class)
    private List<MediaItem> createMediaItems() {
        MediaItem mediaItem6 = MediaItem.fromUri("https://6dee75c44c91925f5f306fd05ae36ba8.h2.smtcdns.net/pull-flv-l11.douyincdn.com/third/stream-114207517177545180_or4.flv?expire=1702435702&sign=7f2c5ea42db8a852d4cdd4c6236e2ad1&abr_pts=-800&_session_id=037-20231206104821E669BBCD058DF6C487E0.1701830902850.65886&TxLiveCode=cold_stream&TxDispType=3&svr_type=live_oc&tencent_test_client_ip=58.33.120.163&dispatch_from=OC_MGR60.188.95.104&utime=1701830903066");
        MediaItem mediaItem5 = new MediaItem.Builder().setUri("http://live-xtrend.thextrend.com/Daramola/Daramola.flv?auth_key=1701773161033-0-0-358c3e082a657d56592ee5c6a8c09342").build();
        MediaItem mediaItem3 = new MediaItem.Builder().setUri("https://oss.xtrendspeed.com/video/classRoom/how_use_credit-cn.mp4").setMediaMetadata(new MediaMetadata.Builder().setTitle("如何使用奖励金").build()).setMimeType(MimeTypes.VIDEO_MP4).build();
        MediaItem mediaItem4 = new MediaItem.Builder().setUri("https://oss.xtrendspeed.com/video/classRoom/about_you-cn.mp4").setMediaMetadata(new MediaMetadata.Builder().setTitle("如何使用奖励金").build()).setMimeType(MimeTypes.VIDEO_MP4).build();
        MediaItem mediaItem1 = new MediaItem.Builder().setUri("https://html5demos.com/assets/dizzy.mp4").setMediaMetadata(new MediaMetadata.Builder().setTitle("MP4 (480x360): Dizzy (H264/aac)").build()).setMimeType(MimeTypes.VIDEO_MP4).build();
        MediaItem mediaItem2 = new MediaItem.Builder().setUri("https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_16x9/bipbop_16x9_variant.m3u8").setMediaMetadata(new MediaMetadata.Builder().setTitle("HLS (adaptive): Apple 16x9 basic stream (TS/h264/aac)").build()).setMimeType(MimeTypes.APPLICATION_M3U8).build();
        List<MediaItem> mediaItems = new ArrayList<>();
        mediaItems.add(mediaItem6);
        mediaItems.add(mediaItem5);
        mediaItems.add(mediaItem3);
        mediaItems.add(mediaItem4);
        mediaItems.add(mediaItem1);
        mediaItems.add(mediaItem2);
        return mediaItems;
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void selectLocalFile(ActivityResultLauncher<Intent> localFilePickerLauncher, String mimeType) {
        String permission = Build.VERSION.SDK_INT >= 33 ? READ_MEDIA_VIDEO : READ_EXTERNAL_STORAGE;
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, FILE_PERMISSION_REQUEST_CODE);
        } else {
            launchLocalFilePicker(localFilePickerLauncher, mimeType);
        }
    }

    private void launchLocalFilePicker(ActivityResultLauncher<Intent> localFilePickerLauncher, String mimeType) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(mimeType);
        localFilePickerLauncher.launch(intent);
    }

    private void videoLocalFilePickerLauncherResult(ActivityResult result) {
        Intent data = result.getData();
        if (data != null) {
            localFileUri = data.getData();
            Log.d(TAG, "本地url:" + localFileUri);
            MediaItem mediaItem = MediaItem.fromUri(localFileUri);
            exoPlayerUtil.setCurrentMediaItemToPlayer(mediaItem);
        }
    }
}

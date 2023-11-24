package com.example;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.zzt.zt_exoplayersample.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;
import com.google.android.exoplayer2.ui.StyledPlayerView;

public class ExoSampleAct extends AppCompatActivity {
    StyledPlayerView player_view2;
    PlayerView player_view1;
    StyledPlayerControlView pay_control;
    PlayerControlView exo_controller;
    private static final String CONTENT_URI_SHORT = "https://storage.googleapis.com/exoplayer-test-media-1/mp4/android-screens-10s.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_sample);

        player_view2 = findViewById(R.id.player_view2);
        player_view1 = findViewById(R.id.player_view1);
        pay_control = findViewById(R.id.pay_control);
        exo_controller = findViewById(R.id.exo_controller);

        ExoPlayer player = new ExoPlayer.Builder(this).build();

        // Bind the player to the view.
        player_view1.setPlayer(player);
        player_view1.setUseController(true);
        pay_control.setPlayer(player);

        // Build the media item.
        MediaItem mediaItem = MediaItem.fromUri(CONTENT_URI_SHORT);
        // Set the media item to be played.
        player.setMediaItem(mediaItem);
        // Prepare the player.
        player.prepare();
        // Start the playback.
        player.play();


    }
}
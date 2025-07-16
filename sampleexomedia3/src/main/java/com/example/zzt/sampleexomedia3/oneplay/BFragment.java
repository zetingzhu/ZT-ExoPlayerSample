package com.example.zzt.sampleexomedia3.oneplay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.MimeTypes;
import androidx.media3.ui.PlayerView;

import com.example.zzt.sampleexomedia3.R;
import com.example.zzt.sampleexomedia3.util.ExoPlayerUtil;


public class BFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private PlayerView player_view_1;
    private ExoPlayerUtil exoPlayerUtil;

    public BFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BFragment newInstance(String param1, String param2) {
        BFragment fragment = new BFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_a, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        player_view_1 = view.findViewById(R.id.player_view_1);

        player_view_1.requestFocus();
        exoPlayerUtil = new ExoPlayerUtil(getActivity(), player_view_1);
        getLifecycle().addObserver(exoPlayerUtil);

        MediaItem mediaItem = new MediaItem.Builder()
                .setUri("https://oss.xtrendspeed.com/video/classRoom/how_use_credit-cn.mp4")
                .setMediaMetadata(new MediaMetadata.Builder()
                        .setTitle("HLS (adaptive): Apple 16x9 basic stream (TS/h264/aac)")
                        .build())
                .setMimeType(MimeTypes.VIDEO_MP4)
                .build();
        exoPlayerUtil.setCurrentMediaItem(mediaItem);
    }
}
package com.example.zzt.sampleexomedia3.frag;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.MimeTypes;
import androidx.media3.ui.PlayerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zzt.sampleexomedia3.R;
import com.example.zzt.sampleexomedia3.util.ExoPlayerUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private PlayerView player_view_1;
    private ExoPlayerUtil exoPlayerUtil;

    public AFragment() {
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
    public static AFragment newInstance(String param1, String param2) {
        AFragment fragment = new AFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
                .setUri("http://live-xtrend.thextrend.com/Daramola/Daramola.flv?auth_key=1701773161033-0-0-358c3e082a657d56592ee5c6a8c09342")
                .setMediaMetadata(new MediaMetadata.Builder()
                        .setTitle("HLS (adaptive): Apple 16x9 basic stream (TS/h264/aac)")
                        .build())
                .setMimeType(MimeTypes.APPLICATION_M3U8)
                .build();
        exoPlayerUtil.setCurrentMediaItem(mediaItem);
    }
}
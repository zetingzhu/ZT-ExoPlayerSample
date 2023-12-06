package com.example.zzt.sampleexomedia3.act;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.zzt.sampleexomedia3.R;
import com.example.zzt.sampleexomedia3.frag.AFragment;

public class ExoFragAct extends AppCompatActivity {
    FrameLayout fl_fragment;

    public static void start(Context context) {
        Intent starter = new Intent(context, ExoFragAct.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_frag);
        initView();
    }

    private void initView() {
        fl_fragment = findViewById(R.id.fl_fragment);

        AFragment aFragment = AFragment.newInstance("", "");
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fl_fragment, aFragment);
        fragmentTransaction.commitAllowingStateLoss();

    }
}
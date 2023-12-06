package com.example.zzt.sampleexomedia3.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.zzt.sampleexomedia3.R;


/**
 * 这个类封装了loading动画
 */
public class AppLoadingLayoutLive extends RelativeLayout {

    /**
     * 旋转图片
     */
    private ImageView imgView;
    /**
     * 提示TextView
     */
    private TextView apptextview;

    private String textLoading;

    /**
     * 构造方法
     *
     * @param context context
     */
    public AppLoadingLayoutLive(Context context) {
        this(context, null);
    }


    /**
     * 构造方法
     *
     * @param context context
     * @param attrs   attrs
     */
    public AppLoadingLayoutLive(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppLoadingLayoutLive(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AppLoadingLayout, defStyle, 0);
        textLoading = a.getString(R.styleable.AppLoadingLayout_loading_text);
        a.recycle();
        init(context);
    }

    protected View createLoadingView(Context context) {
        View container = LayoutInflater.from(context).inflate(R.layout.layout_progressbar, null);
        imgView = container.findViewById(R.id.imgView);
        apptextview = container.findViewById(R.id.apptextview);

        imgView.setImageDrawable(null);
        imgView.setBackgroundResource(R.drawable.live_loading);
        AnimationDrawable animaition = (AnimationDrawable) imgView.getBackground();
        animaition.setOneShot(false);
        animaition.start();             //启动动画

        apptextview.setTextColor(context.getResources().getColor(R.color.white));
        if (!TextUtils.isEmpty(textLoading)) {
            apptextview.setText(textLoading);
        }
        return container;
    }

    /**
     * 初始化
     *
     * @param context context
     */
    private void init(Context context) {
        addView(createLoadingView(context));
    }

    public TextView getApptextview() {
        return apptextview;
    }

    public void setApptextview(TextView apptextview) {
        this.apptextview = apptextview;
    }
}

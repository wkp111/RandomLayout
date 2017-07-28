package com.wkp.randomlayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wkp111 on 2017/4/3.
 */

public class FlyLayout extends FrameLayout implements RandomLayout.OnItemClickListener, RandomLayout.OnAnimationEndListener {

    private Context mContext;

    public FlyLayout(Context context) {
        this(context, null);
    }

    public FlyLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void setData(@NonNull String[]... params) {
        removeAllViews();
        for (String[] param : params) {
            RandomLayout randomLayout = new RandomLayout(mContext);
            randomLayout.setData(param);
            randomLayout.setOnItemClickListener(this);
            randomLayout.setOnAnimationEndListener(this);
            addView(randomLayout);
        }
    }

    public void setData(@NonNull List<String>... params) {
        removeAllViews();
        for (List<String> param : params) {
            RandomLayout randomLayout = new RandomLayout(mContext);
            randomLayout.setData((ArrayList<String>) param);
            randomLayout.setOnItemClickListener(this);
            randomLayout.setOnAnimationEndListener(this);
            addView(randomLayout);
        }
    }

    public void setData(@NonNull ArrayList<String[]> params) {
        removeAllViews();
        for (String[] param : params) {
            RandomLayout randomLayout = new RandomLayout(mContext);
            randomLayout.setData(param);
            randomLayout.setOnItemClickListener(this);
            randomLayout.setOnAnimationEndListener(this);
            addView(randomLayout);
        }
    }

    public void setData(@NonNull List<List<String>> params) {
        removeAllViews();
        for (List<String> param : params) {
            RandomLayout randomLayout = new RandomLayout(mContext);
            randomLayout.setData((ArrayList<String>) param);
            randomLayout.setOnItemClickListener(this);
            randomLayout.setOnAnimationEndListener(this);
            addView(randomLayout);
        }
    }

    public interface OnFlyEverythingListener{
        void onItemClick(View view, int position, String text);
        void onAnimationEnd(RandomLayout randomLayout, int animationCount);
    }

    private OnFlyEverythingListener mListener;

    public void setOnFlyEverythingListener(OnFlyEverythingListener listener) {
        mListener = listener;
    }

    public void startAnimation() {
        if (getChildCount() > 0) {
            ((RandomLayout) getChildAt(getChildCount() - 1)).startAnimation();
        }
    }

    @Override
    public void onItemClick(View view, int position, String text) {
        if (mListener != null) {
            mListener.onItemClick(view,position,text);
        }
    }

    @Override
    public void onAnimationEnd(RandomLayout randomLayout, int animationCount) {
        removeView(randomLayout);
        addView(randomLayout,0);
        if (mListener != null) {
            mListener.onAnimationEnd(randomLayout,animationCount);
        }
    }
}

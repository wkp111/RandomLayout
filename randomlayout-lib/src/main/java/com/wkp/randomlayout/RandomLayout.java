package com.wkp.randomlayout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.wkp.randomlayout.RandomLayout.LayoutData.SIZE_10SP;
import static com.wkp.randomlayout.RandomLayout.LayoutData.SIZE_12SP;
import static com.wkp.randomlayout.RandomLayout.LayoutData.SIZE_14SP;
import static com.wkp.randomlayout.RandomLayout.LayoutData.SIZE_16SP;
import static com.wkp.randomlayout.RandomLayout.LayoutData.SIZE_18SP;
import static com.wkp.randomlayout.RandomLayout.LayoutData.SIZE_20SP;
import static com.wkp.randomlayout.RandomLayout.LayoutData.SIZE_22SP;
import static com.wkp.randomlayout.RandomLayout.LayoutData.SIZE_24SP;
import static com.wkp.randomlayout.RandomLayout.LayoutData.SIZE_26SP;
import static com.wkp.randomlayout.RandomLayout.LayoutData.SIZE_28SP;
import static com.wkp.randomlayout.RandomLayout.LayoutData.SIZE_30SP;

/**
 * Created by wkp111 on 2017/4/2.
 */
public class RandomLayout extends ViewGroup {
    private List<? extends LayoutData> mData;
    private final Context mContext;
    private static final int TV_PADDING_DEFAULT = 4;
    private static final int TV_MARGIN_DEFAULT = 4;
    private int mTvPadding = TV_PADDING_DEFAULT;
    private int mTvMarginX = TV_MARGIN_DEFAULT;
    private int mTvMarginY = TV_MARGIN_DEFAULT;

    private int groupWidth, groupHeight;
    private int childWidth, childHeight;
    private int childLeft, childTop, childRight, childBottom;
    private GestureDetector mDetector;
    private float mCriticalVelocity;
    private float mDetectorVelocity = 60;
    private int mAnimationCount;
    private long mTotalDuration = 2000;

    public RandomLayout(Context context) {
        this(context, null);
    }

    public RandomLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RandomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setBackgroundColor(0xffffffff);
        //临界速度
        mCriticalVelocity = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mDetectorVelocity, getResources().getDisplayMetrics());
        mDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (mIsStartAnimation && Math.hypot(velocityX, velocityY) >= mCriticalVelocity) {
                    startAnimation();
                }
                return false;
            }
        });
    }

    public boolean mIsStartAnimation = true;

    public void startAnimation() {
        AnimationSet set = new AnimationSet(false);
        ScaleAnimation sa = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(mTotalDuration);
        set.addAnimation(sa);

        AlphaAnimation aa = new AlphaAnimation(1, 0);
        aa.setDuration(mTotalDuration / 2);
        aa.setStartOffset(mTotalDuration / 2);
        set.addAnimation(aa);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mIsStartAnimation = false;
                mAnimationCount++;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIsStartAnimation = true;
                animation.setAnimationListener(null);
                clearAnimation();
                if (mAnimationEndListener != null) {
                    mAnimationEndListener.onAnimationEnd(RandomLayout.this,mAnimationCount);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        startAnimation(set);
    }

    public interface OnAnimationEndListener{
        void onAnimationEnd(RandomLayout randomLayout,int animationCount);
    }

    private OnAnimationEndListener mAnimationEndListener;

    public void setOnAnimationEndListener(OnAnimationEndListener animationEndListener) {
        mAnimationEndListener = animationEndListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        for (int i = 0; i < getChildCount(); i++) {
            measureChildWithMargins(getChildAt(i), widthMeasureSpec, 0, heightMeasureSpec, 0);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        groupWidth = w;
        groupHeight = h;
    }

    private List<ViewAreaIndex> mViewAreaIndices = new ArrayList<>();
    private List<View> mLayoutChildViews = new ArrayList<>();
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!changed) {
            return;
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            ViewAreaIndex viewAreaIndex = i >= mViewAreaIndices.size() ? null : mViewAreaIndices.get(i);
            if (viewAreaIndex == null) {
                mViewAreaIndices.add(i, new ViewAreaIndex(i, getChildAt(i)));
            } else {
                viewAreaIndex.setIndex(i);
                viewAreaIndex.setArea(getChildAt(i));
            }
        }
        //因为集合mViewAreaIndices是没有清空的（复用），当切换数据时，并且数据小于集合大小时，会导致空指针异常，所以要保证长度一致
        if (mViewAreaIndices.size() > childCount) {
            for (int i = mViewAreaIndices.size() - 1; i >= childCount; i--) {
                mViewAreaIndices.remove(i);
            }
        }
        Collections.sort(mViewAreaIndices);
        mLayoutChildViews.clear();
        for (int i = mViewAreaIndices.size() - 1; i >= 0; i--) {
            View childAt = getChildAt(mViewAreaIndices.get(i).mIndex);
            int tryCount = 10;
            while (true) {
                if (tryLayout(childAt)) {
                    mLayoutChildViews.add(childAt);
                    break;
                }

                if (tryCount <= 0) {
                    childAt.layout(-1, -1, -1, -1);
                    break;
                }

                tryCount--;
            }
        }
    }

    private Rect mLayoutRect = new Rect();
    private Rect mTryRect = new Rect();

    private boolean tryLayout(View childView) {
        int leftMargin = ((MarginLayoutParams) childView.getLayoutParams()).leftMargin;
        int topMargin = ((MarginLayoutParams) childView.getLayoutParams()).topMargin;
        int rightMargin = ((MarginLayoutParams) childView.getLayoutParams()).rightMargin;
        int bottomMargin = ((MarginLayoutParams) childView.getLayoutParams()).bottomMargin;
        childWidth = childView.getMeasuredWidth() + leftMargin + rightMargin;
        childHeight = childView.getMeasuredHeight() + topMargin + bottomMargin;
        childLeft = mRandom.nextInt(groupWidth - childWidth + 1 <= 0 ? 1 : groupWidth - childWidth + 1);
        childTop = mRandom.nextInt(groupHeight - childHeight + 1 <= 0 ? 1 : groupHeight - childHeight + 1);
        childRight = childLeft + childWidth;
        childBottom = childTop + childHeight;
        mTryRect.set(childLeft, childTop, childRight, childBottom);
        for (View v : mLayoutChildViews) {
            int leftMarginL = ((MarginLayoutParams) v.getLayoutParams()).leftMargin;
            int topMarginL = ((MarginLayoutParams) v.getLayoutParams()).topMargin;
            int rightMarginL = ((MarginLayoutParams) v.getLayoutParams()).rightMargin;
            int bottomMarginL = ((MarginLayoutParams) v.getLayoutParams()).bottomMargin;
            mLayoutRect.set(v.getLeft() - leftMarginL, v.getTop() - topMarginL, v.getRight() + rightMarginL, v.getBottom() + bottomMarginL);
            if (mTryRect.intersect(mLayoutRect)) {
                return false;
            }
        }
        childView.layout(childLeft + leftMargin, childTop + topMargin, childRight - rightMargin, childBottom - bottomMargin);
        return true;
    }

    private static class ViewAreaIndex implements Comparable<ViewAreaIndex> {
        int mIndex;
        int mArea;

        public ViewAreaIndex(int index, View view) {
            mIndex = index;
            mArea = view.getMeasuredWidth() * view.getMeasuredHeight();
        }

        public void setIndex(int index) {
            mIndex = index;
        }

        public void setArea(View view) {
            mArea = view.getMeasuredWidth() * view.getMeasuredHeight();
        }

        @Override
        public int compareTo(ViewAreaIndex o) {
            return this.mArea - o.mArea;
        }
    }

    /**
     * 初始化数据
     */
    private List<DefaultLayoutData> mDefaultLayoutDatas = new ArrayList<>();

    public void setData(@NonNull ArrayList<String> data) {
        mDefaultLayoutDatas.clear();
        for (String s : data) {
            mDefaultLayoutDatas.add(new DefaultLayoutData(s));
        }
        setData(mDefaultLayoutDatas);
    }

    public void setData(@NonNull String[] data) {
        mDefaultLayoutDatas.clear();
        for (String s : data) {
            mDefaultLayoutDatas.add(new DefaultLayoutData(s));
        }
        setData(mDefaultLayoutDatas);
    }

    public void setData(@NonNull List<? extends LayoutData> data) {
        mData = data;
        initData();
    }

    public void setAnimationDuration(long animationDuration) {
        animationDuration = animationDuration < 100 ? 100 : animationDuration;
        mTotalDuration = animationDuration;
    }

    public void setDetectorVelocity(float detectorVelocity) {
        detectorVelocity = detectorVelocity < 10 ? 10 : detectorVelocity;
        mDetectorVelocity = detectorVelocity;
    }

    /**
     * 设置TextView的参数
     *
     * @param size
     */
    public void setDefaultSize(@Size int size) {
        defaultSize = size;
    }

    public void setTvPadding(int tvPadding) {
        mTvPadding = tvPadding;
    }

    public void setTvMargin(int tvMarginX, int tvMarginY) {
        mTvMarginX = tvMarginX;
        mTvMarginY = tvMarginY;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, String text);
    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    private void initData() {
        if (mData != null) {
            removeAllViews();
            for (int i = 0; i < mData.size(); i++) {
                final int position = i;
                LayoutData layoutData = mData.get(i);
                TextView textView = getTextView(layoutData);
                textView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onItemClick(v, position, ((TextView) v).getText().toString().trim());
                        }
                    }
                });
                addView(textView);
            }
        }
    }

    private TextView getTextView(LayoutData layoutData) {
        int tvPadding = pxForDp(mTvPadding);
        int tvMarginX = pxForDp(mTvMarginX);
        int tvMarginY = pxForDp(mTvMarginY);
        int tvTextSize = pxForSp(layoutData.getTextSize());
        TextView textView = new TextView(mContext);
        textView.setText(layoutData.getText());
        textView.setTextSize(tvTextSize);
        //shape图形
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        int bgColor = getBgColor();
        gd.setColor(bgColor);
        gd.setCornerRadius(pxForDp(TV_PADDING_DEFAULT));
        ViewCompat.setBackground(textView, gd);
        textView.setTextColor(getTextColor(bgColor));
        textView.setPadding(tvPadding, tvPadding, tvPadding, tvPadding);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(tvMarginX, tvMarginY, tvMarginX, tvMarginY);
        textView.setLayoutParams(params);
        return textView;
    }

    @ColorInt
    private int getTextColor(@ColorInt int bgColor) {
        int averageColor = (Color.red(bgColor) + Color.green(bgColor) + Color.blue(bgColor)) / 3;
        return averageColor > 0xff / 2 ? Color.BLACK : Color.WHITE;
    }

    private Random mRandom = new Random();

    @ColorInt
    private int getBgColor() {
        return Color.argb(0xff, mRandom.nextInt(0x100), mRandom.nextInt(0x100), mRandom.nextInt(0x100));
    }

    private int pxForDp(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }

    private int pxForSp(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    /**
     * Created by wkp111 on 2017/4/2.
     */

    public interface LayoutData {
        int SIZE_10SP = 5;
        int SIZE_12SP = 6;
        int SIZE_14SP = 7;
        int SIZE_16SP = 8;
        int SIZE_18SP = 9;
        int SIZE_20SP = 10;
        int SIZE_22SP = 12;
        int SIZE_24SP = 14;
        int SIZE_26SP = 16;
        int SIZE_28SP = 18;
        int SIZE_30SP = 20;

        String getText();

        @Size
        int getTextSize();
    }

    private int defaultSize = SIZE_10SP;

    private class DefaultLayoutData implements LayoutData {
        String mText;

        public DefaultLayoutData(String text) {
            mText = text;
        }

        @Override
        public String getText() {
            return mText;
        }

        @Override
        public int getTextSize() {
            return defaultSize;
        }
    }

    @IntDef({SIZE_10SP, SIZE_12SP, SIZE_14SP, SIZE_16SP, SIZE_18SP, SIZE_20SP, SIZE_22SP, SIZE_24SP, SIZE_26SP, SIZE_28SP, SIZE_30SP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Size {
    }


    //解决((MarginLayoutParams) v.getLayoutParams()).leftMargin拿不到问题
    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }


    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }
    }
}

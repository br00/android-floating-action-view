package com.alessandroborelli.design;

/**
 * Created by aborelli on 27/02/2017.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class FloatingActionView extends FrameLayout {

    public static final String FAB_SIZE_WIDTH = "width";
    public static final String FAB_SIZE_HEIGHT = "height";

    private LinearLayout mOutsideLLayout;
    private FloatingActionButton mFAB;
    private String mUseFabSize;
    private int mFabResId;
    private boolean mUseFabBackgroundColor;
    private boolean mUseMargin;
    private boolean mIsAnimating = false;

    private OnFloatingActionViewListener mListener;

    public FloatingActionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingActionView(Context context) {
        this(context, null);
    }

    public FloatingActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Retrieve the style attributes
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionView, defStyleAttr, 0);
        mFabResId = attributes.getResourceId(R.styleable.FloatingActionView_fab, -1);
        mUseFabBackgroundColor = attributes.getBoolean(R.styleable.FloatingActionView_useFabBackgroundColor, false);
        mUseMargin = attributes.getBoolean(R.styleable.FloatingActionView_useFabMargin, false);
        mUseFabSize = attributes.getString(R.styleable.FloatingActionView_useFabSize);
        int outsideColor = attributes.getColor(R.styleable.FloatingActionView_outsideColor, getResources().getColor(android.R.color.transparent));
        attributes.recycle();

        // Create outside layout
        mOutsideLLayout = new LinearLayout(context);
        mOutsideLLayout.setBackgroundColor(outsideColor);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mOutsideLLayout.setLayoutParams(layoutParams);
        OnTouchListener mTouchListener = new OnTouchListener() {
            @Override
            public boolean onTouch(@NonNull View v, @NonNull MotionEvent event) {
                final int x = (int) event.getX();
                final int y = (int) event.getY();
                Rect bounds = new Rect();
                getHitRect(bounds);
                if (!bounds.contains(x, y)) {
                    close();
                    return true;
                }
                return false;
            }
        };
        mOutsideLLayout.setOnTouchListener(mTouchListener);
    }

    public void initView() {
        // Get anchored fab
        mFAB = (FloatingActionButton) getRootView().findViewById(mFabResId);

        if (mFAB == null) {
            throw new RuntimeException("Please specify you FAB ID in your FloatingActionView layout!");
        }

        if (mUseFabBackgroundColor) {
            // Set same background color of the fab
            setBackgroundColor(mFAB.getBackgroundTintList().getDefaultColor());
        }

        // Set same position of the fab
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) getLayoutParams();
        layoutParams.gravity = Gravity.BOTTOM | Gravity.END;

        if (mUseMargin) {
            // Set same margin of the fab
            CoordinatorLayout.LayoutParams fabLP = (CoordinatorLayout.LayoutParams) mFAB.getLayoutParams();
            layoutParams.setMargins(fabLP.leftMargin, fabLP.topMargin, fabLP.rightMargin, fabLP.bottomMargin);
        }

        setLayoutParams(layoutParams);

        // Set elevation
        ViewCompat.setElevation(this, 10);

        // Hide at the beginning
        setVisibility(View.GONE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) getLayoutParams();
        if (mUseFabSize != null && !mUseFabSize.isEmpty() && mFAB != null) {
            if (mUseFabSize.equals(FAB_SIZE_WIDTH)) {
                layoutParams.width = mFAB.getMeasuredWidth();
            } else if (mUseFabSize.equals(FAB_SIZE_HEIGHT)) {
                layoutParams.height = mFAB.getMeasuredHeight();
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initView();
        attachOutsideLayout();
    }

    private void attachOutsideLayout() {
        if (mOutsideLLayout != null) {
            ((ViewGroup) getParent()).addView(mOutsideLLayout);
            mOutsideLLayout.setVisibility(View.GONE);
        }
    }

    public void open() {
        if (isOpened() || mIsAnimating) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setVisibility(View.VISIBLE);
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onGlobalLayout() {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    startOpenAnimations();
                }
            });
        } else {
            setVisibility(View.VISIBLE);
            mOutsideLLayout.setVisibility(View.VISIBLE);
            mFAB.setVisibility(View.GONE);
            if (mListener != null) {
                mListener.onOpen();
            }
        }
    }

    public void close() {
        if (!isOpened() || mIsAnimating) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            starCloseAnimations();
        } else {
            setVisibility(View.GONE);
            mOutsideLLayout.setVisibility(View.GONE);
            mFAB.setVisibility(View.VISIBLE);
            if (mListener != null) {
                mListener.onClose();
            }
        }
    }

    public boolean isOpened() {
        return getVisibility() == View.VISIBLE;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startOpenAnimations() {

        mIsAnimating = true;

        // Reveal
        int centerX = getWidth() - (mFAB.getWidth()/2);
        int centerY = getHeight() - (mFAB.getHeight()/2);
        float startRadius = 0;
        float endRadius = Math.max(getWidth(), getHeight());
        Animator reveal = ViewAnimationUtils.createCircularReveal(this, centerX, centerY, startRadius, endRadius);
        reveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsAnimating = false;
                if (mListener != null) {
                    mListener.onOpen();
                }
            }
        });

        // Fade out fab
        mFAB.setVisibility(View.VISIBLE);
        mFAB.setAlpha(0f);
        Animator fadeOut = ObjectAnimator.ofFloat(mFAB, View.ALPHA, 1, 0);

        // Fade in Outside layout
        mOutsideLLayout.setVisibility(View.VISIBLE);
        mOutsideLLayout.setAlpha(0);
        Animator fadeIn = ObjectAnimator.ofFloat(mOutsideLLayout, View.ALPHA, 0, 1);

        // Animations
        AnimatorSet set = new AnimatorSet();
        set.playTogether(reveal, fadeIn, fadeOut);
        set.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void starCloseAnimations() {

        mIsAnimating = true;

        // UnReveal
        int centerX = getWidth() - (mFAB.getWidth()/2);
        int centerY = getHeight() - (mFAB.getHeight()/2);
        float startRadius = Math.max(getWidth(), getHeight());
        float endRadius = 0;
        Animator reveal = ViewAnimationUtils.createCircularReveal(this, centerX, centerY, startRadius, endRadius);
        reveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setVisibility(View.GONE);
                mOutsideLLayout.setVisibility(View.GONE);
                mIsAnimating = false;
                if (mListener != null) {
                    mListener.onClose();
                }
            }
        });

        // Fade out Outside layout
        mOutsideLLayout.setAlpha(1);
        Animator fadeOut = ObjectAnimator.ofFloat(mOutsideLLayout, View.ALPHA, 1, 0);

        // Fade in fab
        mFAB.setVisibility(View.VISIBLE);
        mFAB.setAlpha(1f);
        Animator fadeIn = ObjectAnimator.ofFloat(mFAB, View.ALPHA, 0, 1);

        // Animations
        AnimatorSet set = new AnimatorSet();
        set.playTogether(reveal, fadeIn, fadeOut);
        set.start();
    }


    public void setOnFloatingActionViewListener(OnFloatingActionViewListener aListener) {
        mListener = aListener;
    }

    public interface OnFloatingActionViewListener {
        void onOpen();
        void onClose();
    }
}

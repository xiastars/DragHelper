package com.example.dell.draghelper;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class BaseDragView extends RelativeLayout {
    /* 宽度 */
    int mWidth = 0;
    /* 高度 */
    int mHeight = 0;
    /* 角色所处位置 */
    Rect mCoor;
    /* 是否在触摸状态 */
    boolean mIsPressed = false;

    CheckLongPressHelper mLongPressHelper;
    FrameLayout.LayoutParams mParams;

    public String getmActionName() {
        return mActionName;
    }

    public void setmActionName(String mActionName) {
        this.mActionName = mActionName;
    }

    /* 行动名称 */
    String mActionName;

    public BaseDragView(Context context) {
        super(context);
        mLongPressHelper = new CheckLongPressHelper(this);
    }

    @SuppressLint({ "ClickableViewAccessibility", "NewApi" })
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                performClick();
                if (!mIsPressed) {
                    mIsPressed = true;
                    ValueAnimator mScaleAnimation = ValueAnimator.ofFloat(0.85f, getScaleX());
                    mScaleAnimation.addUpdateListener(new AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float value = ((Float) animation.getAnimatedValue()).floatValue();
                            setScaleX(value);
                            setScaleY(value);
                        }
                    });
                    mScaleAnimation.setInterpolator(new OvershootInterpolator(1.2f));
                    mScaleAnimation.setDuration(300);
                    mScaleAnimation.start();
                }

                mLongPressHelper.postCheckForLongPress();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsPressed = false;
                mLongPressHelper.cancelLongPress();
                break;
        }
        return false;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int getmWidth() {
        return mWidth;
    }

    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public int getmHeight() {
        return mHeight;
    }

    public void setLayoutPosition(int left, int top) {

    }

    public void setmHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    public int getViewLeft() {
        return mParams.leftMargin;
    }

    public int getViewTop() {
        return mParams.topMargin;
    }

    public Rect getCoor() {
        return null;
    }

    public void resizeFrame(int mWidth, int mHeight) {
    }
}

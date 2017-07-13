package com.example.dell.draghelper;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

public class DragView extends BaseDragView {

    /* 带有图片可以缩放的 */
    private RelativeLayout rlPet;
    private ImageView ivImage;
    private CheckLongPressHelper mLongPressHelper;
    SingleClickListener onSingleClickListener;
    DoubleClickListener onDoubleClickListener;
    LongClickListener onLongClickListener;
    /* 是否在触摸状态 */
    boolean mIsPressed = false;
    /* 默认背景 */
    String mDefaultIcon;
    /* 音频正在播放中 */
    boolean mAudioPlaying;
    /* 要进行的动作类型与URL */
    String mActionUrl;
    /* 结束时X轴 */
    int endX;
    /* 默认情况下，不能拖动，只有选中了才能拖动 */
    boolean dragAble;
    /* 结束时Y轴 */
    int endY;
    /* 音频正在录制中 */
    boolean mAudioRecording;
    /* 准备走动阶段 */
    boolean prepareMoving;
    /**
     * 文字类型
     */
    public static final String ACTION_NAME_TEXT = "文字类型__";
    /* 第一次走动提醒 */
    String FRIST_MOVE_TIPS = "FRIST_MOVE_TIPS";

    private int resId;

    public DragView(Context context) {
        super(context);
        mLongPressHelper = new CheckLongPressHelper(this);
        mParams = (FrameLayout.LayoutParams) this.getLayoutParams();
        if (null == mParams) {
            mParams = new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            setLayoutParams(mParams);
        }
        addChildView();
    }

    private void addChildView() {
        View view = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_drag, null);
        rlPet = (RelativeLayout) view.findViewById(R.id.rl_pet);
        ivImage = (ImageView) view.findViewById(R.id.iv_pet);
        ivImage.setScaleType(ScaleType.FIT_XY);
        this.addView(view);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @SuppressLint("NewApi")
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setScaleX(1);
                setScaleY(1);
                // mDragLayer.checkOnScaleButton((int)event.getX(),
                // (int)event.getY());
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
                    mScaleAnimation.setDuration(100);
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

    public void cancelLongPress() {
        mIsPressed = false;
        mLongPressHelper.cancelLongPress();
    }

    public void setLayoutPosition(int x, int y,int width,int height) {
        if (null != mParams) {
        	android.view.ViewGroup.LayoutParams p = ivImage.getLayoutParams();
        	
        	p.width = width;
        	p.height = height;
            mParams.leftMargin = x;
            mParams.topMargin = y;
            requestLayout();
            invalidate();
        }
    }
    
    @Override
    public void setLayoutPosition(int x, int y) {
        if (null != mParams) {
            mParams.leftMargin = x;
            mParams.topMargin = y;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 设置默认资源图片
     */
    @SuppressWarnings("rawtypes")
    public void setDefalultIcon() {
        if (!TextUtils.isEmpty(mDefaultIcon)) {
            //SUtils.setPic(ivImage, mDefaultIcon, false);
        }
    }

    public void setDefalultIcon(int resId) {
        this.resId = resId;
        ivImage.setBackgroundResource(resId);
    }

    public void setDefalultIcon(String path) {
        setDefaultIconOnly(path);
        setDefalultIcon();
    }

    public void setDefaultIconOnly(String path) {
        this.mDefaultIcon = path;
    }

    @Override
    public Rect getCoor() {
        mCoor = new Rect();
        mCoor.left = mParams.leftMargin;
        mCoor.right = mParams.leftMargin + mWidth;
        mCoor.top = mParams.topMargin;
        mCoor.bottom = mParams.topMargin + mHeight;
        return mCoor;
    }

    @Override
    public android.view.ViewGroup.LayoutParams getLayoutParams() {
        return mParams;
    }

    public boolean isDragAble() {
        return dragAble;
    }

    @Override
    public void resizeFrame(int mWidth, int mHeight) {
        // mParams.width = mWidth;
        // mParams.height = mHeight;
        // RelativeLayout.LayoutParams pa = (LayoutParams)
        // ivImage.getLayoutParams();
        // pa.width = mWidth;
        // pa.height = mHeight;
    }

    private interface OnGetBitmapListener {
        void onSucceed();

        void onFailure();
    }

    public SingleClickListener getOnSingleClickListener() {
        return onSingleClickListener;
    }

    public void setOnSingleClickListener(SingleClickListener onSingleClickListener) {
        this.onSingleClickListener = onSingleClickListener;
    }

    public DoubleClickListener getOnDoubleClickListener() {
        return onDoubleClickListener;
    }

    public void setOnDoubleClickListener(DoubleClickListener onDoubleClickListener) {
        this.onDoubleClickListener = onDoubleClickListener;
    }

    public LongClickListener getOnLongClickListener() {
        return onLongClickListener;
    }

    public void setOnLongClickListener(LongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public interface SingleClickListener{
        void onSClick();
    }

    public interface DoubleClickListener{
        void onDClick();
    }

    public interface LongClickListener{
        void longClick();
    }

}

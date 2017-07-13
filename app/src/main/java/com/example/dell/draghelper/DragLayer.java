package com.example.dell.draghelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * 画布，所有的人物都在这个画布中
 *
 * @author xiastars
 */
public class DragLayer extends FrameLayout implements ViewGroup.OnHierarchyChangeListener {
    /* 当前处理的拖动项 */
    DragView mDragItem = null;
    /**
     * 当前DragItem的LayoutParams
     */
    LayoutParams mDragItemParams;
    Point downPoint = null;
    /**
     * 走动前的位置
     */
    Point mMovePre = new Point();
    /**
     * 走动后的位置
     */
    Point mMoveAfter = new Point();
    /**
     * 背景图片
     */
    ImageView mBackgroundView;
    /**
     * 装饰框
     */
    ImageView mFrameView;

    /**
     * 是否在于宠物编辑动作模式
     */
    boolean mViewOnEditMode = false;
    /**
     * 是否正按在缩放键上,0为左，1为上，2为右，3为下
     */
    int mViewOnScaleMode = -1;
    /**
     * 是否处在走动模式
     */
    boolean onMoveMode = false;
    /* 按下时间，判断长按 */
    long mDownTime = 0;
    /**
     * 播放或预览模式
     */
    boolean mPreviewMode = false;
    /**
     * 有人物正在走动中
     */
    boolean mMoving;
    /* 判断双击 */
    int mClickIndex = 0;
    /* 全屏模式 */
    boolean mFullScreenMode = false;
    /* 左边还是右边 */
    int leftOrRight = 0;
    /* 正在拖动中 */
    boolean mOnDrag = false;
    /* 背景图片 */
    String mBackgroundImg = null;
    /* 当前触摸按下位置X */
    int mMotionDownX = 0;
    /* 当前触摸按下位置Y */
    int mMotionDownY = 0;
    boolean frameNotifyed = false;
    /**
     * 编辑状态下与播放状态下的比例
     */
    float mPlayScale = 0;
    float mPlayScaleY = 0;

    float mDragItemOffsetX;
    float mDragItemOffsetY;

    @SuppressLint("NewApi")
    public DragLayer(Context context) {
        super(context);
        setMotionEventSplittingEnabled(false);
        setChildrenDrawingOrderEnabled(true);
        setOnHierarchyChangeListener(this);
        addBackgroundView();
        mPlayScale = 1240 / 1031f;
        mPlayScaleY = 697.5f / 580f;
    }

    @SuppressLint("NewApi")
	public DragLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMotionEventSplittingEnabled(false);
        setChildrenDrawingOrderEnabled(true);
        setOnHierarchyChangeListener(this);
        addBackgroundView();
        mPlayScale = 1240 / 1031f;
        mPlayScaleY = 697.5f / 580f;
    }

    /**
     * 添加背景
     */
    public void addBackgroundView() {
        mBackgroundView = new ImageView(getContext());
        addView(mBackgroundView);
        mBackgroundView.setClickable(false);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mBackgroundView.setLayoutParams(params);
        mBackgroundView.setScaleType(ScaleType.FIT_XY);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mOnDrag) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        /* 移动时不让点击 */
        if (ismMoving()) {
            return true;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();

        // if(null != mDetector){
        // mDetector.onTouchEvent(event);
        // }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downPoint = new Point(x, y);
                mOnDrag = false;
                mDragItem = checkOnChild(x, y);
            /* 检查是否按在物品四个角落点 */
                // if(checkOnScaleButton(x,y)){
                // return true;
                // }
                mDownTime = System.currentTimeMillis();

                mClickIndex++;
                if (null != mDragItem) {
                    mDragItemOffsetY = y - mDragItem.getViewTop();
                    mDragItemOffsetX = x - mDragItem.getViewLeft();
                    mMotionDownX = x;
                    mMotionDownY = y;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (0 == mMotionDownX && 0 == mMotionDownY) {
                    mMotionDownX = x;
                    mMotionDownY = y;
                }
            /* 走动模式,绘制拖动的线 */
                if (onMoveMode && null != mDragItem) {
                }
                handleMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                if (mDragItem == null) {
                    setmDragItem(null);
                } else {
                }
                mOnDrag = true;
                long curTime = System.currentTimeMillis();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        mClickIndex = 0;
                    }
                }, 200);
                if (curTime - mDownTime > 500) {
                    hanleActionUP(x, y);
                    if (mDragItem != null) {
                        if(mDragItem.getOnLongClickListener() != null){
                            mDragItem.getOnLongClickListener().longClick();
                        }
                    }
                } else {
                    if (Math.abs(x - mMotionDownX) < 5 && Math.abs(y - mMotionDownY) < 5) {
                        setOnEditMode(true);
                        if (mDragItem != null) {
                            if(mClickIndex > 1){
                                mClickIndex = 0;
                                if(mDragItem.getOnDoubleClickListener() != null){
                                    mDragItem.getOnDoubleClickListener().onDClick();
                                }
                            }else{
                                if(mDragItem.getOnSingleClickListener() != null){
                                    mDragItem.getOnSingleClickListener().onSClick();
                                }
                            }

                        }
                    } else {
                        hanleActionUP(x, y);
                    }
                }

                mDownTime = 0;
            case MotionEvent.ACTION_CANCEL:
                mOnDrag = true;
                if (onMoveMode) {
                    return true;
                }
                mDownTime = 0;
                break;
        }
        if (mDragItem != null) {
            return true;
        }
        return false;
    }

    /**
     * 抬手后设置宠物所在位置，并设置疆界束缚
     *
     * @param x
     * @param y
     */
    @SuppressLint("NewApi")
	private void hanleActionUP(int x, int y) {
        if (null == mDragItem)
            return;
        mDragItem.setTranslationX(0);
        mDragItem.setTranslationY(0);
        int left = x - mMotionDownX + mDragItem.getViewLeft();
        int top = y - mMotionDownY + mDragItem.getViewTop();

        if (mDragItem instanceof DragView) {
            DragView layout = (DragView) mDragItem;

            layout.setLayoutPosition(left, top);
            layout.cancelLongPress();
        } else {
            mDragItem.setTranslationX(0);
            mDragItem.setTranslationY(0);
            mDragItem.setLayoutPosition(left, top);
        }
        mDragItem.requestLayout();
        mDragItem.invalidate();
        deleteDragItem();
    }


    /**
     * 移动时改变拖动项的位置
     *
     * @param x
     * @param y
     */
    @SuppressLint("NewApi")
	public void handleMove(int x, int y) {
        if (null == mDragItem) {
            mMotionDownX = 0;
            mMotionDownY = 0;
            return;
        }
        if (mMotionDownX == 0 && mMotionDownY == 0) {
            return;
        }
        int tx = x - mMotionDownX;
        int ty = y - mMotionDownY;
        mDragItem.setTranslationX(tx);
        mDragItem.setTranslationY(ty);
    }


    /**
     * 检查触摸点是否在物品对象上
     *
     * @param x
     * @param y
     * @return
     */
    public DragView checkOnChild(int x, int y) {
        int count = this.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = this.getChildAt(count - i - 1);
            if (null != child) {
                Rect outRect = new Rect();
                if (child instanceof BaseDragView) {
                    BaseDragView view = (BaseDragView) child;
                    outRect = view.getCoor();
                    DragView layout = null;
                    if (view instanceof DragView) {
                        layout = (DragView) view;
                    }
                    if (outRect != null && outRect.contains(x, y)) {
                        if (layout != null) {
                            mPreviewMode = false;
                            setmDragItem(layout);
                            requestLayout();
                            invalidate();
                            return layout;
                        } else {
                        }
                        return null;
                    }
                }
            }
        }
        return null;
    }


    public void deleteDragItem() {
        mDragItem = null;
        mMotionDownX = 0;
        mMotionDownY = 0;
    }

    @Override
    public void onChildViewAdded(View parent, View child) {
    }

    @Override
    public void onChildViewRemoved(View parent, View child) {
    }

    public void setmDragItem(DragView item) {
        mDragItem = null;
        if (null == item) {
            mDragItemParams = null;
            return;
        }
        this.mDragItem = item;
        // mDetector = new ScaleGestureDetector(getContext(), listener);
        mDragItemParams = (LayoutParams) mDragItem.getLayoutParams();
    }

    private DragView isRelativeLayout() {
        if (null != mDragItem && mDragItem instanceof DragView) {
            DragView layout = (DragView) mDragItem;
            return layout;
        }
        return null;
    }

    /**
     * 设置为编辑模式
     *
     * @param b
     */
    public void setOnEditMode(boolean b) {
        this.mViewOnEditMode = b;
    }

    @Override
    public void setBackgroundResource(int resid) {
        if (mBackgroundView != null) {
            mBackgroundView.setBackgroundResource(resid);
        }
    }

    public boolean ismPreviewMode() {
        return mPreviewMode;
    }

    public void setmPreviewMode(boolean mPreviewMode) {
        this.mPreviewMode = mPreviewMode;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void clearAll() {
        mMoving = false;
        onMoveMode = false;
        mMotionDownX = 0;
        mMotionDownY = 0;
        mPreviewMode = false;
        mDragItem = null;
    }

    public boolean ismMoving() {
        return mMoving;
    }

    public void setmMoving(boolean mMoving) {
        this.mMoving = mMoving;
    }

}

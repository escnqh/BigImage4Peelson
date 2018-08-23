package com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag;

import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

import com.meitu.qihangni.bigimage4peelson.BaseApplication;


/**
 * 处理触摸事件
 *
 * @author Ljq 2018/4/19
 */
public class DragProxyWrapper implements DragContract.DragProxy {

    private static final int INVALID_POINTER = -1;
    private final int mMaxVelocity;
    private final int mMinVelocity;
    private int mSlopDistance; //大于这个距离，视为拖拽
    private int mDragState = DragState.SETTLING;
    private int mDragDirection;
    private float mTouchStartX;
    private float mTouchStartY;
    private SparseArray<DragContract.DragCallback> mDragCallbackArray = new SparseArray<>(4);
    private boolean mOpenVelocityTracker;
    private VelocityTracker mVelocityTracker;
    private int mActivePointerId = INVALID_POINTER;
    private RectF mArea;
    private DragContract.DragIntercept mDragIntercept;

    /**
     * @param openFastVelocity 是否开启快速滑动判断
     */
    public DragProxyWrapper(boolean openFastVelocity) {
        mOpenVelocityTracker = openFastVelocity;
        ViewConfiguration vc = ViewConfiguration.get(BaseApplication.getApplication());
        mSlopDistance = vc.getScaledTouchSlop();
        mMaxVelocity = vc.getScaledMaximumFlingVelocity();
        mMinVelocity = vc.getScaledMinimumFlingVelocity();
    }

    public DragProxyWrapper addDragDirection(@DragDirection.Direction int direction, @NonNull DragContract.DragCallback dragCallback) {
        mDragCallbackArray.put(direction, dragCallback);
        return this;
    }

    /**
     * 滑动有效区域，ACTION_DOWN在区域外不触发滑动，默认不做限制
     *
     * @param area
     * @return
     */
    public DragProxyWrapper setValidArea(@NonNull RectF area) {
        mArea = area;
        return this;
    }

    @Override
    public boolean drag(MotionEvent ev) {
        if (mDragCallbackArray.size() == 0) {
            return false;
        }
        boolean isDraging = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = ev.getX();
                mTouchStartY = ev.getY();
                mActivePointerId = ev.getPointerId(0);
                setDragState(DragState.SETTLING);
                if (mArea != null && !mArea.contains(mTouchStartX, mTouchStartY)) {
                    setDragState(DragState.IDEL);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float currentX = ev.getX();
                float currentY = ev.getY();
                if (mDragState == DragState.SETTLING) {
                    if (currentY - mTouchStartY >= mSlopDistance) {
                        if (dragDown(ev)) {
                            isDraging = true;
                            setDragState(DragState.DARGING);
                            break;
                        } else {
                            setDragState(DragState.IDEL);
                        }
                    }
                    if (mTouchStartY - currentY >= mSlopDistance) {
                        if (dragUp(ev)) {
                            isDraging = true;
                            setDragState(DragState.DARGING);
                            break;
                        } else {
                            setDragState(DragState.IDEL);
                        }
                    }
                    if (currentX - mTouchStartX >= mSlopDistance) {
                        if (dragRight(ev)) {
                            isDraging = true;
                            setDragState(DragState.DARGING);
                            break;
                        } else {
                            setDragState(DragState.IDEL);
                        }
                    }
                    if (mTouchStartX - currentX >= mSlopDistance) {
                        if (dragLeft(ev)) {
                            isDraging = true;
                            setDragState(DragState.DARGING);
                            break;
                        } else {
                            setDragState(DragState.IDEL);
                        }
                    }
                }
                if (mDragState == DragState.DARGING) {
                    if (mOpenVelocityTracker && mVelocityTracker == null) {
                        mVelocityTracker = VelocityTracker.obtain();
                    }
                    if (mVelocityTracker != null) {
                        mVelocityTracker.addMovement(ev);
                    }
                    DragContract.DragCallback dragCallback = mDragCallbackArray.get(mDragDirection);
                    if (dragCallback == null) {
                        break;
                    }
                    dragCallback.dragEvent(mTouchStartX, mTouchStartY, currentX, currentY);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mDragState == DragState.DARGING) {
                    DragContract.DragCallback dragCallback = mDragCallbackArray.get(mDragDirection);
                    if (dragCallback == null) {
                        break;
                    }
                    isDraging = true;
                    dragCallback.complete(isFastScroll());
                }
                cancel();
                break;
        }
        return isDraging;
    }

    private boolean dragRight(MotionEvent ev) {
        float currentX = ev.getX();
        float currentY = ev.getY();
        mDragDirection = DragDirection.RIGHT;
        DragContract.DragCallback dragCallback = mDragCallbackArray.get(mDragDirection);
        if (dragCallback == null) {
            return false;
        }
        if (Math.abs(currentX - mTouchStartX) <= Math.abs(currentY - mTouchStartY)) { //45度角区域
            return false;
        }
        return mDragIntercept == null || mDragIntercept.canDrag(ev, mDragDirection);
    }

    private boolean dragLeft(MotionEvent ev) {
        float currentX = ev.getX();
        float currentY = ev.getY();
        mDragDirection = DragDirection.LEFT;
        DragContract.DragCallback dragCallback = mDragCallbackArray.get(mDragDirection);
        if (dragCallback == null) {
            return false;
        }
        if (Math.abs(currentX - mTouchStartX) <= Math.abs(currentY - mTouchStartY)) { //45度角区域
            return false;
        }
        return mDragIntercept == null || mDragIntercept.canDrag(ev, mDragDirection);
    }

    private boolean dragUp(MotionEvent ev) {
        float currentX = ev.getX();
        float currentY = ev.getY();
        mDragDirection = DragDirection.UP;
        DragContract.DragCallback dragCallback = mDragCallbackArray.get(mDragDirection);
        if (dragCallback == null) {
            return false;
        }
        if (Math.abs(currentX - mTouchStartX) >= Math.abs(currentY - mTouchStartY)) { //45度角区域
            return false;
        }
        return mDragIntercept == null || mDragIntercept.canDrag(ev, mDragDirection);
    }

    private boolean dragDown(MotionEvent ev) {
        float currentX = ev.getX();
        float currentY = ev.getY();
        mDragDirection = DragDirection.DOWN;
        DragContract.DragCallback dragCallback = mDragCallbackArray.get(mDragDirection);
        if (dragCallback == null) {
            return false;
        }
        if (Math.abs(currentX - mTouchStartX) >= Math.abs(currentY - mTouchStartY)) { //45度角区域
            return false;
        }
        return mDragIntercept == null || mDragIntercept.canDrag(ev, mDragDirection);
    }

    @Override
    public void reset() {
        for (int i = 0; i < mDragCallbackArray.size(); i++) {
            DragContract.DragCallback dragCallback = mDragCallbackArray.get(i);
            if (dragCallback != null) {
                dragCallback.reset();
            }
        }
    }

    @Override
    public SparseArray<DragContract.DragCallback> getDragCallbackArray() {
        return mDragCallbackArray;
    }

    private void cancel() {
        mActivePointerId = INVALID_POINTER;
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * 是否是快速滑动
     *
     * @return
     */
    private boolean isFastScroll() {
        if (mVelocityTracker != null) {
            mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
            float xVelocity = mVelocityTracker.getXVelocity(mActivePointerId);
            return xVelocity >= mMinVelocity * 2;
        }
        return false;
    }

    private void setDragState(@DragState.State int state) {
        mDragState = state;
    }

    public void setDragIntercept(@NonNull DragContract.DragIntercept dragIntercept) {
        mDragIntercept = dragIntercept;
    }
}

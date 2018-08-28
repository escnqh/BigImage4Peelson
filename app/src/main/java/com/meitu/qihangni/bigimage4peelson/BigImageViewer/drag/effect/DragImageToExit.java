package com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.effect;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.view.View;

import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.AnimationHelper;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.AnimationManager;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.ContextUtils;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.DragActionListener;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.DragContract;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.ViewUtil;

/**
 * @author nqh 2018/8/27
 */
public class DragImageToExit implements DragContract.DragCallback {
    private static int ANIMATOR_DURATION = 150;
    private static float DEFAULT_CANCLE_TRANSLATION_PERCENT = 0.8f;
    private boolean isDraging = false;
    private boolean isClose = false;
    private float mMove;
    private float mScale;
    private View mDragView;
    private DragActionListener mDragActionListener;
    private View mBackgroundView;
    private int mDirection;
    private int mLocationX;
    private int mLocationY;
    private int mResourceHeight;
    private int mResourceWidth;


    public DragImageToExit(View dragView, @Nullable DragActionListener dragActionListener, int direction, View backgroundView, int locationX, int locationY, int resourceHeight, int resourceWidth) {
        mDragView = dragView;
        mDragActionListener = dragActionListener;
        mDirection = direction;
        mBackgroundView = backgroundView;
        mLocationX = locationX;
        mLocationY = locationY;
        mResourceHeight = resourceHeight;
        mResourceWidth = resourceWidth;
    }

    @Override
    public void dragEvent(float startX, float startY, float endX, float endY) {
        float distance = mDragView.getHeight();
        mMove = Math.abs(endY - startY);
        mScale = (distance - mMove / 2) / distance;
        if (mScale > 1) {
            mScale = 1f;
        }
        mDragView.setScaleX(mScale);
        mDragView.setScaleY(mScale);
        mDragView.setTranslationX(endX - startX);
        mDragView.setTranslationY(endY - startY);
        if (mBackgroundView != null) {
            ViewUtil.setVisible(mBackgroundView);
            float backgroundAlpha = (distance - Math.abs(mMove) / 3 * 2) / distance;
            mBackgroundView.setAlpha(backgroundAlpha);
        }
    }

    @Override
    public void complete(boolean isFastScroll) {
        isDraging = false;
        if (mScale >= DEFAULT_CANCLE_TRANSLATION_PERCENT) {
            cancel();
        } else {
            close();
        }
    }

    @Override
    public void reset() {
        if (isDraging) {
            isDraging = false;
            cancel();
        }
    }

    @Override
    public boolean isClose() {
        return isClose;
    }

    private void cancel() {
        if (mBackgroundView != null) {
            mBackgroundView.setAlpha(1f);
        }
        AnimationHelper.animationRestore(mDragView, ANIMATOR_DURATION, new AnimationManager.SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (ContextUtils.isContextValid(mDragView.getContext()) && mDragActionListener != null) {
                    mDragActionListener.onCancel();
                }
            }
        });
    }

    private void close() {
        isClose = true;
        AnimationHelper.animationTarget(mDragView, new RectF(mLocationX, mLocationY, mLocationX + mResourceWidth, mLocationY + mResourceHeight), ANIMATOR_DURATION, new ExitAnimatorListenerAdapter());
        AnimationHelper.animationAlpha(mBackgroundView, ANIMATOR_DURATION, 0f);
    }

    private class ExitAnimatorListenerAdapter extends AnimatorListenerAdapter {
        @Override
        public void onAnimationEnd(Animator animation) {
            Context context = mDragView.getContext();
            if (ContextUtils.isContextValid(context)) {
                if (context instanceof Activity) {
                    Activity act = (Activity) context;
                    if (mDragActionListener != null) {
                        mDragActionListener.onClose(mDirection);
                    } else {
                        act.finish();
                    }
                    act.overridePendingTransition(0, 0);
                }
            }
        }
    }
}

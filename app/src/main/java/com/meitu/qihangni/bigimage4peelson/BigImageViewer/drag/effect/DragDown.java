package com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.effect;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.AnimationHelper;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.ContextUtils;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.DragActionListener;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.DragContract;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.DragDirection;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.DragTargetViewCache;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.ViewUtil;


/**
 * 向下滑动退出
 * Created by ljq on 2018/6/19.
 */
public class DragDown implements DragContract.DragCallback {

    private static final int ANIMATOR_DURATION = 200;
    private static final float DEFAULT_CANCLE_SCALE = 0.9f; //大于这个缩放比例视为取消
    private float mScale;
    private boolean isDraging;
    private final ViewGroup mRootView; //根布局，
    private final View mBackgroundView;
    private final DragActionListener mDragActionListener;
    private final View mContentView;
    private boolean isClose;

    public DragDown(ViewGroup rootView, @Nullable View backgroundView, @Nullable DragActionListener dragActionListener) {
        mRootView = rootView;
        mContentView = mRootView.getChildAt(0);
        mBackgroundView = backgroundView;
        mDragActionListener = dragActionListener;
    }

    @Override
    public void dragEvent(float startX, float startY, float endX, float endY) {
        if (!isDraging) {
            if (mDragActionListener != null) {
                mDragActionListener.onStart();
            }
            isDraging = true;
        }
        float distance = mRootView.getHeight();
        float move = endY - startY;
        mScale = (distance - move / 2) / distance;
        if (mScale > 1) {
            mScale = 1;
        }
        mRootView.setScaleX(mScale);
        mRootView.setScaleY(mScale);
        mRootView.setTranslationX(endX - startX);
        mRootView.setTranslationY(endY - startY);

        if (mBackgroundView != null) {
            ViewUtil.setVisible(mBackgroundView);
            float bacgroundAlpha = (distance - Math.abs(move) / 3 * 2) / distance;
            mBackgroundView.setAlpha(bacgroundAlpha);
        }
    }


    @Override
    public void complete(boolean isFastScroll) {
        isDraging = false;
        if (mScale >= DEFAULT_CANCLE_SCALE) {
            cancel();
        } else {
            close();
        }
    }

    /**
     * 归位
     */
    private void cancel() {
        AnimationHelper.animationRestore(mRootView, ANIMATOR_DURATION, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (ContextUtils.isContextValid(mRootView.getContext())) {
                    if (mDragActionListener != null) {
                        mDragActionListener.onCancel();
                    }
                    if (mBackgroundView != null) {
                        ViewUtil.setGone(mBackgroundView);
                    }
                }
            }
        });
        if (mBackgroundView != null) {
            AnimationHelper.animationAlpha(mBackgroundView, ANIMATOR_DURATION, 1);
        }
    }

    /**
     * 关闭页面
     */
    private void close() {
        if (!closeToTarget()) {
            closeByAlpha();
        }
    }

    /**
     * 移动到目标Item的动画效果
     *
     * @return
     */
    private boolean closeToTarget() {
        View cacheTargetView = DragTargetViewCache.getCacheTargetView();
        if (cacheTargetView != null && !isClose) {
            RectF viewLocation = AnimationHelper.getViewLocation(cacheTargetView);
            if (viewLocation == null) {
                return false;
            }
            //创建一个View用于显示目标item的背景
            Bitmap cacheBitmapFromView = AnimationHelper.getCacheBitmapFromView(cacheTargetView);
            if (cacheBitmapFromView == null) {
                return false;
            }
            View tempView = new View(mRootView.getContext());
            tempView.setBackgroundDrawable(new BitmapDrawable(cacheBitmapFromView));
            mRootView.addView(tempView, mRootView.getChildCount(), new ViewGroup.LayoutParams(mRootView.getWidth(), mRootView.getHeight()));
            AnimationHelper.animationTarget(mRootView, viewLocation, ANIMATOR_DURATION, new CloseAnimatorListenerAdapter());

            //目标View透明度由0到1，当前View由1到0
            AnimationHelper.animationAlpha(tempView, ANIMATOR_DURATION, 0, 1);
            AnimationHelper.animationAlpha(mContentView, ANIMATOR_DURATION, 0);

            if (mBackgroundView != null) {
                AnimationHelper.animationAlpha(mBackgroundView, ANIMATOR_DURATION, 0);
            }
            isClose = true;
            return true;
        }
        return false;
    }

    private void closeByAlpha() {
        isClose = true;
        ObjectAnimator translationXAnimator = ObjectAnimator.ofFloat(mRootView, "alpha", 0);
        AnimatorSet set = new AnimatorSet();
        set.play(translationXAnimator);
        set.setDuration(ANIMATOR_DURATION);
        set.start();
        set.addListener(new CloseAnimatorListenerAdapter());

        if (mBackgroundView != null) {
            AnimationHelper.animationAlpha(mBackgroundView, ANIMATOR_DURATION, 0);
        }
    }

    class CloseAnimatorListenerAdapter extends AnimatorListenerAdapter {

        @Override
        public void onAnimationEnd(Animator animation) {
            Context context = mRootView.getContext();
            if (ContextUtils.isContextValid(context)) {
                if (context instanceof Activity) {
                    Activity act = (Activity) context;
                    if (mDragActionListener != null) {
                        mDragActionListener.onClose(DragDirection.DOWN);
                    } else {
                        act.finish();
                    }
                    act.overridePendingTransition(0, 0);
                }
            }
        }
    }

    /**
     * 重置
     */
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

}

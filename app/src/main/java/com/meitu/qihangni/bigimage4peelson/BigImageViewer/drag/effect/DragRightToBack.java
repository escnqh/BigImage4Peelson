package com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.effect;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;

import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.AnimationHelper;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.AnimationManager;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.ContextUtils;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.DragActionListener;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.DragContract;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.DragDirection;


/**
 * 默认右滑退出
 * Created by ljq on 2018/6/19.
 */
public class DragRightToBack implements DragContract.DragCallback {

    private static final int ANIMATOR_DURATION = 150;
    private static final float DEFAULT_CANCLE_TRANSLATION_PERCENT = 0.5f; //大于这个移动比例视为取消
    private boolean isDraging;
    private final View mView;
    private DragActionListener mDragActionListener;
    private float mMove;
    private boolean isClose;

    public DragRightToBack( View view, @Nullable DragActionListener dragActionListener) {
        mView = view;
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
        mMove = endX - startX;
        if (mMove < 0) {
            mMove = 0;
        }
        mView.setTranslationX(mMove);
    }

    @Override
    public void complete(boolean isFastScroll) {
        isDraging = false;
        if (!isFastScroll && mMove / mView.getWidth() <= DEFAULT_CANCLE_TRANSLATION_PERCENT) {
            cancel();
        } else {
            close();
        }
    }

    /**
     * 归位
     */
    private void cancel() {
        AnimationHelper.animationRestore(mView, ANIMATOR_DURATION, new AnimationManager.SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (ContextUtils.isContextValid(mView.getContext()) && mDragActionListener != null) {
                    mDragActionListener.onCancel();
                }
            }
        });
    }

    /**
     * 关闭页面
     */
    private void close() {
        isClose = true;
        AnimationHelper.animationBack(mView, ANIMATOR_DURATION, new CloseAnimatorListenerAdapter());
    }


    class CloseAnimatorListenerAdapter extends AnimatorListenerAdapter {

        @Override
        public void onAnimationEnd(Animator animation) {
            Context context = mView.getContext();
            if (ContextUtils.isContextValid(context)) {
                if (context instanceof Activity) {
                    Activity act = (Activity) context;
                    if (mDragActionListener != null) {
                        mDragActionListener.onClose(DragDirection.RIGHT);
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

package com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;


/**
 * 动画辅助类
 * Created by ljq on 2018/6/16.
 */
public class AnimationHelper {


    /**
     * 移动到目标动画
     *
     * @param view
     * @param locationOnFeedline
     * @param duration
     * @param listener
     */
    public static void animationTarget(@NonNull View view, @NonNull RectF locationOnFeedline, int duration, @Nullable Animator.AnimatorListener listener) {
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", locationOnFeedline.width() / view.getWidth());
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", locationOnFeedline.height() / view.getHeight());
        ObjectAnimator translationXAnimator = ObjectAnimator.ofFloat(view, "translationX", locationOnFeedline.left + locationOnFeedline.width() / 2 - view.getWidth() / 2);
        ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(view, "translationY", locationOnFeedline.top + locationOnFeedline.height() / 2 - view.getHeight() / 2);
        AnimatorSet set = new AnimatorSet();
        set.play(scaleXAnimator).with(scaleYAnimator).with(translationYAnimator).with(translationXAnimator);
        set.setDuration(duration);
        set.start();
        if (listener != null) {
            set.addListener(listener);
        }
    }

    /**
     * 复原动画
     *
     * @param view
     * @param duration
     * @param listener
     */
    public static void animationRestore(@NonNull View view, int duration, @Nullable Animator.AnimatorListener listener) {
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 1f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 1f);
        ObjectAnimator translationXAnimator = ObjectAnimator.ofFloat(view, "translationX", 0f);
        ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(view, "translationY", 0f);
        AnimatorSet set = new AnimatorSet();
        set.play(scaleXAnimator).with(scaleYAnimator).with(translationYAnimator).with(translationXAnimator);
        set.setDuration(duration);
        set.start();
        if (listener != null) {
            set.addListener(listener);
        }
    }

    /**
     * 向右滑出动画
     *
     * @param view
     * @param duration
     * @param listener
     */
    public static void animationBack(@NonNull View view, int duration, @Nullable Animator.AnimatorListener listener) {
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, "translationX", view.getWidth());
        AnimatorSet set = new AnimatorSet();
        set.play(scaleXAnimator);
        set.setDuration(duration);
        set.start();
        if (listener != null) {
            set.addListener(listener);
        }
    }

    /**
     * 透明度动画
     *
     * @param view
     * @param duration
     * @param alpha
     */
    public static void animationAlpha(@NonNull View view, int duration, float... alpha) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", alpha);
        animator.setDuration(duration);
        animator.start();
    }

    /**
     * 获取View的bitmap
     *
     * @param view
     * @return
     */
    @Nullable
    public static Bitmap getCacheBitmapFromView(@NonNull View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        Bitmap bitmap = null;
        try {
            Bitmap drawingCache = view.getDrawingCache();
            if (drawingCache != null) {
                bitmap = Bitmap.createBitmap(drawingCache);
            }
        } catch (Throwable throwable) {
            // no-op
        }
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    /**
     * 获取View的位置
     *
     * @param view
     * @return
     */
    @Nullable
    public static RectF getViewLocation(@NonNull View view) {
        if (view.getParent() == null) {
            return null;
        }
        int[] location = new int[2];
        view.getLocationInWindow(location);
        int width = view.getWidth();
        int height = view.getHeight();
        return new RectF(location[0], location[1], location[0] + width, location[1] + height);
    }

    /**
     * 判断是否需要设置透明度
     *
     * @param child
     * @return
     */
//    public static boolean canChildAlpha(View child) {
//        return !(child instanceof MediaItemRelativeLayout) && !(child instanceof EmotagPhotoLayout) && (child.getId() != R.id.iv_media_detail_live_cover);
//    }
}

package com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.View;

public class AnimationManager {

    private ValueAnimator mTransScaleAnimator;
    private ValueAnimator mScaleAlphaAnimator;

    public void startShowSmallWindowAnim(View view, int size[], int screenWidth) {
        startSmallWindowShowAnim(view, -(screenWidth / 2 - size[0] / 2), -size[1], 0, 0, 1f, 1f, 300, null);
    }

    public void startShowVideoWindowAnim(View view, int size[], int screenWidth, int screenHeight) {
        startSmallWindowShowAnim(view, screenWidth / 2 - size[0] / 2, screenHeight / 2 - size[1] / 2, 0, 0, 0.4f, 1f,
                300, null);
    }

    public void startWindowToTopbarAnim(View view, int screenWidth, int size[], int topBarHeight,
                                        SimpleAnimatorListener listener) {
        startSmallWindowShowAnim(view, 0, 0, -(screenWidth / 2 - size[0] / 2), -size[1] / 2 - topBarHeight / 2, 1f,
                0.2f, 300, listener);
    }

    public void startTopbarToWindowAnim(View view, int screenWidth, int size[], int topBarHeight) {
        startSmallWindowShowAnim(view, -(screenWidth / 2 - size[0] / 2), -size[1] / 2 - topBarHeight / 2, 0, 0, 0.2f,
                1f, 300, null);
    }

    private void startSmallWindowShowAnim(final View view, final int fromX, final int fromY, final int toX,
                                          final int toY, final float fromScale, final float toScale, int duration,
                                          final SimpleAnimatorListener listener) {
        if (mTransScaleAnimator != null && mTransScaleAnimator.isRunning()) {
            mTransScaleAnimator.cancel();
        }
        final float curTransX = view.getTranslationX() + fromX;
        final float curTransY = view.getTranslationY() + fromY;
        final int disX = toX - fromX;
        final int disY = toY - fromY;

        view.setTranslationX(curTransX);
        view.setTranslationY(curTransY);
        view.setScaleX(fromScale);
        view.setScaleY(fromScale);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 100);
        mTransScaleAnimator = valueAnimator;
        valueAnimator.setDuration(duration);
        valueAnimator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentV = (Integer) animation.getAnimatedValue() / 100f;
                view.setTranslationX(curTransX + disX * currentV);
                view.setTranslationY(curTransY + disY * currentV);
                float scale = fromScale + (toScale - fromScale) * currentV;
                view.setScaleX(scale);
                view.setScaleY(scale);
            }
        });
        valueAnimator.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                if (listener != null) {
                    listener.onAnimationStart(animation);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                if (listener != null) {
                    listener.onAnimationRepeat(animation);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                resetParams(view);
                if (mTransScaleAnimator != null) {
                    mTransScaleAnimator.removeAllListeners();
                }
                mTransScaleAnimator = null;
                if (listener != null) {
                    listener.onAnimationEnd(animation);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                resetParams(view);
                if (listener != null) {
                    listener.onAnimationCancel(animation);
                }
            }
        });
        valueAnimator.start();
    }

    private void resetParams(View view) {
        view.setTranslationX(0);
        view.setTranslationY(0);
        view.setScaleX(1);
        view.setScaleY(1);
        view.setAlpha(1);
    }

    public void startPlayButtonAnim(final View view, long delay) {
        if (mScaleAlphaAnimator != null && mScaleAlphaAnimator.isRunning()) {
            mScaleAlphaAnimator.cancel();
        }
        view.setAlpha(0);
        // 缓动效果
        ValueAnimator mAnimator = ValueAnimator.ofFloat(0f, 0.5f, 0.75f, 1.05f, 1f);
        mScaleAlphaAnimator = mAnimator;
        mAnimator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (null != view) {
                    float animFraction = ((Float) animation.getAnimatedValue()).floatValue();
                    view.setScaleX(animFraction);
                    view.setScaleY(animFraction);
                    float alpha = animFraction;
                    view.setAlpha(alpha);
                }
            }
        });

        mAnimator.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                resetParams(view);
                if (mScaleAlphaAnimator != null) {
                    mScaleAlphaAnimator.removeAllListeners();
                }
                mScaleAlphaAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                resetParams(view);
            }
        });

        mAnimator.setStartDelay(delay);
        mAnimator.setDuration(500);
        mAnimator.start();
    }

    public static class SimpleAnimatorListener implements AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }

    }
}

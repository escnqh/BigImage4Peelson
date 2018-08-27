package com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.effect

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.graphics.RectF
import android.view.View
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.*

/**
 *
 * @author nqh 2018/8/23
 */
class DragToExit(private val mView: View, private val mDragActionListener: DragActionListener?, private val mDirection: Int, private val mBackgroundView: View?) : DragContract.DragCallback {
    companion object {
        private const val ANIMATOR_DURATION = 150
        private const val DEFAULT_CANCLE_TRANSLATION_PERCENT = 0.8f
    }

    private var isDraging: Boolean = false
    private var mMove: Float = 0f
    private var isClose: Boolean = false
    private var mScale: Float = 0.0f


    override fun dragEvent(startX: Float, startY: Float, endX: Float, endY: Float) {
        val distance = mView.height.toFloat()
        mMove = Math.abs(endY - startY)
        mScale = (distance - mMove / 2) / distance
        if (mScale > 1) {
            mScale = 1f
        }
        mView.scaleX = mScale
        mView.scaleY = mScale
        mView.translationX = endX - startX
        mView.translationY = endY - startY

        if (mBackgroundView != null) {
            ViewUtil.setVisible(mBackgroundView)
            val bacgroundAlpha = (distance - Math.abs(mMove) / 3 * 2) / distance
            mBackgroundView.alpha = bacgroundAlpha
        }
    }

    override fun complete(isFastScroll: Boolean) {
        isDraging = false
        if (mScale >= DEFAULT_CANCLE_TRANSLATION_PERCENT) {
            cancel()
        } else {
            close()
        }
    }

    override fun reset() {
        if (isDraging) {
            isDraging = false
            cancel()
        }
    }

    override fun isClose(): Boolean = isClose

    private fun cancel() {
        if (mBackgroundView != null) {
            mBackgroundView.alpha = 1f
        }
        AnimationHelper.animationRestore(mView, ANIMATOR_DURATION, object : AnimationManager.SimpleAnimatorListener() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                if (ContextUtils.isContextValid(mView.context) && mDragActionListener != null) {
                    mDragActionListener.onCancel()
                }
            }
        })
    }

    private fun close() {
        isClose = true
        AnimationHelper.animationTarget(mView, RectF(0f, 0f, 0f, 0f), ANIMATOR_DURATION, ExitAnimatorListenerAdapter())
        AnimationHelper.animationAlpha(mView, ANIMATOR_DURATION, 0f)
    }

    inner class ExitAnimatorListenerAdapter : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            val context: Context = mView.context
            if (ContextUtils.isContextValid(context)) {
                if (context is Activity) {
                    val act: Activity = context
                    if (mDragActionListener != null) {
                        mDragActionListener.onClose(mDirection)
                    } else {
                        act.finish()
                    }
                    act.overridePendingTransition(0, 0)
                }
            }
        }
    }

}
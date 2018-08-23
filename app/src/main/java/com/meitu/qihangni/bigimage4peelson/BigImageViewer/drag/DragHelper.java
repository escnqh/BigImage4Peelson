package com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag;

import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.effect.DragDown;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.effect.DragRightToBack;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.effect.DragRightToTarget;


/**
 * 滑动后退，目前支持下滑和右滑手势
 * 支持下滑缩放后退，右滑缩放后退，右滑后退
 * 支持自定义后退效果
 *
 * @author ljq
 * @since 2018/08/02
 */
public class DragHelper {

    private DragContract.DragProxy mDragProxy;
    private DragLayout mDragLayout;

    private DragHelper(Builder builder) {
        init(builder);
    }

    private void init(final Builder builder) {
        if (ContextUtils.isContextValid(builder.activity)) {
            ViewGroup rootView = builder.activity.findViewById(android.R.id.content);
            if (rootView == null || rootView.getChildCount() == 0) {
                return;
            }
            View backgroundView = createBackgroundView(builder);
            mDragProxy = createDragProxy(builder, rootView, backgroundView);
            new DragLifecycle(builder.activity, mDragProxy);
            mDragLayout = createDragLayout(builder, backgroundView, mDragProxy);
        }
    }

    /**
     * 创建响应触摸事件的layout，在根布局的父布局植入
     *
     * @param builder
     * @param backgroundView
     * @param dragProxy
     */
    private DragLayout createDragLayout(Builder builder, View backgroundView, DragContract.DragProxy dragProxy) {
        DragLayout mDragLayout = new DragLayout(builder.activity);
        if (!builder.mOnlyCreateView){
            mDragLayout.attachToActivity(builder.activity);
        }
        mDragLayout.setCanDrag(builder.mCanDrag);
        mDragLayout.setDragProxy(dragProxy);
        if (backgroundView != null) {
            mDragLayout.setBackgroundView(backgroundView);
        }
        return mDragLayout;
    }

    public DragLayout getDragLayout() {
        return mDragLayout;
    }

    /**
     * 创建手势代理
     *
     * @param builder
     * @param rootView
     * @param backgroundView
     * @return
     */
    @NonNull
    private DragContract.DragProxy createDragProxy(final Builder builder, final ViewGroup rootView, final View backgroundView) {
        DragProxyWrapper dragProxyWrapper = new DragProxyWrapper(true);
        SparseArray<DragContract.DragCallback> dragCallbackMap = builder.mDragCallbackMap;
        for (int i = 0; i < dragCallbackMap.size(); i++) {
            int direction = dragCallbackMap.keyAt(i);
            DragContract.DragCallback dragCallback = dragCallbackMap.get(direction);
            if (dragCallback == null) {
                //添加默认的效果
                switch (direction) {
                    case DragDirection.DOWN:
                        dragCallback = new DragDown(rootView, backgroundView, builder.mDragActionListener);
                        break;
                    case DragDirection.RIGHT:
                        if (builder.mCanDragRightToTarget) {
                            dragCallback = new DragRightToTarget(rootView, backgroundView, builder.mDragActionListener);
                        } else {
                            dragCallback = new DragRightToBack(rootView, builder.mDragActionListener);
                        }
                        break;
                }
            }
            if (dragCallback != null) {
                dragProxyWrapper.addDragDirection(direction, dragCallback);
            }
        }
        if (builder.mValidArea != null) {
            dragProxyWrapper.setValidArea(builder.mValidArea);
        }
        if (builder.mDragIntercept != null) {
            dragProxyWrapper.setDragIntercept(builder.mDragIntercept);
        }
        return dragProxyWrapper;
    }

    /**
     * 创建一个背景View，用于拖拽的时候显示背景渐变效果
     *
     * @param builder
     * @return
     */
    @Nullable
    private View createBackgroundView(Builder builder) {
        View backgroundView = null;
        if (builder.mBackgroundColor > 0) {
            backgroundView = new View(builder.activity);
            backgroundView.setBackgroundResource(builder.mBackgroundColor);
            backgroundView.setVisibility(View.GONE);
        }
        return backgroundView;
    }

    /**
     * 做缩放退出
     *
     * @return
     */
    public boolean closeToTarget() {
        if (mDragProxy != null) {
            SparseArray<DragContract.DragCallback> dragCallbackArray = mDragProxy.getDragCallbackArray();
            for (int i = 0; i < dragCallbackArray.size(); i++) {
                DragContract.DragCallback dragCallback = dragCallbackArray.get(i);
                if (dragCallback != null) {
                    if (dragCallback.isClose()) {
                        return false;
                    }
                    if (dragCallback instanceof DragRightToTarget) {
                        return ((DragRightToTarget) dragCallback).closeToTarget();
                    }
                }
            }
        }
        return false;
    }

    public static class Builder {

        private final FragmentActivity activity;
        private boolean mCanDrag = true;
        private int mBackgroundColor;
        private SparseArray<DragContract.DragCallback> mDragCallbackMap = new SparseArray<>(4);
        private DragContract.DragIntercept mDragIntercept;
        private RectF mValidArea;
        private DragActionListener mDragActionListener;
        private boolean mCanDragRightToTarget;
        private boolean mOnlyCreateView;

        public Builder(@NonNull FragmentActivity activity) {
            this.activity = activity;
        }

        public Builder setCanDrag(boolean canDrag) {
            mCanDrag = canDrag;
            return this;
        }

        /**
         * 设置背景色，用于拖拽的时候显示背景渐变效果
         *
         * @param backgroundColor
         * @return
         */
        public Builder setBackgroundColor(int backgroundColor) {
            mBackgroundColor = backgroundColor;
            return this;
        }

        /**
         * 添加支持手势方向，使用默认效果
         *
         * @param direction
         * @return
         */
        public Builder addDragDirection(@DragDirection.Direction int direction) {
            mDragCallbackMap.put(direction, null);
            return this;
        }

        /**
         * 添加支持手势方向，需自定义效果
         *
         * @param direction
         * @param dragCallback
         * @return
         */
        public Builder addDragDirection(@DragDirection.Direction int direction, @NonNull DragContract.DragCallback dragCallback) {
            mDragCallbackMap.put(direction, dragCallback);
            return this;
        }

        /**
         * 添加拦截器，用于判断当前是否可拖动
         *
         * @param dragIntercept
         * @return
         */
        public Builder setDragIntercept(@NonNull DragContract.DragIntercept dragIntercept) {
            mDragIntercept = dragIntercept;
            return this;
        }

        /**
         * 添加拖动状态回调，若添加了回调，需要自己处理activity finish
         *
         * @param dragActionListener
         * @return
         */
        public Builder setDragActionListener(@NonNull DragActionListener dragActionListener) {
            mDragActionListener = dragActionListener;
            return this;
        }

        /**
         * 是否支持右滑返回缩放效果
         *
         * @param canDragRightToTarget
         * @return
         */
        public Builder setCanDragRightToTarget(boolean canDragRightToTarget) {
            mCanDragRightToTarget = canDragRightToTarget;
            return this;
        }

        public Builder onlyCreateView(){
            mOnlyCreateView = true;
            return this;
        }

        /**
         * 设置手势有效区域，默认最大
         *
         * @param validArea
         * @return
         */
        public Builder setValidArea(@NonNull RectF validArea) {
            mValidArea = validArea;
            return this;
        }

        public DragHelper build() {
            return new DragHelper(this);
        }

    }

}

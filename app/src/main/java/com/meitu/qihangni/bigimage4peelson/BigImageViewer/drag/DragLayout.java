package com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


/**
 * @author ljq
 * @since 2018/07/31
 */
public class DragLayout extends FrameLayout {

    private DragContract.DragProxy mDragProxyEvent;
    private boolean mCanDrag;
    private ViewGroup mContentView;

    public DragLayout(@NonNull Context context) {
        this(context, null);
    }

    public DragLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCanDrag(boolean canDrag) {
        mCanDrag = canDrag;
    }

    public void setDragProxy(DragContract.DragProxy dragProxyEvent) {
        mDragProxyEvent = dragProxyEvent;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return (mCanDrag && mDragProxyEvent != null && mDragProxyEvent.drag(ev)) || super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mCanDrag && mDragProxyEvent != null) {
            mDragProxyEvent.drag(ev);
        }
        return true;
    }

    public void attachToActivity(@NonNull Activity activity) {
        mCanDrag = true;
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        View decorChild = decorView.findViewById(android.R.id.content);
        while (decorChild.getParent() != decorView) {
            decorChild = (View) decorChild.getParent();
        }
        mContentView = (ViewGroup) decorChild;
        decorView.removeView(decorChild);
        addView(decorChild);
        decorView.addView(this);
    }

    public void setBackgroundView(@NonNull View backgroundView) {
        addView(backgroundView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }


    public void detachFromActivity(@NonNull Activity activity) {
        mCanDrag = false;
        ViewGroup decorChild = (ViewGroup) getChildAt(0);
        ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
        decor.removeView(this);
        removeView(decorChild);
        decor.addView(decorChild);
    }
}

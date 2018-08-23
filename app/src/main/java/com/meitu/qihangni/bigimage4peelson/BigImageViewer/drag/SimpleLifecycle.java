package com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.CallSuper;

/**
 * 基于arch.lifecycle机制的简单的生命周期实现, 提供绑定的方法和生命周期回调
 *
 * @author ccs 2018.02.11
 */
public abstract class SimpleLifecycle implements BaseLifecycle {

    private LifecycleOwner mLifecycleOwner;

    /**
     * 绑定生命周期, 不需要主动调用removeObserver, 除非不希望再监听了
     */
    protected SimpleLifecycle(LifecycleOwner owner) {
        mLifecycleOwner = owner;
        owner.getLifecycle().addObserver(this);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @CallSuper
    @Override
    public void onDestroy() {
        if (null != mLifecycleOwner) {
            mLifecycleOwner.getLifecycle().removeObserver(this);
            mLifecycleOwner = null;
        }
    }

    @Override
    public void onAny(LifecycleOwner owner, Lifecycle.Event event) {

    }
}

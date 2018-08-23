package com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag;

import android.arch.lifecycle.LifecycleOwner;


public class DragLifecycle extends SimpleLifecycle{
    private DragContract.DragProxy mDragProxy;

    /**
     * 绑定生命周期, 不需要主动调用removeObserver, 除非不希望再监听了
     *
     * @param owner
     */
    protected DragLifecycle(LifecycleOwner owner, DragContract.DragProxy dragProxy) {
        super(owner);
        mDragProxy = dragProxy;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDragProxy != null){
            mDragProxy.reset();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DragTargetViewCache.clearCache();
    }
}

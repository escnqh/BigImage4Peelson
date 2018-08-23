package com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag;

import android.support.annotation.Nullable;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * 详情页对应Feed页Item的View缓存
 * Created by ljq on 2018/6/15.
 */
public class DragTargetViewCache {

    private static WeakReference<View> mCacheTargetViewWeakRef;
    //    private static WeakReference<RecyclerListView> mCacheRecyclerViewWeakRef;
    private static int mCacheTargetPosition = -1;

    public static void cacheTargetView(@Nullable View view) {
        clearCache();
        if (view != null) {
            mCacheTargetViewWeakRef = new WeakReference<>(view);
        }
    }

//    public static void cacheTargetPosition(@Nullable RecyclerListView recyclerView, int position) {
//        clearCache();
//        if (recyclerView != null && position >= 0) {
//            mCacheRecyclerViewWeakRef = new WeakReference<>(recyclerView);
//            mCacheTargetPosition = position;
//        }
//    }

    @Nullable
    public static View getCacheTargetView() {
        if (mCacheTargetViewWeakRef != null) {
            return mCacheTargetViewWeakRef.get();
        }
//        if (mCacheRecyclerViewWeakRef != null) {
//            RecyclerListView recyclerListView = mCacheRecyclerViewWeakRef.get();
//            if (recyclerListView != null) {
//                return recyclerListView.getLayoutManager().findViewByPosition(mCacheTargetPosition);
//            }
//        }
        return null;
    }

    public static void clearCache() {
        mCacheTargetViewWeakRef = null;
//        mCacheRecyclerViewWeakRef = null;
        mCacheTargetPosition = -1;
    }
}

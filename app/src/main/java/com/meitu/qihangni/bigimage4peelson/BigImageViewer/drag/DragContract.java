package com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag;

import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.MotionEvent;

/**
 * @author Ljq 2018/4/19
 */
public interface DragContract {

    interface DragProxy {
        boolean drag(MotionEvent ev);

        void reset();

        SparseArray<DragCallback> getDragCallbackArray();
    }

    interface DragCallback {

        void dragEvent(float startX, float startY, float endX, float endY);

        void complete(boolean isFastScroll);

        void reset();

        boolean isClose();
    }

    interface DragIntercept {

        boolean canDrag(@NonNull MotionEvent ev, @DragDirection.Direction int direction);

    }
}

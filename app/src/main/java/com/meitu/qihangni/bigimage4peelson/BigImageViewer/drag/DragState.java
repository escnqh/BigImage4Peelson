package com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag;

import android.support.annotation.IntDef;

/**
 * @author Ljq 2018/4/19
 */
public class DragState {

    /**
     * view正在触摸但未达到触发是否拖拽的状态
     */
    public static final int SETTLING = 1;

    /**
     * 正在拖拽
     */
    public static final int DARGING = 2;

    /**
     * view未触摸或触发拖拽失败的状态
     */
    public static final int IDEL = 3;


    @IntDef({SETTLING, DARGING, IDEL})
    public @interface State {
    }
}

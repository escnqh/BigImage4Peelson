package com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag;

import android.support.annotation.IntDef;

/**
 * 拖拽的方向
 * Created by ljq on 2018/6/16.
 */
public class DragDirection {
    /**
     * 向下拖动
     */
    public static final int DOWN = 0;

    /**
     * 向右拖动
     */
    public static final int RIGHT = 1;

    /**
     * 向上拖动
     */
    public static final int UP = 3;

    /**
     * 向左拖动
     */
    public static final int LEFT = 4;


    @IntDef({DOWN, RIGHT, UP, LEFT})
    public @interface Direction {
    }
}

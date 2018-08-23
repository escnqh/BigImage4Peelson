package com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag;

/**
 * Created by ljq on 2018/6/19.
 */
public interface DragActionListener {

    void onStart();

    void onCancel();

    void onClose(@DragDirection.Direction int direction);
}

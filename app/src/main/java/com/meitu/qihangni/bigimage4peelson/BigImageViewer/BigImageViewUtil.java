package com.meitu.qihangni.bigimage4peelson.BigImageViewer;

import android.content.Context;

import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.ImagePreview;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.bean.ImageInfo;

import java.util.List;

/**
 * @author nqh 2018/8/22
 */
public class BigImageViewUtil {
    public static void showSingleImage() {

    }

    public static void showAlbum(Context context, List<ImageInfo> imageInfoList) {
        ImagePreview.getInstance()
                .setContext(context)
                .setClickToExit(false)
                .setDragable(false)
                .setShowDownButton(false)
                .setImageInfoList(imageInfoList)
                .setLoadStrategy(ImagePreview.LoadStrategy.AlwaysOrigin)
                .setFolderName("BigImageViewDownload")
                .setScaleLevel(0.5f, 1f, 1.5f)
                .setZoomTransitionDuration(300)
                .start();
    }
}

package com.meitu.qihangni.bigimage4peelson.BigImageViewer;

import android.content.Context;

import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.ImagePreview;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.bean.ImageInfo;
import com.meitu.qihangni.bigimage4peelson.R;

import java.util.List;

/**
 * @author nqh 2018/8/22
 */
public class BigImageViewUtil {
    public static void showSingleImage(Context context, String imageUrl, int locationX, int locationY, int resourceHeight, int resourceWidth) {

    }

    public static void showAlbum(Context context, List<ImageInfo> imageInfoList) {
        ImagePreview.getInstance()
                .setContext(context)
                .setClickToExit(false)
                .setDragable(false)
                .setShowDownButton(false)
                .setImageInfoList(imageInfoList)
                .setLoadStrategy(ImagePreview.LoadStrategy.AlwaysOrigin)
                .setBackgroundColor(R.color.whitebackground)
                .setFolderName("BigImageViewDownload")
                .setScaleLevel(1f, 3f, 5f)
                .setZoomTransitionDuration(300)
                .start();
    }
}

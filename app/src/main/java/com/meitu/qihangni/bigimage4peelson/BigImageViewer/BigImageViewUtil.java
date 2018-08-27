package com.meitu.qihangni.bigimage4peelson.BigImageViewer;

import android.content.Context;

import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.ImagePreview;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.bean.ImageInfo;
import com.meitu.qihangni.bigimage4peelson.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nqh 2018/8/22
 */
public class BigImageViewUtil {

    /**
     * 展示一张单图，包含拖拽点击退出等效果
     *
     * @param context        上下文
     * @param imageUrl       显示的单图Url
     * @param locationX      源x位置
     * @param locationY      源y位置
     * @param resourceHeight 源高度
     * @param resourceWidth  源宽度
     */
    public static void showSingleImage(Context context, String imageUrl, int locationX, int locationY, int resourceHeight, int resourceWidth) {
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setOriginUrl(imageUrl);
        imageInfo.setThumbnailUrl(
                imageUrl.concat("-1200"));
        List<ImageInfo> imageInfoList = new ArrayList<>();
        imageInfoList.add(imageInfo);
        ImagePreview.getInstance()
                .setContext(context)
                .setClickToExit(true)
                .setDragable(true)
                .setShowDownButton(false)
                .setImageInfoList(imageInfoList)
                .setResourceLocation(locationX, locationY)
                .setResourceSize(resourceHeight, resourceWidth)
                .setLoadStrategy(ImagePreview.LoadStrategy.AlwaysOrigin)
                .setBackgroundColor(R.color.blackbackground)
                .setFolderName("BigImageViewDownload")
                .setScaleLevel(1f, 3f, 5f)
                .setZoomTransitionDuration(300)
                .start();
    }

    /**
     * 展示一系列图片
     *
     * @param context 上下文
     * @param images  源列表
     */
    public static void showAlbum(Context context, List<String> images) {
        ImageInfo imageInfo;
        final List<ImageInfo> imageInfoList = new ArrayList<>();
        for (String image : images) {
            imageInfo = new ImageInfo();
            imageInfo.setOriginUrl(image);// 原图
            imageInfo.setThumbnailUrl(
                    image.concat("-1200"));// 缩略图，实际使用中，根据需求传入缩略图路径。如果没有缩略图url，可以将两项设置为一样，并隐藏查看原图按钮即可。
            imageInfoList.add(imageInfo);
            imageInfo = null;
        }
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

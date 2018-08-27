package com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.bean.ImageInfo;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.view.ImagePreviewActivity;
import com.meitu.qihangni.bigimage4peelson.R;

import java.util.List;


/**
 */
public class ImagePreview {

    public static final int MODE_SCALE_TO_MEDIUM_TO_MAX_TO_MIN = 1001;// 三级放大
    public static final int MODE_SCALE_TO_MAX_TO_MIN = 1002;// 二级放大，最大与最小
    public static final int MODE_SCALE_TO_MEDIUM_TO_MIN = 1003;// 二级放大，中等与最小

    private Context context;
    private List<ImageInfo> imageInfoList;// 图片数据集合
    private int index = 0;// 默认显示第几个
    private String folderName = "ImagePreview";// 下载到的文件夹名（根目录中）
    private float minScale = 1f;// 最小缩放倍数
    private float mediumScale = 2.0f;// 中等缩放倍数
    private float maxScale = 5.0f;// 最大缩放倍数;

    private boolean isShowDownButton = true;// 是否显示下载按钮
    private int zoomTransitionDuration = 200;// 动画持续时间 单位毫秒 ms
    private boolean isDragable = false;
    private boolean isClickToExit = false;
    private int backgroundcolor = R.color.blackbackground;
    private int locationX = 0;
    private int locationY = 0;
    private int resourceHeight = 0;
    private int resourceWidth = 0;

    private LoadStrategy loadStrategy = LoadStrategy.Default;// 加载策略

    public enum LoadStrategy {
        /**
         * 仅加载原图；会强制隐藏查看原图按钮
         */
        AlwaysOrigin,

        /**
         * 仅加载普清；会强制隐藏查看原图按钮
         */
        AlwaysThumb,

        /**
         * 根据网络自适应加载，WiFi原图，流量普清；会强制隐藏查看原图按钮
         */
        NetworkAuto,

        /**
         * 手动模式：默认普清，点击按钮再加载原图；会根据原图、缩略图url是否一样来判断是否显示查看原图按钮
         */
        Default
    }

    public static ImagePreview getInstance() {
        return InnerClass.instance;
    }

    public ImagePreview setContext(@NonNull Context context) {
        this.context = context;
        return this;
    }

    public ImagePreview setResourceSize(int resourceHeight, int resourceWidth) {
        this.resourceHeight = resourceHeight;
        this.resourceWidth = resourceWidth;
        return this;
    }

    public ImagePreview setResourceLocation(int locationX, int locationY) {
        this.locationX = locationX;
        this.locationY = locationY;
        return this;
    }

    public int getLocationX() {
        return locationX;
    }

    public int getLocationY() {
        return locationY;
    }

    public int getResourceHeight() {
        return resourceHeight;
    }

    public int getResourceWidth() {
        return resourceWidth;
    }

    public List<ImageInfo> getImageInfoList() {
        return imageInfoList;
    }

    public ImagePreview setImageInfoList(@NonNull List<ImageInfo> imageInfoList) {
        this.imageInfoList = imageInfoList;
        return this;
    }

    public ImagePreview setBackgroundColor(int backgroundcolor) {
        this.backgroundcolor = backgroundcolor;
        return this;
    }

    public int getBackgroundcolor() {
        return backgroundcolor;
    }

    public int getIndex() {
        return index;
    }

    public ImagePreview setIndex(int index) {
        this.index = index;
        return this;
    }

    public boolean isShowDownButton() {
        return isShowDownButton;
    }

    public ImagePreview setShowDownButton(boolean showDownButton) {
        isShowDownButton = showDownButton;
        return this;
    }

    public boolean isShowOriginButton(int index) {
        // 根据不同加载策略，自行判断是否显示查看原图按钮
        String originUrl = getImageInfoList().get(index).getOriginUrl();
        String thumbUrl = getImageInfoList().get(index).getThumbnailUrl();
        if (originUrl.equalsIgnoreCase(thumbUrl)) {// 原图、缩略图url一样，不显示查看原图按钮
            return false;
        }
        if (loadStrategy == LoadStrategy.Default) {
            return true;// 手动模式时，根据是否有原图缓存来决定是否显示查看原图按钮
        } else if (loadStrategy == LoadStrategy.NetworkAuto) {
            return false;// 强制隐藏查看原图按钮
        } else if (loadStrategy == LoadStrategy.AlwaysThumb) {
            return false;// 强制隐藏查看原图按钮
        } else if (loadStrategy == LoadStrategy.AlwaysOrigin) {
            return false;// 强制隐藏查看原图按钮
        } else {
            return false;
        }
    }

    public String getFolderName() {
        if (TextUtils.isEmpty(folderName)) {
            folderName = "BigImageViewDownload";
        }
        return folderName;
    }

    public ImagePreview setFolderName(@NonNull String folderName) {
        this.folderName = folderName;
        return this;
    }

    public ImagePreview setScaleLevel(float min, float medium, float max) {
        if (max > medium && medium > min && min > 0) {
            this.minScale = min;
            this.mediumScale = medium;
            this.maxScale = max;
        } else {
            throw new IllegalArgumentException("max must greater to medium, medium must greater to min!");
        }
        return this;
    }

    public float getMinScale() {
        return minScale;
    }

    public float getMediumScale() {
        return mediumScale;
    }

    public float getMaxScale() {
        return maxScale;
    }

    public int getZoomTransitionDuration() {
        return zoomTransitionDuration;
    }

    public ImagePreview setZoomTransitionDuration(int zoomTransitionDuration) {
        if (zoomTransitionDuration < 0) {
            throw new IllegalArgumentException("zoomTransitionDuration must greater 0");
        }
        this.zoomTransitionDuration = zoomTransitionDuration;
        return this;
    }

    public ImagePreview setLoadStrategy(LoadStrategy loadStrategy) {
        this.loadStrategy = loadStrategy;
        return this;
    }

    public LoadStrategy getLoadStrategy() {
        return loadStrategy;
    }

    public ImagePreview setDragable(boolean isDragable) {
        this.isDragable = isDragable;
        return this;
    }

    public boolean getDragable() {
        return isDragable;
    }

    public ImagePreview setClickToExit(boolean isClickToExit) {
        this.isClickToExit = isClickToExit;
        return this;
    }

    public boolean getClickToExit() {
        return isClickToExit;
    }

    public void reset() {
        imageInfoList = null;
        index = 0;
        isShowDownButton = true;
        loadStrategy = LoadStrategy.Default;
        folderName = "ImagePreview";
        context = null;
    }

    public void start() {
        if (context == null) {
            throw new IllegalArgumentException("You must call 'setContext(Context context)' first!");
        }
        if (imageInfoList == null || imageInfoList.size() == 0) {
            throw new IllegalArgumentException(
                    "Do you forget to call 'setImageInfoList(List<ImageInfo> imageInfoList)' ?");
        }
        if (this.index >= imageInfoList.size()) {
            throw new IllegalArgumentException("index out of range!");
        }
        ImagePreviewActivity.activityStart(context);
    }

    private static class InnerClass {
        private static ImagePreview instance = new ImagePreview();
    }
}
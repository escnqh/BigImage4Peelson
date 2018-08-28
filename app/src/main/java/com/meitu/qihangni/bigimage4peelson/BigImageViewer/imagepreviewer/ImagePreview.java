package com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer;

import android.support.annotation.NonNull;

import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.bean.ImageInfo;
import com.meitu.qihangni.bigimage4peelson.R;

import java.io.Serializable;
import java.util.List;

public class ImagePreview implements Serializable {
    private static final long serialVersionUID = -5725819091270571846L;
    private List<ImageInfo> imageInfoList;// 图片数据集合
    private int index = 0;// 默认显示第几个
    private float minScale = 1f;// 最小缩放倍数
    private float mediumScale = 2.0f;// 中等缩放倍数
    private float maxScale = 5.0f;// 最大缩放倍数;
    private int zoomTransitionDuration = 200;// 动画持续时间 单位毫秒 ms
    private boolean isDragable = false;
    private boolean isClickToExit = false;
    private int backgroundcolor = R.color.blackbackground;
    private int locationX = 0;
    private int locationY = 0;
    private int resourceHeight = 0;
    private int resourceWidth = 0;

    ImagePreview(Builder builder) {
        this.imageInfoList = builder.imageInfoList;
        this.index = builder.index;
        this.minScale = builder.minScale;
        this.mediumScale = builder.mediumScale;
        this.maxScale = builder.maxScale;
        this.zoomTransitionDuration = builder.zoomTransitionDuration;
        this.isClickToExit = builder.isClickToExit;
        this.isDragable = builder.isDragable;
        this.backgroundcolor = builder.backgroundcolor;
        this.locationX = builder.locationX;
        this.locationY = builder.locationY;
        this.resourceWidth = builder.resourceWidth;
        this.resourceHeight = builder.resourceHeight;
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

    public int getBackgroundcolor() {
        return backgroundcolor;
    }

    public int getIndex() {
        return index;
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

    public boolean getDragable() {
        return isDragable;
    }

    public boolean getClickToExit() {
        return isClickToExit;
    }

    public static class Builder implements Serializable {
        private static final long serialVersionUID = 8438483406435763499L;
        private List<ImageInfo> imageInfoList;// 图片数据集合
        private int index = 0;// 默认显示第几个
        private float minScale = 1f;// 最小缩放倍数
        private float mediumScale = 2.0f;// 中等缩放倍数
        private float maxScale = 5.0f;// 最大缩放倍数;
        private int zoomTransitionDuration = 200;// 动画持续时间 单位毫秒 ms
        private boolean isDragable = false;
        private boolean isClickToExit = false;
        private int backgroundcolor = R.color.blackbackground;
        private int locationX = 0;
        private int locationY = 0;
        private int resourceHeight = 0;
        private int resourceWidth = 0;

        public Builder setClickToExit(boolean isClickToExit) {
            this.isClickToExit = isClickToExit;
            return this;
        }

        public Builder setDragable(boolean isDragable) {
            this.isDragable = isDragable;
            return this;
        }

        public Builder setZoomTransitionDuration(int zoomTransitionDuration) {
            if (zoomTransitionDuration < 0) {
                throw new IllegalArgumentException("zoomTransitionDuration must greater 0");
            }
            this.zoomTransitionDuration = zoomTransitionDuration;
            return this;
        }

        public Builder setIndex(int index) {
            this.index = index;
            return this;
        }

        public Builder setScaleLevel(float min, float medium, float max) {
            if (max > medium && medium > min && min > 0) {
                this.minScale = min;
                this.mediumScale = medium;
                this.maxScale = max;
            } else {
                throw new IllegalArgumentException("max must greater to medium, medium must greater to min!");
            }
            return this;
        }

        public Builder setImageInfoList(@NonNull List<ImageInfo> imageInfoList) {
            this.imageInfoList = imageInfoList;
            return this;
        }

        public Builder setBackgroundColor(int backgroundcolor) {
            this.backgroundcolor = backgroundcolor;
            return this;
        }

        public Builder setResourceSize(int resourceHeight, int resourceWidth) {
            this.resourceHeight = resourceHeight;
            this.resourceWidth = resourceWidth;
            return this;
        }

        public Builder setResourceLocation(int locationX, int locationY) {
            this.locationX = locationX;
            this.locationY = locationY;
            return this;
        }

        public ImagePreview build() {
            if (imageInfoList == null || imageInfoList.size() == 0) {
                throw new RuntimeException(
                        "Do you forget to call 'setImageInfoList(List<ImageInfo> imageInfoList)' ?");
            }
            if (index >= imageInfoList.size()) {
                throw new RuntimeException("index out of range!");
            }
            return new ImagePreview(this);
        }
    }
}
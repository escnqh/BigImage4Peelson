package com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.tool;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;

/**
 * 图片下载工具类
 */
public class DownloadPictureUtil {

    public static void downloadPicture(final Context context, final String url, final String path,
                                       final String name) {
        ToastUtil.getInstance()._short(context, "开始下载...");

        SimpleTarget<File> target = new SimpleTarget<File>() {

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                ToastUtil.getInstance()._short(context, "保存失败");
            }

            @Override
            public void onResourceReady(@NonNull File resource,
                                        @Nullable Transition<? super File> transition) {
                boolean result = FileUtil.copyFile(resource, path, name);
                if (result) {
                    ToastUtil.getInstance()._short(context, "成功保存到 ".concat(path).concat(name));
                    new SingleMediaScanner(context, path, new SingleMediaScanner.ScanListener() {
                        @Override
                        public void onScanFinish() {
                            // scanning...
                        }
                    });
                } else {
                    ToastUtil.getInstance()._short(context, "保存失败");
                }
            }
        };
        Glide.with(context).downloadOnly().load(url).into(target);
    }
}
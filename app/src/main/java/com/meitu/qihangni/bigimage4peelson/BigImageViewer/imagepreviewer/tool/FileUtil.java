package com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.tool;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * 文件工具类
 */
public class FileUtil {
    /**
     * 根据文件路径拷贝文件
     *
     * @param resourceFile 源文件
     * @param targetPath   目标路径（包含文件名和文件格式）
     * @return boolean 成功true、失败false
     */
    public static boolean copyFile(File resourceFile, String targetPath, String fileName) {
        boolean result = false;
        if (resourceFile == null || TextUtils.isEmpty(targetPath)) {
            return result;
        }
        File target = new File(targetPath);
        if (target.exists()) {
            target.delete(); // 已存在的话先删除
        } else {
            try {
                target.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File targetFile = new File(targetPath.concat(fileName));
        if (targetFile.exists()) {
            targetFile.delete();
        } else {
            try {
                targetFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileChannel resourceChannel = null;
        FileChannel targetChannel = null;
        try {
            resourceChannel = new FileInputStream(resourceFile).getChannel();
            targetChannel = new FileOutputStream(targetFile).getChannel();
            resourceChannel.transferTo(0, resourceChannel.size(), targetChannel);
            result = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }
        try {
            resourceChannel.close();
            targetChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
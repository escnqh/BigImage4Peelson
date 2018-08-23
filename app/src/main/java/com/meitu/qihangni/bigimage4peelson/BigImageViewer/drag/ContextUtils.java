package com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

/**
 * 上下文的一些处理工具
 *
 * @author lidiqing
 * @since 2017/5/17.
 */

public class ContextUtils {

    public static boolean isContextValid(Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                return !activity.isFinishing() && !activity.isDestroyed();
            } else {
                return !activity.isFinishing();
            }
        }
        return true;
    }
}

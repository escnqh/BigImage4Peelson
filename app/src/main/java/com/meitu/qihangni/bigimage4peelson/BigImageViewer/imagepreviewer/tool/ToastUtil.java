package com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.tool;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * Toast工具类
 */
public class ToastUtil {

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    private Toast toast;

    public ToastUtil() {

    }

    public static ToastUtil getInstance() {
        return InnerClass.instance;
    }

    public void _short(final Context context, final String text) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    toast.setText(text);
                    toast.show();
                }
            }
        });
    }

    public void _long(final Context context, final String text) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    toast.setText(text);
                    toast.show();
                }
            }
        });
    }

    private static class InnerClass {
        private static ToastUtil instance = new ToastUtil();
    }
}
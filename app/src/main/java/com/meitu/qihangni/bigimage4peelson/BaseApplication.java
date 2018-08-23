package com.meitu.qihangni.bigimage4peelson;

import android.app.Application;

/**
 * @author nqh 2018/8/20
 */
public class BaseApplication extends Application {

    static Application app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static Application getApplication() {
        return app;
    }
}

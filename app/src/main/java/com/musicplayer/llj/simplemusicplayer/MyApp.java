package com.musicplayer.llj.simplemusicplayer;

import android.app.Application;
import android.content.Intent;
import com.musicplayer.llj.simplemusicplayer.service.PlayService;
import com.musicplayer.llj.simplemusicplayer.util.Preferences;

public class MyApp extends Application {
    private static MyApp mMyApp;

    public static MyApp getMyApp() {
        return mMyApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMyApp = this;

        Preferences.init(this);

        //启动播放服务
        Intent intent = new Intent(this, PlayService.class);
        startService(intent);
    }
}

package com.nativeenglish.tool;

import android.app.Application;

import java.time.Duration;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //For bmob
        Bmob.initialize(this, "399a5f39bd4fd1092d8676ea54bb6c5a");

        ContentParser.parseContent();
    }


    public static long getDuration(String time) {
        Duration duration = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            duration = Duration.parse(time);
            if(duration != null){
                return duration.getSeconds();
            }
        }
        else {
            //todo api < 26 duration
        }

        return 0;
    }
}

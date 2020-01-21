package cn.bmob.sms;

import android.app.Application;

import cn.bmob.v3.Bmob;

/**
 * Created on 18/9/25 10:42
 *
 * @author zhangchaozhou
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        Bmob.resetDomain("http://open-vip.bmob.cn/8/");
//        Bmob.initialize(this,"12784168944a56ae41c4575686b7b332");
        Bmob.initialize(this,"ba096f73f0149e0ed309f0f2cbb62017");
    }
}

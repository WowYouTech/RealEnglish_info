/**
 * @(#)DemoApplication.java, 2015锟�4锟�3锟�. Copyright 2012 Yodao, Inc. All rights
 *                           reserved. YODAO PROPRIETARY/CONFIDENTIAL. Use is
 *                           subject to license terms.
 */
package com.youdao.sdk.ocrdemo;

import com.youdao.sdk.app.YouDaoApplication;

import android.app.Application;

/**
 * @author lukun
 */
public class DemoApplication extends Application {

    private static DemoApplication swYouAppction;

    @Override
    public void onCreate() {
        super.onCreate();
        YouDaoApplication.init(this,"请输入您的appid");//创建应用，每个应用都会有一个Appid，绑定对应的翻译服务实例，即可使用
        swYouAppction = this;
    }

    public static DemoApplication getInstance() {
        return swYouAppction;
    }

}

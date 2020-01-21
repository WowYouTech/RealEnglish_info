package com.youdao.sdk.ydtranslatedemo;

/**
 * @(#)ListenForwardHelper.java, 2015�?4�?4�?. Copyright 2012 Yodao, Inc. All
 * rights reserved. YODAO
 * PROPRIETARY/CONFIDENTIAL. Use is subject to
 * license terms.
 */

import android.app.Activity;
import android.content.Intent;

import com.youdao.sdk.ydtranslatedemo.utils.ToastUtils;

import static android.Manifest.permission.RECORD_AUDIO;

/**
 * @author lukun 跳转
 */
public class TranslateForwardHelper {

    public static void toTranslateActivity(Activity activity) {
        Intent in = new Intent(activity, TranslateActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivityForResult(in, 0);
        activity.overridePendingTransition(R.anim.listen_activity_open_in_anim,
                R.anim.listen_activity_open_out_anim);

    }

    public static void toHanyuActivity(Activity activity) {
        Intent in = new Intent(activity, HanyuActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivityForResult(in, 0);
        activity.overridePendingTransition(R.anim.listen_activity_open_in_anim,
                R.anim.listen_activity_open_out_anim);

    }


    public static void toTranslateOfflineActivity(Activity activity) {
        Intent in = new Intent(activity, TranslateOfflineActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivityForResult(in, 0);
        activity.overridePendingTransition(R.anim.listen_activity_open_in_anim,
                R.anim.listen_activity_open_out_anim);

    }

    public static void toSpeachTranslateOfflineActivity(Activity activity) {
        Intent in = new Intent(activity, SpeachTranslateDemoActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivityForResult(in, 0);
        activity.overridePendingTransition(R.anim.listen_activity_open_in_anim,
                R.anim.listen_activity_open_out_anim);

    }

    public static void toOCRTranslateActivity(Activity activity) {
        Intent in = new Intent(activity, OcrTranslateActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivityForResult(in, 0);
        activity.overridePendingTransition(R.anim.listen_activity_open_in_anim,
                R.anim.listen_activity_open_out_anim);

    }

    public static void toBrowser(Activity activity, String url) {
        Intent in = new Intent(activity, MyBrowser.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        in.putExtra("deeplink", url);
        activity.startActivityForResult(in, 0);
        activity.overridePendingTransition(R.anim.listen_activity_open_in_anim,
                R.anim.listen_activity_open_out_anim);

    }


}

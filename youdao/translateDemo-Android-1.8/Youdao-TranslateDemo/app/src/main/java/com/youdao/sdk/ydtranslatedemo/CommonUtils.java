package com.youdao.sdk.ydtranslatedemo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class CommonUtils {
    private static long sLastClickTime;
    private static final int INTERVAL = 500;//500毫秒

    public static boolean isFastDoubleClick() {
        if (System.currentTimeMillis() - sLastClickTime < INTERVAL) {
            return true;
        } else {
            sLastClickTime = System.currentTimeMillis();
            return false;
        }
    }

    public static boolean isPermissionGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public static boolean requestPermission(Activity activity, String[] permissions, int requestCode) {
        boolean hasPermission = true;
        for (String permission : permissions) {
            if (!CommonUtils.isPermissionGranted(activity, permission)) {
                hasPermission = false;
                break;
            }
        }
        if (!hasPermission) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
        return hasPermission;
    }
}

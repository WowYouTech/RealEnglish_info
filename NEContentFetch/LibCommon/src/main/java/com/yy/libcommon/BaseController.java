package com.yy.libcommon;

import androidx.fragment.app.FragmentManager;

/**
 * Created by steveyang on 30/11/16.
 */

public interface BaseController {

    FragmentManager getCurrentFragmentManager();
    boolean isDestroyed();

    SubFragPermission getSubFragPermission();

//    void showProgress(String title,String message);
//    void updateProgress(String message);
//    void hideProgress();
//    void showToast(String msg);
//    boolean checkCameraPermissionWithRun(Runnable runnable);
//    boolean checkFilePermissionWithRun(Runnable runnable);
//    void requestCameraPermission();
//    void requestFilePermission();


}

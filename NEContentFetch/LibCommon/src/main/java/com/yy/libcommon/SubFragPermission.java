package com.yy.libcommon;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


/**
 * ==================================
 * Created by michael.carr on 18/12/2014.
 * ==================================
 */
public class SubFragPermission extends BaseSubFragment {

    final String FILE_EDIT_NOTIFY = "com.fleetiq.fleetiq360.file";
    final String CAMERA_NOTIFY = "com.fleetiq.fleetiq360.file";
    
    public Runnable cameraPendingRunnable;
    public Runnable filePendingRunnable;


    public SubFragPermission(BaseActivity baseActivity, BaseController baseController) {
        super(baseController,baseActivity);
    }


    public void onCameraFeatureEnabled() {

        if (cameraPendingRunnable != null) {
            cameraPendingRunnable.run();
        }

        if(mBaseController instanceof BaseActivity){
            Intent intent = new Intent(CAMERA_NOTIFY);
            intent.putExtra("enabled", true);
            LocalBroadcastManager.getInstance(mBaseActivity).sendBroadcast(intent);
        }
    }

    public void onCameraFeatureDisabled() {
        mBaseActivity.showToast("No permission to access camera !");

        if(mBaseController instanceof BaseActivity){
            Intent intent = new Intent(CAMERA_NOTIFY);
            intent.putExtra("enabled", false);
            LocalBroadcastManager.getInstance(mBaseActivity).sendBroadcast(intent);
        }
    }


    public void onFileFeatureEnabled(){
        if (filePendingRunnable != null) {

            filePendingRunnable.run();
        }

        if(mBaseController instanceof BaseActivity){
            Intent intent = new Intent(FILE_EDIT_NOTIFY);
            intent.putExtra("enabled", true);
            LocalBroadcastManager.getInstance(mBaseActivity).sendBroadcast(intent);
        }

    }
    public void onFileFeatureDisabled(){

        mBaseActivity.showToast("No permission to access storage !");

        if(mBaseController instanceof BaseActivity){
            Intent intent = new Intent(FILE_EDIT_NOTIFY);
            intent.putExtra("enabled", false);
            LocalBroadcastManager.getInstance(mBaseActivity).sendBroadcast(intent);
        }

    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(mBaseController instanceof BaseActivity){
                return;
            }

            if (intent.getAction().equals(CAMERA_NOTIFY)) {
                if (intent.hasExtra("enabled") && intent.getBooleanExtra("enabled",false)) {
                    onCameraFeatureEnabled();
                } else {
                    onCameraFeatureDisabled();
                }
            }

            if (intent.getAction().equals(FILE_EDIT_NOTIFY)) {
                if (intent.hasExtra("enabled") && intent.getBooleanExtra("enabled",false)) {
                    onFileFeatureEnabled();
                } else {
                    onFileFeatureDisabled();
                }
            }

        }
    };

    public void onActive() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CAMERA_NOTIFY);
        intentFilter.addAction(FILE_EDIT_NOTIFY);
        LocalBroadcastManager.getInstance(mBaseActivity).registerReceiver(broadcastReceiver, intentFilter);

    }

    public void onHidden(){


    }

    public void onDestroy() {
        LocalBroadcastManager.getInstance(mBaseActivity).unregisterReceiver(broadcastReceiver);
    }

    public boolean checkCameraPermissionWithRun(Runnable runnable) {
        if (ContextCompat.checkSelfPermission(mBaseActivity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            cameraPendingRunnable = runnable;
            requestCameraPermission();

            return false;
        } else {
            if (runnable != null) {
                runnable.run();
            }

            return true;
        }
    }


    public static boolean isFilePermissionEnabled(Context context){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    public boolean checkFilePermissionWithRun(Runnable runnable) {


        if (ContextCompat.checkSelfPermission(mBaseActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            filePendingRunnable = runnable;
            requestFilePermission();

            return false;
        } else {
            if (runnable != null) {
                runnable.run();
            }

            return true;
        }
    }

    final public static int REQUEST_FILE_PERMISSION = 100;
    final public static int REQUEST_CAMERA_PERMISSION = 200;
    public void requestFilePermission() {
        if (ContextCompat.checkSelfPermission(mBaseActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(mBaseActivity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_FILE_PERMISSION
            );
        }
    }

    public void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(mBaseActivity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(mBaseActivity,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION
            );
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FILE_PERMISSION: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    onFileFeatureEnabled();
                } else {

                    onFileFeatureDisabled();
                }
                return;
            }

            case REQUEST_CAMERA_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    onCameraFeatureEnabled();

                } else {

                    onCameraFeatureDisabled();
                }
                return;
            }

        }
    }
}

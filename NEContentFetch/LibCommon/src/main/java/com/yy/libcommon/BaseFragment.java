package com.yy.libcommon;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * ====================================
 * Created by michael.carr on 17/02/14.
 * ====================================
 */
public class BaseFragment extends Fragment implements BaseController {

    public  static final int REQUEST_TAKE_PHOTO = 200;

    private static final Field sChildFragmentManagerField;
    private static final String LOGTAG = "BaseFragment";
    protected ViewGroup mRootView;
    public boolean isDestroyed = false;

    private Intent takePictureIntent;
    public String tempPhotoFilePath = "";
    public SubFragPermission subFragPermission;
    protected boolean isActive = false;

    public void onInActive() {
        isActive = false;

    }

    public void onActive() {

        isActive = true;
    }

    public  void takePhoto() {
        subFragPermission.checkFilePermissionWithRun(new Runnable() {
            @Override
            public void run() {
                subFragPermission.filePendingRunnable = null;

                File photoFile = FileManager.createLocalImageFile(null);
                if(photoFile == null){
                    showToast("Failed to create image file!");
                    return;
                }
                else {
                    tempPhotoFilePath = photoFile.getAbsolutePath();
                }

                takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    takePictureIntent.putExtra("outputX", 640);
                    takePictureIntent.putExtra("outputY", 360);
                    takePictureIntent.putExtra("aspectX", 1);
                    takePictureIntent.putExtra("aspectY", 1);
                    takePictureIntent.putExtra("scale", true);

                    Uri photoURI = FileProvider.getUriForFile(getContext(),
                            "au.com.collectiveintelligence.fleetiq360.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                    subFragPermission.checkCameraPermissionWithRun(new Runnable() {
                        @Override
                        public void run() {
//                            if(isResumed()){
                                subFragPermission.cameraPendingRunnable = null;
                                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
//                            }
                        }
                    });
                }

            }
        });
    }


    public BaseActivity getBaseActivity(){
        return (BaseActivity)getActivity();
    }

    public BaseFragment(){
    }

    public int getScreenWidth() {
        Display display = getActivity().getWindow().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public void setSimpleSpinner(Spinner spinner, int stringArrayId){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                stringArrayId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    public void showErrDialog(String s) {
        showDialog(getContext(),getString(R.string.error),s);
    }

    public void showToast(String msg){
        if(isResumed() && getActivity()!=null){
            Toast toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0,0);
            toast.show();
        }
    }

    public void showProgress(String title, String message) {
        if(getBaseActivity() != null && !isDestroyed){
            getBaseActivity().showProgress(title,message);
        }
    }

    public void showSavingProgress(){
        if(getBaseActivity() != null && !isDestroyed){
            getBaseActivity().showSavingProgress();
        }
    }

    public void showDeletingProgress(){
        if(getBaseActivity() != null && !isDestroyed){
            getBaseActivity().showDeletingProgress();
        }
    }

    public void showLoadingProgress(){
        if(getBaseActivity() != null && !isDestroyed){
            getBaseActivity().showLoadingProgress();
        }
    }

    public void updateProgress(String message){
        if(getBaseActivity() != null && !isDestroyed){
            getBaseActivity().updateProgress(message);
        }
    }

    public void hideProgress(){
        if(getBaseActivity() != null && !isDestroyed){
            getBaseActivity().hideProgress();
        }
    }

    static {
        Field f = null;
        try {
            f = Fragment.class.getDeclaredField("mChildFragmentManager");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Log.e(LOGTAG, "Error getting mChildFragmentManager field", e);
        }
        sChildFragmentManagerField = f;
    }

    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        isDestroyed = false;

        subFragPermission = new SubFragPermission(getBaseActivity(),this);

    }

//    protected View findViewById(int id){
//        if(mRootView != null){
//            return mRootView.findViewById(id);
//        }
//        return null;
//
//    }

    public <T extends View> T findViewById(@IdRes int id) {

        if(mRootView != null){
            return mRootView.findViewById(id);
        }
        return null;
    }


    @Override
    public void onDetach() {
        super.onDetach();

//        if (sChildFragmentManagerField != null) {
//            try {
//                sChildFragmentManagerField.setAccessible(true);
//                sChildFragmentManagerField.set(this, null);
//            } catch (Exception e) {
//                Log.e(LOGTAG, "Error setting mChildFragmentManager field", e);
//            }
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(subFragPermission != null){ subFragPermission.onDestroy(); }
        isDestroyed = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        if(subFragPermission != null){ subFragPermission.onDestroy(); }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(subFragPermission != null){ subFragPermission.onActive(); }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public  void showDialog(Context context, String title, String msg){


        ErrorDialog errorDialog = ErrorDialog.newInstance(
                title,
                msg,
                context.getResources().getString(R.string.ok));

        errorDialog.show(getChildFragmentManager(), "errorDialog");
    }


    public  void showDialog(Context context, String title, String msg, boolean cancelable,
                            ErrorDialog.OnErrorCallback callback){


        ErrorDialog errorDialog = ErrorDialog.newInstance(
                title,
                msg,
                context.getResources().getString(R.string.ok));

        errorDialog.setCancelable(cancelable);
        errorDialog.setOnErrorCallback(callback);
        errorDialog.show(getChildFragmentManager(), "errorDialog");
    }

    public  void showDialog(Context context, String title, String msg, ErrorDialog.OnErrorCallback callback){


        ErrorDialog errorDialog = ErrorDialog.newInstance(
                title,
                msg,
                context.getResources().getString(R.string.ok));

        errorDialog.setOnErrorCallback(callback);
        errorDialog.show(getChildFragmentManager(), "errorDialog");
    }

    /////////////////Base Controller//////////////
    public SubFragPermission getSubFragPermission(){
        return subFragPermission;
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }

    @Override
    public FragmentManager getCurrentFragmentManager() {
        return getChildFragmentManager();
    }


    //----- Fragment managing
    public static void addFragment(FragmentManager fragmentManager, Fragment fragment, int container) {

        addFragment(fragmentManager,fragment,container,false);
    }
    public static void addFragment(FragmentManager fragmentManager, Fragment fragment, int container, boolean backStack) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(container,  fragment);
        if(backStack){
            ft.addToBackStack(null);
        }
        ft.commit();
    }
    public static void removeFragment(FragmentManager fragmentManager, int container) {

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.remove(fragmentManager.findFragmentById(container));
        ft.commit();
    }
    public void addFragment(Fragment fragment, int container) {

        addFragment(fragment,container,false);
    }
    public void addFragment(Fragment fragment, int container, boolean backStack) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(container,  fragment);
        if(backStack){
            ft.addToBackStack(null);
        }
        ft.commit();
    }
    public void removeSelfFromParent() {

        if(getParentFragment() != null){
            Fragment fragment = getParentFragment();
            FragmentTransaction ft = fragment.getChildFragmentManager().beginTransaction();
            ft.remove(this);
            ft.commit();
        }
    }



}


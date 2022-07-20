package com.yy.libcommon;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.AnimRes;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class BaseActivity extends AppCompatActivity implements BaseController,FragmentInterface.FragmentHandle{

    private static final String TAG="BaseActivity";
    private Fragment mFragment;
    private FragmentManager manager;
    protected boolean isStarted = false;
    protected boolean isResumed = false;

    //* Http client related *//
    protected CustomProgressDialog mCustomProgressDialog;
    protected SubFragPermission subFragPermission;

    public int getScreenWidth() {
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }


    public void backToHome(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void addFragment(@IdRes int containerViewId, Fragment fragment, String tag) {

        mFragment =fragment;//new TimerSetFragment();
        manager=getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(containerViewId, mFragment, tag);
        transaction.commit();
    }
    public void addFragment(@IdRes int containerViewId, Fragment fragment, String tag, boolean addStack) {
        mFragment =fragment;//new TimerSetFragment();
        manager=getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(containerViewId, mFragment, tag);

        if(addStack){
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
    @Override
    public void hideFragment(String tag) {
        manager=getSupportFragmentManager();
        Fragment  pFragment=manager.findFragmentByTag(tag);
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.hide(pFragment).commit();
    }
    public void removeFragment(String tag) {
        manager=getSupportFragmentManager();
        Fragment  pFragment=manager.findFragmentByTag(tag);
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(pFragment).commit();
    }
    public Fragment findFramentByTag(String tag) {
        manager=getSupportFragmentManager();
        Fragment  pFragment=manager.findFragmentByTag(tag);
        return  pFragment;
    }
    @Override
    public Fragment showFragment(@IdRes int containerViewId,String tag, String className) {
        manager=getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment  pFragment=manager.findFragmentByTag(tag);
        if (pFragment==null) {
            try {
           //     Mylog.printf(TAG,"..............pFragment=null");
                pFragment = (Fragment) (Class.forName(className)).newInstance();
                addFragment(containerViewId,pFragment, tag);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        else
            transaction.show(pFragment).commit();
        return   pFragment;
    }
    public Fragment showFragmentWithoutStack(@IdRes int containerViewId, String tag, Fragment fragment) {

        manager=getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment  pFragment=manager.findFragmentByTag(tag);
        if (pFragment==null) {
            pFragment = fragment;
            addFragment(containerViewId,pFragment, tag, false);
        }
        else {
            transaction.show(pFragment).commit();
        }

        return   pFragment;
    }
    public Fragment showFragmentWithStack(@IdRes int containerViewId, String tag, Fragment fragment) {

        manager=getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment  pFragment=manager.findFragmentByTag(tag);
        if (pFragment==null) {
            pFragment = fragment;
            addFragment(containerViewId,pFragment, tag, true);
        }
        else {
            transaction.show(pFragment).commit();
        }

        return   pFragment;
    }
    public void addFragmentWithAnimation(@AnimRes int enter, @AnimRes int exit, @AnimRes int popEnter, @AnimRes int popExit,
                                         @IdRes int containerViewId, Fragment fragment, String tag, boolean addStack)
    {
        mFragment =fragment;//new TimerSetFragment();
        manager=getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(enter,exit,popEnter,popExit);
        transaction.replace(containerViewId, mFragment, tag);

        if(addStack){
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        subFragPermission.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }


    public void showDialog(String title, String msg){

        ErrorDialog errorDialog = ErrorDialog.newInstance(
                title,
                msg,
                getResources().getString(R.string.ok));

        errorDialog.show(getSupportFragmentManager(), "errorDialog");
    }

    protected void initKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                |WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    void showKeyboard(final View view){
        runLater(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        },200);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected void onRecover(Bundle savedInstanceState){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState!=null) {
            onRecover(savedInstanceState);
        }

        subFragPermission = new SubFragPermission(this,this);

        initKeyboard();

        isStarted = false;

        setBestOrientation();
    }

    public void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isStarted = true;

    }

    @Override
    protected void onResume(){
        super.onResume();
        isResumed = true;
        isStarted = true;
        subFragPermission.onActive();
    }

    @Override
    protected void onPause(){
        super.onPause();
        isResumed = false;
    }

    @Override
    protected void onStop(){
        super.onStop();
        isStarted = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subFragPermission.onDestroy();
    }

    public void runLater(final Runnable runnable, long delayMillis){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } catch (Exception e) {

                }
            }
        }, delayMillis);
    }

    public void setBestOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        DisplayMetrics dm = getResources().getDisplayMetrics();
//        double density = dm.density * 160;
//        double x = Math.pow(dm.widthPixels / density, 2);
//        double y = Math.pow(dm.heightPixels / density, 2);
//        double screenInches = Math.sqrt(x + y);
//
//        if(screenInches >=7 ){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }else {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
    }


    /////////////////Base Controller//////////////
    public SubFragPermission getSubFragPermission(){
        return subFragPermission;
    }

    @Override
    public FragmentManager getCurrentFragmentManager() {
        return getSupportFragmentManager();
    }

    public void showToast(String msg){
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0,0);
        toast.show();
    }

    public  void showDialog(String title, String msg, ErrorDialog.OnErrorCallback callback){


        ErrorDialog errorDialog = ErrorDialog.newInstance(
                title,
                msg,
                getResources().getString(R.string.ok));

        errorDialog.setOnErrorCallback(callback);
        errorDialog.show(getCurrentFragmentManager(), "errorDialog");
    }


    protected void showProgressDialog(){
        if(null == mCustomProgressDialog){
            return;
        }
        mCustomProgressDialog.setCancelable(false);
        mCustomProgressDialog.show(getSupportFragmentManager(),"customProgressDialog");
    }

    public void showProgress(String title,String message) {

        hideProgress();

        mCustomProgressDialog = CustomProgressDialog.newInstance(title, message);
        showProgressDialog();
    }

    public void showSavingProgress(){

        hideProgress();

        mCustomProgressDialog = CustomProgressDialog.newInstance(null, getString(R.string.saving));
        showProgressDialog();
    }

    public void showDeletingProgress(){

        hideProgress();

        mCustomProgressDialog = CustomProgressDialog.newInstance(null, getString(R.string.deleting));

        showProgressDialog();
    }

    public void showLoadingProgress(){

        hideProgress();

        mCustomProgressDialog = CustomProgressDialog.newInstance(null, getString(R.string.loading));

        showProgressDialog();
    }

    public void updateProgress(String message){
        if(mCustomProgressDialog != null){
            mCustomProgressDialog.updateText(message);
        }
    }

    public void hideProgress(){
        if(mCustomProgressDialog != null){
            mCustomProgressDialog.dismiss();
        }
    }
}

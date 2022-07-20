package com.yy.libcommon;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


/**
 * ==================================
 * Created by michael.carr on 18/12/2014.
 * ==================================
 */
public class BaseDialog extends DialogFragment implements  BaseController{

    protected ViewGroup mRootView;
    public IDialogGenericCallback mGenericCallback;
    public boolean isDestroyed = false;

    protected SubFragPermission subFragPermission;

    public int getScreenWidth() {
        Display display = getDialog().getWindow().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
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

    public BaseActivity getBaseActivity(){
        return (BaseActivity)getActivity();
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if(mGenericCallback!=null){
            mGenericCallback.callBack();
        }
    }

    public void showToast(String msg){
        if(isResumed() && getActivity()!=null){
            Toast toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0,0);
            toast.show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        subFragPermission = new SubFragPermission(getBaseActivity(),this);

        setupViews();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(subFragPermission != null){
            subFragPermission.onDestroy();
        }
        isDestroyed = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    protected void setupViews(){

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        subFragPermission.onActive();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        subFragPermission.onDestroy();
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

    public void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
            getChildFragmentManager().executePendingTransactions();
        }
    }

}

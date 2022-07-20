package com.yy.libcommon;

import android.view.View;
import android.view.ViewGroup;


/**
 * Created by steveyang on 6/12/16.
 */

public class BaseSubFragment {

    protected ViewGroup mRootView;
    protected BaseController mBaseController;
    protected BaseActivity mBaseActivity;
    protected boolean isHidden = true;

    public BaseSubFragment(BaseController baseController, BaseActivity activity) {

        mBaseActivity = activity;

        mBaseController = baseController;
    }

    public void onActive(View rootView) {
        mRootView = (ViewGroup) rootView;
        isHidden = false;
    }

    public void onActive() {
        isHidden = false;
    }

    public boolean isActive(){
        return !isHidden;
    }

    public void onHidden() {
        isHidden = true;
    }


    public void onDestroy() {

    }
}

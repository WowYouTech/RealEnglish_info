package com.yy.libcommon;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;

/**
 * Created by Administrator on 2017/5/6.
 */
public class FragmentInterface {
    public  interface   FragmentHandle  {
        void addFragment(@IdRes int containerViewId, Fragment fragment, String tag);
        void hideFragment(String tag);
         void removeFragment(String tag);
         Fragment findFramentByTag(String tag);
        Fragment showFragment(@IdRes int containerViewId,String tag, String className);
    }
}

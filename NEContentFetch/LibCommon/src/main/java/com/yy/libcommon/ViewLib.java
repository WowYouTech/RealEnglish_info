package com.yy.libcommon;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

public class ViewLib {

    public static void fadeOutAnimation(final View view, long animationDuration) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new LinearInterpolator());
        fadeOut.setStartOffset(animationDuration);
        fadeOut.setDuration(animationDuration);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        view.startAnimation(fadeOut);
    }
}

package com.yy.libcommon;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageButton;


public class RadioImageButton extends ImageButton implements Checkable {
    private OnCheckedChangeListener onCheckedChangeListener;
    boolean isChecked = false;

    public RadioImageButton(Context context) {
        super(context);
    }

    public RadioImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(attrs);
    }

    public RadioImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseAttributes(attrs);
    }

    int checkedDrawable;
    int uncheckedDrawable;

    private void parseAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ToggleImageButton);

        checkedDrawable = a.getResourceId(R.styleable.ToggleImageButton_checkedBackground, -1);
        uncheckedDrawable = a.getResourceId(R.styleable.ToggleImageButton_uncheckedBackground, -1);
        setChecked(a.getBoolean(R.styleable.ToggleImageButton_android_checked, false));
        a.recycle();
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        isChecked = checked;

        //setBackgroundResource(isChecked?checkedDrawable:uncheckedDrawable);
        setScaleType(ScaleType.FIT_CENTER);
        setImageResource(isChecked?checkedDrawable:uncheckedDrawable);

        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChanged(this, checked);
        }
    }

    @Override
    public void toggle() {
        if(isChecked){
            return;
        }

        setChecked(!isChecked());
    }

    @Override
    public boolean performClick() {

        toggle();

        return super.performClick();
    }

    public OnCheckedChangeListener getOnCheckedChangeListener() {
        return onCheckedChangeListener;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public void setOnClickListener(OnCheckedChangeListener onCheckedChangeListener) {

    }

    public static class OnCheckedChangeListener {
        public void onCheckedChanged(RadioImageButton buttonView, boolean isChecked){
            return;
        }
    }
}
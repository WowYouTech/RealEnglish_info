package com.yy.libcommon;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;


/**
 * ==================================
 * Created by michael.carr on 8/10/2014.
 * ==================================
 */
public class BaseThemedDialog extends BaseDialog {

    private static final int DIALOG_SHADOW_WIDTH = 16;
    private static final int LOLLIPOP_DIALOG_SHADOW_WIDTH = 32;
    private static final int VIBRATION_DURATION_MS = 10;
    private static final int TITLE_BAR_HEIGHT = 60;

    protected ViewGroup mLayoutView;
    protected int layoutResource;
    protected RelativeLayout mMainLayout;
    protected ArrayList<String> mRequests;
    private int mLayoutWidth;
    private Vibrator mVibrator;
    private ArrayList<View> mVibrateButtons;
    public TextView base_themed_dialog_right_text_view;
    public TextView base_themed_dialog_left_text_view;

    public String headerRightText;
    public String headerLeftText;

    protected View base_themed_dialog_titleView;
    protected String mTitle;

    protected boolean isWideDialog = false;

    protected void onLeftButton() {

    }

    protected void onRightButton() {

    }

    @Override
    protected void setupViews() {
        super.setupViews();

        //right text button
        base_themed_dialog_right_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRightButton();
            }
        });
        base_themed_dialog_left_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLeftButton();
            }
        });
        if(headerRightText != null && headerRightText.length()>0){
            base_themed_dialog_right_text_view.setVisibility(View.VISIBLE);
            base_themed_dialog_right_text_view.setText(headerRightText);
        }else{
            base_themed_dialog_right_text_view.setVisibility(View.GONE);
        }

        if(headerLeftText != null && headerLeftText.length()>0){
            base_themed_dialog_left_text_view.setVisibility(View.VISIBLE);
            base_themed_dialog_left_text_view.setText(headerLeftText);
        }else{
            base_themed_dialog_left_text_view.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = (ViewGroup) inflater.inflate(R.layout.base_themed_dialog, null);

        inflateLayout(inflater);

        mVibrator = (Vibrator) getActivity().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        mVibrateButtons = new ArrayList<>();

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        base_themed_dialog_titleView = mRootView.findViewById(R.id.base_themed_dialog_titleView);
        base_themed_dialog_right_text_view = (TextView)mRootView.findViewById(R.id.base_themed_dialog_right_text_view);
        base_themed_dialog_left_text_view = (TextView)mRootView.findViewById(R.id.base_themed_dialog_left_text_view);

        return mRootView;
    }

    public BaseThemedDialog(){
        mRequests = new ArrayList<>();
    }

    public void setLayout(int layoutResource){
        this.layoutResource = layoutResource;
    }

    public void setTitleText(String titleText){
        if (titleText != null) {
            ((TextView) mRootView.findViewById(R.id.base_themed_dialog_titleTextView)).setText(titleText);
        }
    }

    protected void inflateLayout(LayoutInflater inflater){

        if (layoutResource > 0) {
            mMainLayout = (RelativeLayout) mRootView.findViewById(R.id.base_themed_dialog_mainRelativeLayout);
            mLayoutView = (ViewGroup) inflater.inflate(layoutResource, mMainLayout, true);

            if(!isWideDialog){
                mLayoutView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                mLayoutWidth = mLayoutView.getMeasuredWidth();
            }else{
                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                mLayoutWidth = (int)(screenWidth*0.9);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mLayoutWidth = mLayoutWidth + Util.dpToPx(LOLLIPOP_DIALOG_SHADOW_WIDTH,getContext());
            } else {
                mLayoutWidth = mLayoutWidth + Util.dpToPx(DIALOG_SHADOW_WIDTH,getContext());
            }
        }

        mVibrator = (Vibrator) getActivity().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        mVibrateButtons = new ArrayList<>();

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        base_themed_dialog_titleView = mRootView.findViewById(R.id.base_themed_dialog_titleView);
        base_themed_dialog_right_text_view = (TextView)mRootView.findViewById(R.id.base_themed_dialog_right_text_view);
        base_themed_dialog_left_text_view = (TextView)mRootView.findViewById(R.id.base_themed_dialog_left_text_view);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setViewParams();

    }

    private void setViewParams(){
        getDialog().getWindow().setLayout(mLayoutWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    protected View findViewById(int viewId){
        return mLayoutView.findViewById(viewId);
    }


    private int getCurrentWidth(){
        return mRootView.getMeasuredWidth();
    }

    private int getCurrentHeight(){
        return mRootView.getMeasuredHeight() - Util.dpToPx(TITLE_BAR_HEIGHT,getContext());
    }

    protected void vibrateOnPress(View view){

        mVibrateButtons.add(view);

        for (final View b : mVibrateButtons){
            b.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            vibrate();
                            break;
                    }

                    return false;
                }
            });
        }
    }

    protected void setLayoutClickable(View view, boolean enabled) {
        Util.setLayoutClickable(view, enabled);
    }

    private void vibrate(){
        vibrate(VIBRATION_DURATION_MS);
    }

    private void vibrate(int duration){
        mVibrator.vibrate(Long.valueOf(String.valueOf(duration)));
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
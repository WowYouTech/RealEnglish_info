package com.yy.libcommon;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;


/**
 * ==================================
 * Created by michael.carr on 7/07/2014.
 * ==================================
 */
public class ErrorDialog extends BaseDialog {

    private TextView mTitleTextView;
    private TextView mErrorText;
    private TextView mCloseButton;

    private String mTitle;
    private String mContent;
    private String mCloseButtonText;

    private OnErrorCallback mCallback;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        mRootView = (ViewGroup) inflater.inflate(R.layout.base_error_dialog, container);

        return mRootView;
    }

    public static ErrorDialog newInstance(String title, String content, String buttonText){

        ErrorDialog fragment = new ErrorDialog();

        if (title != null){
            fragment.mTitle = title;
        }

        if (content != null){
            fragment.mContent = content;
        }

        if (buttonText != null) {
            fragment.mCloseButtonText = buttonText;
        }

        return fragment;

    }

    public void setOnErrorCallback(OnErrorCallback callback){
        mCallback = callback;
    }

    @Override
    protected void setupViews() {
        super.setupViews();

        getDialog().getWindow().setLayout((int)(getResources().getDimension(R.dimen.default_dialog_width)), ViewGroup.LayoutParams.WRAP_CONTENT);

        mTitleTextView = (TextView) mRootView.findViewById(R.id.error_dialog_titleTextView);
        mCloseButton = (TextView) mRootView.findViewById(R.id.error_dialog_closeTextView);
        mErrorText = (TextView) mRootView.findViewById(R.id.error_dialog_errorTextView);

        if (mTitle != null){
            mTitleTextView.setText(mTitle);
        } else {
            mTitleTextView.setText("");
        }

        if (mContent != null) {
            mErrorText.setText(mContent);
        } else {
            mErrorText.setHint("");
        }

        if (mCloseButtonText != null){
            mCloseButton.setText(mCloseButtonText);
        } else {
            mCloseButton.setText(getString(R.string.ok));
        }

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();

                if (mCallback != null){
                    mCallback.onCloseButton();
                }
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mCallback != null){
            mCallback.onDismiss();
        }
    }

    public static class OnErrorCallback {
        public void onCloseButton(){

        }
        public void onDismiss(){

        }
    }

}

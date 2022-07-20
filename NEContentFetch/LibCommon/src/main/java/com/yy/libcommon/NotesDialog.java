package com.yy.libcommon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * ==================================
 * Created by michael.carr on 7/07/2014.
 * ==================================
 */
public class NotesDialog extends BaseDialog {

    private Callback mCallback;
    private TextView mTitleTextView;
    private EditText mInputEditText;
    private Button mSaveButton;
    private String mSaveButtonText;
    private Button mCancelButton;

    private String mTitle;
    private String mHint;
    private String mContent;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        mRootView = (ViewGroup) inflater.inflate(R.layout.notes_dialog, container);
        return mRootView;
    }

    public static NotesDialog newInstance(String title, String hint, String content, Callback callback){

        NotesDialog fragment = new NotesDialog();

        if (title != null){
            fragment.mTitle = title;
        }

        if (hint != null){
            fragment.mHint = hint;
        }

        if (callback != null) {
            fragment.mCallback = callback;
        }

        if (content != null){
            fragment.mContent = content;
        }

        return fragment;
    }

    public void setAcceptText(String string){
        mSaveButtonText = string;
    }


    @Override
    protected void setupViews() {
        super.setupViews();

        getDialog().getWindow().setLayout((int)(getResources().getDimension(R.dimen.default_dialog_width)), ViewGroup.LayoutParams.WRAP_CONTENT);

        mTitleTextView = (TextView) mRootView.findViewById(R.id.notes_dialog_titleTextView);
        mInputEditText = (EditText) mRootView.findViewById(R.id.notes_dialog_editText);
        mSaveButton = (Button) mRootView.findViewById(R.id.notes_dialog_saveButton);
        mCancelButton = (Button) mRootView.findViewById(R.id.notes_dialog_cancelButton);
        if (mTitle != null){
            mTitleTextView.setText(mTitle);
        } else {
            mTitleTextView.setText("Title");
        }

        if (mSaveButtonText != null) {
            mSaveButton.setText(mSaveButtonText);
        }

        if (mHint != null) {
            mInputEditText.setHint(mHint);
        } else {
            mInputEditText.setHint("");
        }

        if (mContent != null){
            mInputEditText.setText(mContent);
            mInputEditText.setSelection(mInputEditText.getText().toString().length());
        } else {
            mInputEditText.setText("");
        }

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.callback(mInputEditText.getText().toString());
                dismiss();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public interface Callback{
        public void callback(String notes);
    }
}
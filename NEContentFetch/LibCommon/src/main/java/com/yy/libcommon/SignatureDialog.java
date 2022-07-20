package com.yy.libcommon;

import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * ====================================
 * Created by michael.carr on 26/06/2014.
 * ====================================
 */
public class SignatureDialog extends BaseDialog {

    private static final String BLOB_REF_KEY = "BLOB_REF_KEY";

    //* Views *//
    private ViewGroup mRootView;
    private DrawingView mDrawingView;
    private Button mClearButton;
    private Button mCancelButton;
    private Button mSaveButton;

    //* Instance *//
    String filePath;
    private SignatureCallback mSignatureCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRootView = (ViewGroup) inflater.inflate(R.layout.signature_dialog, container);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));

        if (getArguments() != null){
            filePath = getArguments().getString(BLOB_REF_KEY);
        }
        return mRootView;
    }


    public static SignatureDialog newInstance(String path) {
        SignatureDialog fragment = new SignatureDialog();
        Bundle bundle = new Bundle(2);
        bundle.putSerializable(BLOB_REF_KEY, path);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setCallback(SignatureCallback signatureCallback){
        mSignatureCallback = signatureCallback;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        findViews();
        setupViews();
        setUpViewListeners();
    }

    public void init() {
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void findViews() {
        mDrawingView = (DrawingView) mRootView.findViewById(R.id.signature_dialog_drawingView);
        mClearButton = (Button) mRootView.findViewById(R.id.signature_dialog_clearButton);
        mCancelButton = (Button) mRootView.findViewById(R.id.signature_dialog_cancelButton);
        mSaveButton = (Button) mRootView.findViewById(R.id.signature_dialog_saveButton);
    }

    public void setupViews() {
        if (filePath != null) {
            updateImageView(filePath);
        }
    }

    private void setUpViewListeners() {
        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawingView.clear();
            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelPress();
            }
        });
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSavePress();
            }
        });
    }

    private void onSavePress() {

         if (mDrawingView.hasBeenDrawnOn()) {

             mDrawingView.setDrawingCacheEnabled(true);

             boolean succeed = saveSignature(mDrawingView.getDrawingCache());
             if(succeed){
                 if (mSignatureCallback != null) {
                     mSignatureCallback.callBack(filePath);
                 }
                 dismiss();
             }
             else {
                 ErrorDialog errorDialog = ErrorDialog.newInstance(
                         "Error",
                         "Failed to save, , please check your internet connection and retry.",
                         getString(R.string.ok));
                 errorDialog.show(getChildFragmentManager(), "errorDialog");
             }

        } else { // Nothing has been drawn

            if (!mDrawingView.hasImage()) { // EditText not empty

                ErrorDialog errorDialog = ErrorDialog.newInstance(
                        "Error",
                        "Please sign!",
                        getString(R.string.ok));
                errorDialog.show(getChildFragmentManager(), "errorDialog");

            } else {

                if (mSignatureCallback != null) {
                    mSignatureCallback.callBack(filePath);
                }

                dismiss();

            }
        }
    }

    private void onCancelPress() {
        dismiss();
    }

    private boolean saveSignature(Bitmap bitmap) {

        try {

            File file = null;

            if(null == filePath){
                file  = FileManager.createLocalImageFile("signature_cache");
            }
            else {
                file = new File(filePath);
            }


            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, bytes);
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.close();

            filePath = file.getAbsolutePath();

            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    private void updateImageView(String currentPhotoPath){

        if (currentPhotoPath != null){
            //Set image view
            Bitmap imageBitmap = Util.decodeFile(currentPhotoPath, getScreenWidth());
            mDrawingView.setImageBitmap(imageBitmap);

        } else {
            mDrawingView.setImageBitmap(null);
            Log.i("DEV9", "Clearing image");
        }
    }


    public interface SignatureCallback {
        public void callBack(String filePath);
    }
}
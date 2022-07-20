package com.yy.libcommon;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



/**
 * ==================================
 * Created by michael.carr on 9/09/2014.
 * ==================================
 */
public class CustomProgressDialog extends BaseThemedDialog {

    private static final String TITLE = "TITLE";
    private static final String DETAILS = "DETAILS";

    private String mTitle;
    private String mDetails;

    private TextView mDetailsTextView;

    public CustomProgressDialog(){
        setLayout(R.layout.custom_progress_dialog);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null){
            mTitle = getArguments().getString(TITLE);
            mDetails = getArguments().getString(DETAILS);
        }
    }

    public static CustomProgressDialog newInstance(String title, String details){

        CustomProgressDialog fragment = new CustomProgressDialog();

        Bundle bundle = new Bundle(2);
        bundle.putString(TITLE, title);
        bundle.putString(DETAILS, details);
        fragment.setArguments(bundle);

        return fragment;

    }

    @Override
    protected void setupViews() {
        super.setupViews();

//        getDialog().getWindow().setLayout(Util.dpToPx(getResources().getDimension(R.dimen.default_dialog_width),
//                getActivity()), ViewGroup.LayoutParams.WRAP_CONTENT);
        mDetailsTextView = (TextView) mRootView.findViewById(R.id.custom_progress_dialog_detailsTextView);

        if (mTitle != null){
            setTitleText(mTitle);
        } else {
            setTitleText(getString(R.string.loading));
        }

        if (mDetails != null){
            mDetailsTextView.setText(mDetails);
        } else {
            mDetailsTextView.setText(getString(R.string.loading));
        }

        if(mTitle==null || mTitle.length()==0){
            base_themed_dialog_titleView.setVisibility(View.GONE);
        }
    }

    public void updateText(String message){

        if (mDetailsTextView != null && isResumed()){
            mDetailsTextView.setText(message);
        }
    }
}

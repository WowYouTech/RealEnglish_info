package com.yy.libcommon;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * ==================================
 * Created by michael.carr on 2/07/2014.
 * ==================================
 */
public class ThemedSingleListDialog<T> extends BaseThemedDialog {

    private ArrayList<T> mObjects;
    private Callback<T> mCallback;
    SubCallback<T> mSubCallback = null;
    private String mTitle;
    private ListView mListView;
    private ThemedDialogSingleListAdapter<T> mArrayAdapter;
    private T mSelectedObject;
    private boolean mAddAllOption;


    public ThemedSingleListDialog(){
        setLayout(R.layout.themed_single_list_dialog);
    }

    public static <T> ThemedSingleListDialog newInstance(ArrayList<T> objects, T selectedObject, String title, boolean addAllAtTop, Callback<T> callback){

        ThemedSingleListDialog themedSingleListDialog = new ThemedSingleListDialog();

        themedSingleListDialog.mCallback = callback;
        themedSingleListDialog.mObjects = objects;
        themedSingleListDialog.mTitle = title;
        themedSingleListDialog.mSelectedObject = selectedObject;
        themedSingleListDialog.mAddAllOption = addAllAtTop;

        return themedSingleListDialog;

    }

    public static class SubCallback<T> {
        public String getSubText(T object){
            return "";
        }
    }

    public static <T> ThemedSingleListDialog newInstance(ArrayList<T> objects,
                                                         T selectedObject, String title,
                                                         boolean addAllAtTop, Callback<T> callback,SubCallback subCallback){

        ThemedSingleListDialog dialog = ThemedSingleListDialog.newInstance(objects,selectedObject,title,addAllAtTop,callback);
        dialog.mSubCallback = subCallback;
        return dialog;
    }

    @Override
    protected void setupViews() {

        setTitleText(mTitle);

        if (mAddAllOption){
            mObjects.add(0, null);
        }

        mListView = (ListView) findViewById(R.id.themed_single_list_dialog_listView);


        mArrayAdapter = new ThemedDialogSingleListAdapter<>(getActivity(),
                R.layout.themed_dialog_single_list_item, mObjects, mSubCallback);

        if (mSelectedObject != null) {
            mArrayAdapter.setSelectedIndex(mObjects.indexOf(mSelectedObject));
        } else {
            //If something crashes, blame this
            if (mAddAllOption){
                mArrayAdapter.setSelectedIndex(0);
            }
        }

        mListView.setAdapter(mArrayAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onClick(position);

            }
        });
    }

    private void onClick(int itemPos){

        dismiss();

        if (mCallback != null) {
            mCallback.callBack(mArrayAdapter.getItem(itemPos),itemPos);
        }

    }

    public interface Callback<T> {
        public void callBack(T object, int index);
    }

}
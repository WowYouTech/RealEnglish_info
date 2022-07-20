package com.nativeenglish.tool;


import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.CaptionListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.nativeenglish.tool.models.ContentList;
import com.nativeenglish.tool.models.ContentToSort;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class PreProcessActivity extends NEBaseActivity implements EasyPermissions.PermissionCallbacks {

    int MaxNumberPerTime = 50;

    GoogleAccountCredential mCredential;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { YouTubeScopes.YOUTUBE_READONLY, YouTubeScopes.YOUTUBE_FORCE_SSL };

    Button toolButton1,toolButton2;
    private static YouTube mService = null;
    List<ContentList> contentLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_process);

        initYtbService();

        toolButton1 = (Button) findViewById(R.id.toolButton1);
        toolButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contentLists.clear();
                fetchContentToPreProcess();
            }
        });

        toolButton2 = (Button) findViewById(R.id.toolButton2);
        toolButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }



    void initYtbService() {
        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new YouTube.Builder(
                transport, jsonFactory, mCredential)
                .setApplicationName("YouTube Data API Android Quickstart")
                .build();
    }

    void fetchContentToPreProcess() {

        List<BmobQuery<ContentList>> queries = new ArrayList<>();
        BmobQuery<ContentList> eq1 = new BmobQuery<>();
        eq1.addWhereEqualTo("subKind", 0);
        queries.add(eq1);
        BmobQuery<ContentList> eq2 = new BmobQuery<>();
        eq2.addWhereDoesNotExists("subKind");
        queries.add(eq2);
        BmobQuery<ContentList> eq3 = new BmobQuery<>();
        eq3.addWhereDoesNotExists("duration");
        queries.add(eq3);
        BmobQuery<ContentList> eq4 = new BmobQuery<>();
        eq4.addWhereEqualTo("duration", 0);
        queries.add(eq4);

        BmobQuery<ContentList> mainQuery = new BmobQuery<>();
        mainQuery
                .or(queries)
                .addWhereEqualTo("originType","Ytb")
                .order("cIndex")
                .setLimit(MaxNumberPerTime);

        mainQuery.findObjects(new FindListener<ContentList>() {
            @Override
            public void done(List<ContentList> object, BmobException e) {

                if(e==null){

                    Log.d("sort","fetchContentToPreProcess number："+object.size());

                    if(object == null || object.size() == 0){
                        progressDialog.dismiss();
                        showToast("fetchContentToPreProcess Returned Empty, Just Relax.");
                    }
                    else{
                        contentLists.clear();
                        contentLists.addAll(object);
                        getResultsFromApi();
                    }
                }else{

                    Log.e("sort","fetchContentToPreProcess failed");
                    showToast("fetchContentToPreProcess failed");
                    progressDialog.dismiss();
                }
            }
        });
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            Log.e("error","No network connection available.");
        } else {
            progressDialog.show();
            new MakeRequestTask(mCredential).execute();
        }
    }

    /**
     * An asynchronous task that handles the YouTube Data API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, Boolean> {

        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
        }

        /**
         * Background task to call YouTube Data API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return false;
            }
        }

        /**
         * Fetch information about the "GoogleDevelopers" YouTube channel.
         * @return List of Strings containing information about the channel.
         * @throws IOException
         */
        private Boolean getDataFromApi() throws IOException {

            String vidCombined = "";
            for(ContentList contentList : contentLists){

                String ss = contentList.getOriginId();
                if(ss.isEmpty()){
                    continue;
                }
                if(vidCombined.isEmpty()){
                    vidCombined = vidCombined + ss;
                }
                else {
                    vidCombined = vidCombined + "," + ss;
                }
            }
            if(vidCombined.isEmpty()){
                return false;
            }
            else {
                VideoListResponse response = mService.videos().list("snippet,contentDetails").setId(vidCombined).execute();

                if(null == response || response.getItems().size() == 0){
                    return false;
                }
                else {
                    for(Video video : response.getItems()){
                        ContentList contentList = getContentByOriginId(video.getId());
                        if(null == contentList){
                            continue;
                        }

                        String caption = video.getContentDetails().getCaption();
                        if(caption.equals("true")){
                            contentList.setSubKind(ContentList.subKind_has_sub);
                        }
                        else {
                            contentList.setSubKind(ContentList.subKind_auto_sub);
                        }

                        long seconds = MyApplication.getDuration(video.getContentDetails().getDuration());
                        if(seconds> 0){
                            contentList.setDuration((int) seconds);
                        }
                        String channel = video.getSnippet().getChannelTitle();
                        contentList.setOriginChannel(channel);
                    }
                    return true;
                }
            }
        }

        ContentList getContentByOriginId(String originId) {
            if(contentLists.size() == 0){
                return null;
            }
            for(ContentList contentList : contentLists){
                if(contentList.getOriginId().equals(originId)){
                    return contentList;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean output) {

            if(!output){
                showToast("Fetch Video Info failed");
                progressDialog.dismiss();
                return;
            }
            saveContentListToServer(contentLists);
        }

        @Override
        protected void onCancelled() {

            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            PreProcessActivity.REQUEST_AUTHORIZATION);
                } else {
                    progressDialog.dismiss();
                    showToast("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                progressDialog.dismiss();
                showToast("Request cancelled.");
            }
        }
    }



    void saveContentListToServer(List<ContentList> listToSave ){

        List<BmobObject> objectList = new ArrayList<>();
        for(ContentList contentList : listToSave){
            objectList.add(contentList);
        }
        new BmobBatch().updateBatch(objectList).doBatch(new QueryListListener<BatchResult>() {

            @Override
            public void done(List<BatchResult> o, BmobException e) {
                if(e==null){
                    showToast("saveVideoListToServer Succeeded.");
                    fetchContentToPreProcess();
                }else{
                    Log.e("tool","saveContentListToServer failed");
                    showToast("saveVideoListToServer failed");
                    progressDialog.dismiss();

                }
            }
        });
    }



    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Log.d("debug",
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                PreProcessActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

}

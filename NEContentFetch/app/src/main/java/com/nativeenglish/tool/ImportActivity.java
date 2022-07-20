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
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.nativeenglish.tool.models.ChannelStatus;
import com.nativeenglish.tool.models.ContentToSort;
import com.nativeenglish.tool.models.DateLib;
import com.nativeenglish.tool.models.SearchChannel;
import com.nativeenglish.tool.models.SearchKeys;
import com.nativeenglish.tool.models.YoutubeDataModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class ImportActivity extends NEBaseActivity implements EasyPermissions.PermissionCallbacks {


    GoogleAccountCredential mCredential;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { YouTubeScopes.YOUTUBE_READONLY, YouTubeScopes.YOUTUBE_FORCE_SSL };

    Button toolButton1;

    static List<String> listYtbVideos = new ArrayList<>();
    static List<String> listToSave = new ArrayList<>();
    private static YouTube mService = null;
    HashMap<String,Integer> durationMap = new HashMap<>();
    HashMap<String,Integer> publishMap = new HashMap<>();
    HashMap<String,String> titleMap = new HashMap<>();

    String[] manualImportIds = {"aEvItEpMly8",
            "aa42dCfhVYw",
            "ucxjWlXf39Q",
            "jH3Nx7dJs24",
            "KavEoHGiU5I",
            "jPUO7M_b9k0",
            "pP0dulcLCAE",
            "ddaEVXvK6I4",
            "ZuSRbaTj-nY",
            "30tEeFc34x4",
            "Y27DfyMdAG8",
            "1oQOR9pIkQA",
            "siGVmxWM9ug",
            "X3DI-eSEFN8",
            "LwokxspsrA0",
            "xZqDVpmnajo",
            "9Yam5B_iasY",
            "iBYizdPEXDY",
            "E_Sb5P7los4",
            "06t3j5E4FvQ",
            "lvPxRyIWWX8",
            "Q5D6dfvlRjU",
            "4sC5XxhvE0c",
            "EeeKQa6cynE",
            "5rhQX9xZxwY&l",
            "iKcWu0tsiZM",
            "AOMpxsiUg2Q",
            "lQYzT49hyKo",
            "6Gc-lfpvMg8",
            "ARSNaSeT9hw",
            "OhdXJrGr1iM",
            "Rjab8fanzHc",
            "lJDC_CTdTnU",
            "OdMcbLT3jSY",
            "stxai1ZlEQY",
            "9k6UojCbu9I",
            "9x2_xd6F4dQ",
            "REboboHMqB4",
            "qQS5-To-_go",
            "JdA9_mtXYME",
            "bPcttdCRLqw",
            "NHGmamJw1L4",
            "6HhvwDoUlLQ",
            "t6SzxPK7EG0",
            "Iu88d2WcQF4",
            "9C2py9nMlRY",
            "Vvy9ytk0mBE",
            "ksG3mTPqd4w",
            "CnqDz-qh9Go",
            "K2vDU06PlSw",
            "zKvN_zL3nQk",
            "mqwY_N4XBno",
            "3LYcdhV_oss",
            "nLO18pjRjfg",
            "ZNNElZCMlSs",
            "aFGLXsp8l1U",
            "F7WB11NXYFE",
            "ZJ5ExJ5oDr4",
            "6mbt-ia3X2c",
            "2gtRaS8MA3U",
            "-Z5D7rzK9vM",
            "DxNnzN05xeY",
            "9eWZrW79H_k",
            "CLp-asUf_qE",
            "2OcGusiKWFA",
            "vm_jrbVMZPE",
            "ulKKj6Yy5Nk",
            "jPUO7M_b9k0",
            "RH10b65-Fys",
            "DmV6yTOhhlU",
            "l54w-CtUgx0",
            "YuOBzWF0Aws",
            "J5RZOU6vK4Q",
            "_rdINNHLYaQ",
            "v9yZco8bwI8",
            "hIiu0NWuCoU",
            "QNG71xzFxz0",
            "pAmAI3wpK_U",
            "MevKTPN4ozw",
            "-HZdU_VQvDI",
            "KpDOBxQKNvM",
            "03vWkDG-5aE",
            "5Wn6ZxCu2-Y",
            "cbP9yosjdq0",
            "xckvXtYtowY",
            "XJs7Ikq9chY",
            "zIRJXJT00vU",
            "tGZ4KmSMbuk",
            "VFkQSGyeCWg"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        initYtbService();

        listYtbVideos.addAll(Arrays.asList(manualImportIds));

        toolButton1 = (Button) findViewById(R.id.toolButton1);
        toolButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                durationMap.clear();
                publishMap.clear();
                titleMap.clear();
                getResultsFromApi();
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

            SubscriptionListResponse result = null;
            String nextPageToken = null;
            YouTube.Subscriptions.List ytbService;

            ytbService = mService.subscriptions().list("snippet")
                    .setMine(true).setMaxResults((long) 1);
            result = ytbService.execute();

            return true;
        }

        @Override
        protected void onPostExecute(Boolean output) {

            if(!output){
                showToast("Fetch channels failed");
                progressDialog.dismiss();
                return;
            }

            fetchVideoList();
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
                            ImportActivity.REQUEST_AUTHORIZATION);
                } else {
                    Log.e("Tool","The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                Log.e("Tool","Request cancelled.");
            }
        }
    }

    void fetchVideoList() {

        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] objects) {

                try {
                    String vidCombined = "";
                    listToSave.clear();
                    for(int i = 0; i< listYtbVideos.size() && i < 50; i++){

                        String ytbVid = listYtbVideos.get(i);

                        if(ytbVid.isEmpty()){
                            continue;
                        }
                        if(vidCombined.isEmpty()){
                            vidCombined = vidCombined + ytbVid;
                        }
                        else {
                            vidCombined = vidCombined + "," + ytbVid;
                        }
                        listToSave.add(ytbVid);
                    }

                    VideoListResponse videoListResponse = mService.videos().list("snippet,contentDetails").setId(vidCombined).execute();

                    if(videoListResponse != null && videoListResponse.getItems().size() > 0){

                        for(int i = 0; i< listToSave.size(); i++){

                            String vid = listToSave.get(i);

                            Video foundVideo = null;
                            for(Video video : videoListResponse.getItems()){
                                if(video.getId().equals(vid)){
                                    foundVideo = video;
                                    break;
                                }
                            }

                            if(foundVideo != null){
                                long seconds = MyApplication.getDuration(foundVideo.getContentDetails().getDuration());
                                durationMap.put(vid,new Integer((int) seconds));
                                publishMap.put(vid,new Integer((int) foundVideo.getSnippet().getPublishedAt().getValue()));
                                titleMap.put(vid, foundVideo.getSnippet().getTitle());
                            }
                        }
                    }
                    return videoListResponse;

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);


                if(o != null){
                    filterExistedVideoToSort();
                }
                else {
                    Log.e("tool","fetchVideoList failed number");
                    showToast("fetchVideoList failed number");
                    progressDialog.dismiss();
                }
            }
        }.execute();

    }


    void filterExistedVideoToSort() {
        if(listToSave.size() == 0){
            showToast("Finished");
            progressDialog.dismiss();
            return;
        }

        List<BmobQuery<ContentToSort>> queries = new ArrayList<>();
        int i = 0;
        for(String vid : listToSave){
            BmobQuery<ContentToSort> eq1 = new BmobQuery<>();
            eq1.addWhereEqualTo("originId", vid);
            queries.add(eq1);
            i++;
            if(i > 50){
                break;
            }
        }
        BmobQuery<ContentToSort> mainQuery = new BmobQuery<>();
        mainQuery.or(queries);
        mainQuery.findObjects(new FindListener<ContentToSort>() {
            @Override
            public void done(List<ContentToSort> object, BmobException e) {

                if(e==null){
                    saveVideoListToServer(object);
                    Log.d("tool","search existed content："+object.size());
                }else{
                    Log.e("tool","filterExistedVideoToSort failed number: ");
                    showToast("filterExistedVideoToSort failed number: ");
                    progressDialog.dismiss();
                }
            }
        });
    }

    void saveVideoListToServer(final List<ContentToSort> existedList){

        List<ContentToSort> contentToSorts = new ArrayList<>();
        ArrayList<String> existedIdList = new ArrayList<>();
        if(existedList != null){
            for(ContentToSort contentToSort : existedList){
                existedIdList.add(contentToSort.getOriginId());
            }
        }

        ArrayList<String> savedList = new ArrayList<>();

        for(String vid : listToSave){

            savedList.add(vid);

            if(existedIdList.contains(vid)){
                continue;
            }
            if(titleMap.get(vid) == null){
                continue;
            }

            ContentToSort contentToSort = new ContentToSort();
            contentToSort.setOriginId(vid);
            contentToSort.setFileName(titleMap.get(vid));
            contentToSort.setOriginType("Ytb");
            contentToSort.setOriginLink(YoutubeDataModel.getPlayStringFromYtbId(vid));
            contentToSort.setOriginChannel("");
            long pubTime = publishMap.get(vid);
            contentToSort.setPubTime(pubTime);
            if(durationMap.get(vid) != null){
                contentToSort.setDuration(durationMap.get(vid));
            }
            contentToSorts.add(contentToSort);

            if(contentToSorts.size() >= 50){
                break;
            }
        }

        List<String> toRemove = new ArrayList<>();
        for(String ss : listYtbVideos){
            if(savedList.contains(ss)){
                toRemove.add(ss);
            }
        }
        listYtbVideos.removeAll(toRemove);

        List<BmobObject> objectList = new ArrayList<>();
        for(ContentToSort contentToSort : contentToSorts){
            objectList.add(contentToSort);
        }

        new BmobBatch().insertBatch(objectList).doBatch(new QueryListListener<BatchResult>() {

            @Override
            public void done(List<BatchResult> o, BmobException e) {
                if(e==null){

                    fetchVideoList();

                }else{
                    Log.e("tool","saveVideoListToServer failed number：" );
                    showToast("saveVideoListToServer failed number：" );
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
                ImportActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

}

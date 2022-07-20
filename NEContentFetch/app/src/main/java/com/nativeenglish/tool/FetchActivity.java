package com.nativeenglish.tool;


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

import com.google.api.services.youtube.model.*;
import com.nativeenglish.tool.models.ChannelStatus;
import com.nativeenglish.tool.models.ContentList;
import com.nativeenglish.tool.models.ContentToSort;
import com.nativeenglish.tool.models.DateLib;
import com.nativeenglish.tool.models.SearchChannel;
import com.nativeenglish.tool.models.SearchKeys;
import com.nativeenglish.tool.models.YoutubeDataModel;

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

import java.io.IOException;
import java.time.Duration;
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


public class FetchActivity extends NEBaseActivity implements EasyPermissions.PermissionCallbacks {

    int maxChannelsToProcess = 500;
    long shortVideoDuration = 6*60;//sec
    long mediumVideoDuration = 9*60;//sec
    long minVideoDuration = 2*60;//sec

    GoogleAccountCredential mCredential;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { YouTubeScopes.YOUTUBE_READONLY, YouTubeScopes.YOUTUBE_FORCE_SSL };

    Button toolButton1,toolButton2;

    static List<SearchChannel> myChannels = new ArrayList<>();

    static int channelFetchIndex = 0;
    static List<SearchResult> listSearchResult = new ArrayList<>();
    private static com.google.api.services.youtube.YouTube mService = null;
    boolean searchByKeys = false;
    public boolean fetchSortVideos = false;
    public boolean fetchMediumVideos = false;
    HashMap<String,Integer> durationMap = new HashMap<>();
    HashMap<String,Integer> subMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch);

        initYtbService();

        toolButton1 = (Button) findViewById(R.id.toolButton1);
        toolButton2 = (Button) findViewById(R.id.toolButton2);
        toolButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchByKeys = false;
                fetchSortVideos = true;
                fetchMediumVideos = false;
                durationMap.clear();
                getResultsFromApi();
            }
        });
        toolButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchByKeys = true;
                fetchSortVideos = true;
                fetchMediumVideos = false;
                durationMap.clear();
                getResultsFromApi();
            }
        });


        findViewById(R.id.toolButtonMedium).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchByKeys = false;
                fetchSortVideos = false;
                fetchMediumVideos = true;
                durationMap.clear();
                getResultsFromApi();
            }
        });
        findViewById(R.id.toolButtonKeyMedium).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchByKeys = true;
                fetchSortVideos = false;
                fetchMediumVideos = true;
                durationMap.clear();
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
        mService = new com.google.api.services.youtube.YouTube.Builder(
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
            myChannels.clear();

            if(searchByKeys){
                fetchSearchKeys();
            }
            else {
                new MakeRequestTask(mCredential).execute();
            }
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

            while (result == null ||  (result != null && (nextPageToken != null && nextPageToken.length() > 0)) ) {

                ytbService = mService.subscriptions().list("snippet")
                        .setMine(true).setMaxResults((long) 50);

                if(nextPageToken != null && nextPageToken.length() > 0){
                    result = ytbService.setPageToken(nextPageToken).execute();
                }
                else {
                    result = ytbService.execute();
                }

                if(result != null && result.getItems().size() > 0){
                    nextPageToken = result.getNextPageToken();
                    List<Subscription> subscriptions = result.getItems();
                    myChannels.addAll(SearchChannel.fromSubscriptions(subscriptions));


                }
                if(myChannels.size() >= maxChannelsToProcess){
                    break;
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean output) {

            if(!output){
                showToast("Fetch channels failed");
                progressDialog.dismiss();
                return;
            }
            startFetchVideo();
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
                            FetchActivity.REQUEST_AUTHORIZATION);
                } else {
                    Log.e("Tool","The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                Log.e("Tool","Request cancelled.");
            }
        }
    }

    void fetchSearchKeys() {
        BmobQuery<SearchKeys> query = new BmobQuery<>();
        query.findObjects(new FindListener<SearchKeys>() {
            @Override
            public void done(List<SearchKeys> object, BmobException e) {
                if(e==null){
                    myChannels.addAll(SearchChannel.fromSeachKeys(object));
                }else{
                    showToast("Fetch SearchKeys failed: "+e.getMessage()+","+e.getErrorCode());
                }

                startFetchVideo();
            }
        });
    }

    void startFetchVideo() {
        channelFetchIndex = 0;
        progressDialog.setMessage("FetchChannelStatus Start, size is " + myChannels.size());
        fetchChannelStatus();
    }

    void fetchChannelStatus() {

        if(channelFetchIndex >= myChannels.size()) {
            progressDialog.dismiss();
            toolButton1.setEnabled(true);toolButton2.setEnabled(true);
            showToast("FetchChannelStatus Finished");
            return;
        }

        final SearchChannel searchChannel = myChannels.get(channelFetchIndex);
        final String channelId = searchChannel.getChannelId();

        BmobQuery<ChannelStatus> query = new BmobQuery<ChannelStatus>();
        query.addWhereEqualTo("chaId", channelId);
        query.findObjects(new FindListener<ChannelStatus>() {
            @Override
            public void done(List<ChannelStatus> object, BmobException e) {

                if(e==null){

                    DateTime dateTime = null;
                    org.joda.time.DateTime jdate = null;
                    ChannelStatus channelStatus = null;

                    if(object.size() > 0){
                        channelStatus = object.get(0);

                        if(channelStatus.getEndTime() != null){
                            long ends = channelStatus.getEndTimeInMs();
                            jdate = new org.joda.time.DateTime(ends);
                            org.joda.time.DateTime dateTime1 = jdate.plusDays(1);
                            dateTime = new DateTime(dateTime1.toDate());
                        }
                    }

                    //todo test only - skip fetch status check
                    Log.d("tool","fetchVideoList start number: " + (channelFetchIndex+1));
                    progressDialog.setMessage("fetchVideoList start number " + (channelFetchIndex+1));
                    fetchVideoList(searchChannel, null, channelStatus);
//                    if(jdate != null && DateLib.isSameLocalDay(jdate,DateLib.getCurrentDate())){
//                        channelFetchIndex++;
//                        Log.d("tool","fetchChannelStatus skipped number: "+channelFetchIndex);
//                        progressDialog.setMessage("fetchChannelStatus skipped number " + channelFetchIndex);
//                        fetchChannelStatus();
//                    }
//                    else{
//                        Log.d("tool","fetchVideoList start number: " + (channelFetchIndex+1));
//                        progressDialog.setMessage("fetchVideoList start number " + (channelFetchIndex+1));
//                        fetchVideoList(searchChannel, dateTime, channelStatus);
//                    }

                }else{
                    channelFetchIndex++;
                    Log.e("tool","fetchChannelStatus failed number: "+channelFetchIndex);
                    progressDialog.setMessage("fetchChannelStatus failed number " + channelFetchIndex);
                    fetchChannelStatus();
                }
            }
        });
    }

    void fetchVideoList(final SearchChannel searchChannel, DateTime lastFetchDate, final ChannelStatus channelStatus) {

        final DateTime endDate = new DateTime(org.joda.time.DateTime.now().toDate());
        DateTime startDate = lastFetchDate;

        if(null == lastFetchDate){
            org.joda.time.DateTime jd = DateLib.getCurrentDate().minusYears(1);
            long mills = jd.getMillis();
            startDate = new DateTime(mills);
        }

        listSearchResult.clear();
        final DateTime finalStartDate = startDate;

        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] objects) {

                try {

                    List<SearchResult> searchResults = new ArrayList<>();

                    SearchListResponse response = null;

                    YouTube.Search.List ytbService = mService.search().list("id,snippet").setType("video")
                            .setPublishedBefore(endDate).setPublishedAfter(finalStartDate)
                            .setRelevanceLanguage("en")
                            .setMaxResults((long) 30)
                            .setOrder("date");

                    if(searchChannel.subscription != null){
                        ytbService.setChannelId(searchChannel.getChannelId());
                    }
                    else {
                        ytbService.setQ(searchChannel.getChannelId());
                    }
                    if(fetchMediumVideos){
                        ytbService.setVideoDuration("medium");
                    }
                    response = ytbService.execute();

                    if(response != null && response.getItems().size() > 0) {

                        searchResults.addAll(response.getItems());

                        //todo fetch another page
//                        String nextPageToken = response.getNextPageToken();
//                        if(nextPageToken != null && nextPageToken.length() > 0) {
//                            ytbService.setPageToken(nextPageToken).setMaxResults((long)30);
//                            response = ytbService.execute();
//                            if(response != null) {
//                                searchResults.addAll(response.getItems());
//                            }
//                        }
                    }
                    else {
                        Log.e("OldChannels","Old or empty channels:"+searchChannel.getChannelTitle());
                    }

                    if(searchResults != null && searchResults.size() > 0) {

                        if(fetchSortVideos || fetchMediumVideos){

                            String vidCombined = "";
                            for(SearchResult searchResult : searchResults){
                                String ss = searchResult.getId().getVideoId();
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

                            ArrayList<SearchResult> shortResults = new ArrayList<>();

                            VideoListResponse videoListResponse = mService.videos().list("contentDetails").setId(vidCombined).execute();
                            if(videoListResponse != null && videoListResponse.getItems().size() > 0){

                                for(int i = 0; i< searchResults.size(); i++){

                                    SearchResult searchResult = searchResults.get(i);

                                    Video foundVideo = null;
                                    for(Video video : videoListResponse.getItems()){
                                        if(video.getId().equals(searchResult.getId().getVideoId())){
                                            foundVideo = video;
                                            break;
                                        }
                                    }

                                    if(foundVideo != null){

                                        long minDuration = 0;
                                        long maxDuration = 0;
                                        if(fetchSortVideos){
                                            minDuration = minVideoDuration;
                                            maxDuration = shortVideoDuration;
                                        }
                                        else if(fetchMediumVideos){
                                            minDuration = shortVideoDuration;
                                            maxDuration = mediumVideoDuration;
                                        }

                                        long seconds = MyApplication.getDuration(foundVideo.getContentDetails().getDuration());
                                        if(seconds> 0 && seconds < maxDuration && seconds >= minDuration){
                                            shortResults.add(searchResult);
                                            durationMap.put(searchResult.getId().getVideoId(),new Integer((int) seconds));

                                            String caption = foundVideo.getContentDetails().getCaption();
                                            if(caption.equals("true")){
                                                subMap.put(searchResult.getId().getVideoId(),ContentList.subKind_has_sub);
                                            }
                                            else {
                                                subMap.put(searchResult.getId().getVideoId(),ContentList.subKind_auto_sub);
                                            }

                                        }
                                    }
                                }
                            }
                            listSearchResult.addAll(shortResults);
                        }
                        else {
                            listSearchResult.addAll(response.getItems());
                        }
                    }


                    return response;

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);


                if(o != null){
                    if(listSearchResult.size() == 0){
                        saveChannelStatus(searchChannel,channelStatus,finalStartDate,endDate);
                    }
                    else {
                        filterExistedVideoToSort(searchChannel, channelStatus, finalStartDate, endDate);
                    }
                }
                else {
                    channelFetchIndex++;
                    Log.e("tool","fetchVideoList failed number: " + channelFetchIndex);
                    progressDialog.setMessage("fetchVideoList failed number: " + channelFetchIndex);
                    fetchChannelStatus();
                }
            }
        }.execute();

    }


    void filterExistedVideoToSort(final SearchChannel searchChannel, final ChannelStatus channelStatus, final DateTime startTime, final DateTime endTime) {

        List<BmobQuery<ContentToSort>> queries = new ArrayList<>();
        for(SearchResult searchResult : listSearchResult){
            String vid = searchResult.getId().getVideoId();
            BmobQuery<ContentToSort> eq1 = new BmobQuery<>();
            eq1.addWhereEqualTo("originId", vid);
            queries.add(eq1);
        }
        BmobQuery<ContentToSort> mainQuery = new BmobQuery<>();
        mainQuery.or(queries);
        mainQuery.findObjects(new FindListener<ContentToSort>() {
            @Override
            public void done(List<ContentToSort> object, BmobException e) {

                if(e==null){
                    saveVideoListToServer(object, searchChannel,channelStatus,startTime,endTime);
                    Log.d("tool","search existed content："+object.size());
                }else{
                    channelFetchIndex++;
                    Log.e("tool","filterExistedVideoToSort failed number: " + channelFetchIndex);
                    progressDialog.setMessage("filterExistedVideoToSort failed number: " + channelFetchIndex);
                    fetchChannelStatus();
                }
            }
        });
    }

    void saveVideoListToServer(final List<ContentToSort> existedList, final SearchChannel searchChannel, final ChannelStatus channelStatus, final DateTime startTime, final DateTime endTime) {

        if(listSearchResult.size() == 0){
            saveChannelStatus(searchChannel, channelStatus, startTime, endTime);
            return;
        }

        List<ContentToSort> contentToSorts = new ArrayList<>();
        ArrayList<String> existedIdList = new ArrayList<>();
        if(existedList != null){
            for(ContentToSort contentToSort : existedList){
                existedIdList.add(contentToSort.getOriginId());
            }
        }

        ArrayList<SearchResult> savedList = new ArrayList<>();

        for(SearchResult searchResult : listSearchResult){

            String vid = searchResult.getId().getVideoId();
            savedList.add(searchResult);

            if(existedIdList.contains(vid)){
                continue;
            }

            ContentToSort contentToSort = new ContentToSort();
            contentToSort.setOriginId(vid);
            contentToSort.setFileName(searchResult.getSnippet().getTitle());
            contentToSort.setOriginType("Ytb");
            contentToSort.setOriginLink(YoutubeDataModel.getPlayStringFromYtbId(vid));
            contentToSort.setOriginChannel(searchChannel.getChannelTitle());
            long pubTime = searchResult.getSnippet().getPublishedAt().getValue()/1000;
            contentToSort.setPubTime(pubTime);
            if(durationMap.get(vid) != null){
                contentToSort.setDuration(durationMap.get(vid));
            }
            if(subMap.get(vid) != null){
                contentToSort.setSubKind(subMap.get(vid));
            }
            contentToSorts.add(contentToSort);

            if(contentToSorts.size() >= 50){
                break;
            }
        }

        listSearchResult.removeAll(savedList);
        List<BmobObject> objectList = new ArrayList<>();
        for(ContentToSort contentToSort : contentToSorts){
            objectList.add(contentToSort);
        }

        new BmobBatch().insertBatch(objectList).doBatch(new QueryListListener<BatchResult>() {

            @Override
            public void done(List<BatchResult> o, BmobException e) {
                if(e==null){

                    if(listSearchResult.size() == 0){
                        saveChannelStatus(searchChannel, channelStatus, startTime, endTime);
                    }
                    else {
                        saveVideoListToServer(existedList, searchChannel, channelStatus,startTime,endTime);
                    }

//                    for(int i=0;i<o.size();i++){
//                        BatchResult result = o.get(i);
//                        BmobException ex =result.getError();
//                        if(ex==null){
//                            Log.d("Tool","第"+i+"个数据批量添加成功："+result.getCreatedAt()+","+result.getObjectId()+","+result.getUpdatedAt());
//                        }else{
//                            Log.e("Tool","第"+i+"个数据批量添加失败："+ex.getMessage()+","+ex.getErrorCode());
//                        }
//                    }
                }else{
                    channelFetchIndex++;
                    fetchChannelStatus();
                    Log.e("tool","saveVideoListToServer failed number：" + channelFetchIndex);
                    progressDialog.setMessage("saveVideoListToServer failed number：" + channelFetchIndex);
                }
            }
        });
    }


    void saveChannelStatus(SearchChannel searchChannel, final ChannelStatus channelStatus, DateTime startTime, DateTime endTime) {

        ChannelStatus channelStatusToEdit = channelStatus;
        if(null == channelStatus){
            channelStatusToEdit = new ChannelStatus();
        }

        channelStatusToEdit.setChaId(searchChannel.getChannelId());
        long startSec = startTime.getValue()/1000;
        long endSec = endTime.getValue()/1000;
        channelStatusToEdit.setStartTime((int)startSec);
        channelStatusToEdit.setEndTime((int) endSec);

        if(null == channelStatus){
            channelStatusToEdit.save(new SaveListener<String>() {

                @Override
                public void done(String objectId, BmobException e) {

                    channelFetchIndex++;
                    if(e==null){
                        Log.d("tool","saveChannelStatus succeeded：" + channelFetchIndex);
                        progressDialog.setMessage("Channel number " + channelFetchIndex +" fetched.");
                    }else{
                        Log.e("tool","saveChannelStatus Failed：" + channelFetchIndex);
                        progressDialog.setMessage("Channel number " + channelFetchIndex +" fetch failed.");
                    }
                    fetchChannelStatus();
                }
            });
        }else {
            channelStatusToEdit.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    channelFetchIndex++;
                    if(e==null){
                        Log.d("tool","saveChannelStatus succeeded：" + channelFetchIndex);
                        progressDialog.setMessage("Channel number " + channelFetchIndex +" fetched.");
                    }else{
                        Log.e("tool","saveChannelStatus Failed：" + channelFetchIndex);
                        progressDialog.setMessage("Channel number " + channelFetchIndex +" fetch failed.");
                    }
                    fetchChannelStatus();
                }
            });
        }

    }



    void deleteFoundToSortList(List<BmobObject> objectList,final String channelId, final ChannelStatus channelStatus, final DateTime startTime, final DateTime endTime) {

        new BmobBatch().deleteBatch(objectList).doBatch(new QueryListListener<BatchResult>() {

            @Override
            public void done(List<BatchResult> o, BmobException e) {
                if(e==null){
                    for(int i=0;i<o.size();i++){
                        BatchResult result = o.get(i);
                        BmobException ex =result.getError();
                        if(ex==null){
                            Log.d("Tool","第"+i+"个数据批量删除成功");
                        }else{
                            Log.d("Tool","第"+i+"个数据批量删除失败："+ex.getMessage()+","+ex.getErrorCode());
                        }
                    }
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
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
                FetchActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

}

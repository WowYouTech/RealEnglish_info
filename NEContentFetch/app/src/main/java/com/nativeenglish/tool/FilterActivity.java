package com.nativeenglish.tool;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.llollox.androidprojects.compoundbuttongroup.CompoundButtonGroup;
import com.nativeenglish.tool.models.ContentList;
import com.nativeenglish.tool.models.ContentToSort;
import com.nativeenglish.tool.models.DateLib;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;

public class FilterActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    public static List<ContentList> contentListsToFilter = new ArrayList<>();
    int currentContentIndex=0;
    ContentList currentContentToFilter;

    Button next;
    CompoundButtonGroup levels;
    CompoundButtonGroup categories;
    CompoundButtonGroup toEdit;
    Button exit, prev, save;
    TextView title;
    int maxLevelNumber = 4;

    private static String GOOGLE_YOUTUBE_API = "AIzaSyBH8szUCt1ctKQabVeQuvWgowaKxHVjn8E";
    private YouTubePlayerView mYoutubePlayerView = null;
    private YouTubePlayer mYoutubePlayer = null;

    protected ProgressDialog progressDialog;
    public void showToast(String msg){
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0,0);
        toast.show();
    }

    public void runLater(final Runnable runnable, long delayMillis){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } catch (Exception e) {

                }
            }
        }, delayMillis);
    }


    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        currentContentIndex = 0;
        if(contentListsToFilter == null || contentListsToFilter.size() == 0){
            showToast("No Data To Sort");
            finish();
        }
        else {
            currentContentToFilter = contentListsToFilter.get(currentContentIndex);
        }

        levels = findViewById(R.id.levels);
        categories = findViewById(R.id.categories);
        toEdit = findViewById(R.id.toEdit);
        next = findViewById(R.id.next);
        exit = findViewById(R.id.exit);
        prev = findViewById(R.id.prev);
        save = findViewById(R.id.save);
        title = findViewById(R.id.title);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentContentIndex == 0){
                    showToast("No Previous");
                    return;
                }
                currentContentIndex--;
                setCurrentVideoPlayer();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextButton();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterActivity.this.finish();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveResults(false);
            }
        });

        mYoutubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
        progressDialog.setMessage("");
        progressDialog.show();
        mYoutubePlayerView.initialize(GOOGLE_YOUTUBE_API, this);
    }

    void onNextButton() {

        if(!checkCurrent()){
            return;
        }

        if(currentContentIndex+1 >= contentListsToFilter.size()) {
            showToast("No More");
            return;
        }
        else {
            saveCurrentInfo();
            currentContentIndex++;
            setCurrentVideoPlayer();
        }
    }

    void setCurrentVideoPlayer() {
        currentContentToFilter = contentListsToFilter.get(currentContentIndex);
        mYoutubePlayer.cueVideo(currentContentToFilter.getOriginId());

        progressDialog.setMessage("");
        progressDialog.show();
        runLater(new Runnable() {
            @Override
            public void run() {
                Date date = new Date(currentContentToFilter.getPubTimeInMs());
                org.joda.time.DateTime dateTime = new org.joda.time.DateTime(date);
                String ds = "none";
                if(dateTime != null){
                    ds = DateLib.dateToLocalDayStringWithSlash(dateTime);
                }
                title.setText((currentContentIndex+1)+"/"+contentListsToFilter.size()
                        + "-" + currentContentToFilter.getcIndex()
                        +"-"
                        + ds
                        +"-"
                        +"-" + currentContentToFilter.getOriginChannel()
                        + "-" +currentContentToFilter.getFileName());

                int levelIndex = currentContentToFilter.getLeveViewIndex();

                int category = -1;
                if(currentContentToFilter.getCategory() != null){
                    category = currentContentToFilter.getCategory() - 1;
                }
                int toEditIndex = currentContentToFilter.getToEditInteger();

                levels.setCheckedPosition(levelIndex);
                categories.setCheckedPosition(category);
                toEdit.setCheckedPosition(toEditIndex);

                progressDialog.dismiss();
            }
        },1000);

    }

    void saveCurrentInfo() {
        if(null == currentContentToFilter){
            return;
        }

        int level = getLevel();
        int cat = getCat();
        int toEdit = getEdit();

        currentContentToFilter.setcLevel(level);
        currentContentToFilter.setCategory(cat);
        currentContentToFilter.setToEdit(toEdit);
    }

    int getLevel() {

        int viewLevel = -1;
        List<Integer> listlevel = levels.getCheckedPositions();
        if(listlevel.size() != 0){
            viewLevel = listlevel.get(0);
        }
        else {
            return 0;
        }

        if(viewLevel == 0){
            return ContentList.LEVEL_Clear_Simple;
        }
        else if(viewLevel == 1){
            return ContentList.LEVEL_Clear_Complex;
        }
        else if(viewLevel == 2){
            return ContentList.LEVEL_UnClear_Simple;
        }
        else if(viewLevel == 3){
            return ContentList.LEVEL_UnClear_Complex;
        }
        return ContentList.LEVEL_DISCARDED;
    }

    int getCat() {
        List<Integer> listCats = categories.getCheckedPositions();
        if(listCats.size() != 0){
            return listCats.get(0) + 1;
        }
        else {
            return 0;
        }
    }

    int getEdit() {
        List<Integer> listEdit = toEdit.getCheckedPositions();

        if(listEdit.size() != 0){
            return listEdit.get(0);
        }
        else {
            return 0;
        }
    }

    boolean isToDump() {
        if(getLevel() < 0){
            return true;
        }
        return false;
    }

    boolean checkCurrent() {
        if(isToDump()){
            return true;
        }

        if(getLevel() == 0|| getCat() == 0 || getEdit() < 0){
            showToast("Sort Current Content First.");
            return false;
        }

        return true;
    }

    void saveResults(boolean exit) {

        if(!checkCurrent()){
            if(currentContentIndex == 0 && exit){
                FilterActivity.this.finish();
            }
            return;
        }
        saveCurrentInfo();
        progressDialog.setMessage("");
        progressDialog.show();
        saveContentToFilter(exit);
    }

    void saveContentToFilter(final boolean exit){

        List<BmobObject> objectList = new ArrayList<>();
        for(int i = 0; i <= currentContentIndex; i++) {
            ContentList contentList = contentListsToFilter.get(i);
            objectList.add(contentList);
        }

        new BmobBatch().updateBatch(objectList).doBatch(new QueryListListener<BatchResult>() {

            @Override
            public void done(List<BatchResult> o, BmobException e) {

                if(e==null){

                    showToast("Content Save finished.");
                    progressDialog.dismiss();

                }else{
                    showToast("Content sort status save failed.");
                    progressDialog.dismiss();
                }
            }
        });
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {

        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);

        youTubePlayer.setPlaybackEventListener(playbackEventListener);

        mYoutubePlayer = youTubePlayer;

        progressDialog.dismiss();

        if (!wasRestored && currentContentToFilter != null) {
            setCurrentVideoPlayer();
        }

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

        showToast("Init youtube player failed.");
        progressDialog.dismiss();
    }

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onLoading() {

        }

        @Override
        public void onLoaded(String s) {

        }

        @Override
        public void onAdStarted() {

        }

        @Override
        public void onVideoStarted() {

        }

        @Override
        public void onVideoEnded() {

        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {

        }
    };

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onPlaying() {

        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onStopped() {

        }

        @Override
        public void onBuffering(boolean b) {

        }

        @Override
        public void onSeekTo(int i) {

        }
    };
}

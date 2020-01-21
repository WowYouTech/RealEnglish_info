package com.nativeenglish.tool;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.SearchResult;
import com.llollox.androidprojects.compoundbuttongroup.CompoundButtonGroup;
import com.nativeenglish.tool.models.ChannelStatus;
import com.nativeenglish.tool.models.ContentList;
import com.nativeenglish.tool.models.ContentToSort;
import com.nativeenglish.tool.models.DateLib;
import com.nativeenglish.tool.models.SearchChannel;
import com.nativeenglish.tool.models.YoutubeDataModel;

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

public class SortActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    public static List<ContentToSort> listContentToSorts;
    int currentContentIndex=0;
    ContentToSort currentContentToSort;

    Button next;
    CompoundButtonGroup levels;
    CompoundButtonGroup categories;
    CompoundButtonGroup toEdit;
    Button exit, prev, save;
    TextView title;
    int maxLevelNumber = 2;

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
        setContentView(R.layout.activity_sort);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        currentContentIndex = 0;
        if(listContentToSorts == null || listContentToSorts.size() == 0){
            showToast("No Data To Sort");
            finish();
        }
        else {
            currentContentToSort = listContentToSorts.get(currentContentIndex);
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
                SortActivity.this.finish();
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

        if(currentContentIndex+1 >= listContentToSorts.size()) {
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
        currentContentToSort = listContentToSorts.get(currentContentIndex);
        mYoutubePlayer.cueVideo(currentContentToSort.getOriginId());

        progressDialog.setMessage("");
        progressDialog.show();
        runLater(new Runnable() {
            @Override
            public void run() {
                Date date = new Date(currentContentToSort.getPubTimeInMs());
                org.joda.time.DateTime dateTime = new org.joda.time.DateTime(date);
                String ds = "none";
                if(dateTime != null){
                    ds = DateLib.dateToLocalDayStringWithSlash(dateTime);
                }
                title.setText((currentContentIndex+1)+"/"+listContentToSorts.size() + "-"+currentContentToSort.getIndex()
                        +"-"
                        + ds
                        +"-"
                        +"-" + currentContentToSort.getOriginChannel()
                        + "-" +currentContentToSort.getFileName());

                int levelIndex = -1;
                if(currentContentToSort.getLevel() != null){
                    if(currentContentToSort.getLevel() == -1){
                        levelIndex = maxLevelNumber;
                    }
                    else {
                        levelIndex = currentContentToSort.getLevel() - 1;
                    }
                }

                int category = -1;
                if(currentContentToSort.getCategory() != null){
                    category = currentContentToSort.getCategory() - 1;
                }
                int toEditIndex = currentContentToSort.getToEditInteger();

                levels.setCheckedPosition(levelIndex);
                categories.setCheckedPosition(category);
                toEdit.setCheckedPosition(toEditIndex);

                progressDialog.dismiss();
            }
        },1000);

    }

    void saveCurrentInfo() {
        if(null == currentContentToSort){
            return;
        }

        int level = getLevel();
        int cat = getCat();
        int toEdit = getEdit();

        currentContentToSort.setLevel(level);
        currentContentToSort.setCategory(cat);
        currentContentToSort.setToEdit(toEdit);
    }

    int getLevel() {
        List<Integer> listlevel = levels.getCheckedPositions();
        if(listlevel.size() != 0){
            int level = listlevel.get(0) + 1;
            if(level > maxLevelNumber){
                return -1;
            }
            else {
                return level;
            }
        }
        else {
            return 0;
        }
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
                SortActivity.this.finish();
            }
            return;
        }

        saveCurrentInfo();
        progressDialog.setMessage("");
        progressDialog.show();
        saveContentToSort(exit);
    }

    void saveContentToSort(final boolean exit){

        final ArrayList<ContentToSort> listToUpgrade = new ArrayList<>();

        List<BmobObject> objectList = new ArrayList<>();
        for(int i = 0; i <= currentContentIndex; i++) {
            ContentToSort contentToSort = listContentToSorts.get(i);

            contentToSort.setSortStatus(1);
            objectList.add(contentToSort);

            if(contentToSort.getLevel() > 0){
                listToUpgrade.add(contentToSort);
            }
        }

        new BmobBatch().updateBatch(objectList).doBatch(new QueryListListener<BatchResult>() {

            @Override
            public void done(List<BatchResult> o, BmobException e) {

                if(e==null){
                    progressDialog.setMessage("Content sort status saved.");

                    if(listToUpgrade.size() == 0){
                        showToast("Content Save finished.");
                        progressDialog.dismiss();
                    }
                    else {
                        filterExistedContent(listToUpgrade, exit);
                    }

                }else{
                    showToast("Content sort status save failed.");
                    progressDialog.dismiss();
                }
            }
        });
    }

    void filterExistedContent(final List<ContentToSort> listToSave, final boolean exit) {

        List<BmobQuery<ContentList>> queries = new ArrayList<>();

        for(ContentToSort contentToSort : listToSave){
            String originId = contentToSort.getOriginId();
            BmobQuery<ContentList> eq1 = new BmobQuery<>();
            eq1.addWhereEqualTo("originId", originId);
            queries.add(eq1);
        }
        BmobQuery<ContentList> mainQuery = new BmobQuery<>();
        mainQuery.or(queries);
        mainQuery.findObjects(new FindListener<ContentList>() {
            @Override
            public void done(List<ContentList> object, BmobException e) {

                if(e==null){

                    saveContentListToServer(listToSave, object, exit);

                }else{

                    Log.e("tool","filterExistedContent failed");
                    showToast("filterExistedContent failed");
                    progressDialog.dismiss();
                }
            }
        });
    }

    void saveContentListToServer(List<ContentToSort> listToSave, final List<ContentList> existedList, final boolean exit){


        ArrayList<String> existedIdList = new ArrayList<>();
        if(existedList != null){
            for(ContentList contentList : existedList){
                existedIdList.add(contentList.getOriginId());
            }
        }

        ArrayList<ContentList> contentListsToSave = new ArrayList<>();

        for(ContentToSort contentToSort : listToSave){

            String originId = contentToSort.getOriginId();

            if(existedIdList.contains(originId)){
                continue;
            }

            ContentList contentList = new ContentList();
            contentList.setOriginId(originId);
            contentList.setFileName(contentToSort.getFileName());
            contentList.setOriginType(contentToSort.getOriginType());
            contentList.setOriginLink(contentToSort.getOriginLink());
            contentList.setPubTime(contentToSort.getPubTime());
            contentList.setcLevel(contentToSort.getLevel());
            contentList.setCategory(contentToSort.getCategory());
            contentList.setToEdit(contentToSort.getToEditInteger());
            contentList.setcStatus(ContentList.cStatus_sorted);
            contentList.setDuration(contentToSort.getDurationInt());
            if(contentToSort.getSubKind() != null && contentToSort.getSubKind() > 0){
                contentList.setSubKind(contentToSort.getSubKind());
            }
            contentList.setOriginChannel(contentToSort.getOriginChannel());

            contentListsToSave.add(contentList);

            if(contentListsToSave.size() >= 50){
                break;
            }
        }

        List<BmobObject> objectList = new ArrayList<>();
        for(ContentList contentList : contentListsToSave){
            objectList.add(contentList);
        }

        new BmobBatch().insertBatch(objectList).doBatch(new QueryListListener<BatchResult>() {

            @Override
            public void done(List<BatchResult> o, BmobException e) {
                if(e==null){
                    showToast("saveVideoListToServer Succeeded.");
                    progressDialog.dismiss();
                    if(exit){
                        runLater(new Runnable() {
                            @Override
                            public void run() {
                                SortActivity.this.finish();
                            }
                        },2000);
                    }
                }else{
                    Log.e("tool","saveContentListToServer failed");
                    showToast("saveVideoListToServer failed");
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

        if (!wasRestored && currentContentToSort != null) {
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

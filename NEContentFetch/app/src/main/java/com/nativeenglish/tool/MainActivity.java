package com.nativeenglish.tool;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.viewpager.widget.ViewPager;

import com.nativeenglish.tool.models.ContentList;
import com.nativeenglish.tool.models.ContentToSort;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends NEBaseActivity {



    Button toolButton1,toolButton2, toolButton3, toolButtonPreProcess;
    static int TypeSize = 2;
    static int ContentSkipCount = 0;
    static int PageSize = 50;
    static int MinSortSize = 20;
    List<ContentToSort> contentToSortList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTabs();

        toolButton1 = (Button) findViewById(R.id.toolButton1);
        toolButton2 = (Button) findViewById(R.id.toolButton2);
        toolButton3 = (Button) findViewById(R.id.toolButton3);
        toolButtonPreProcess = (Button) findViewById(R.id.toolButtonPreProcess);

        toolButtonPreProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PreProcessActivity.class));
            }
        });
        toolButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FetchActivity.class));
            }
        });
        toolButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentSkipCount = 0;
                progressDialog.show();
                contentToSortList.clear();
                fetchContentToSort(0);
            }
        });
        toolButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentSkipCount = 0;
                progressDialog.show();
                fetchContentToFilter(0);
            }
        });
        findViewById(R.id.toolButtonCleanUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CleanUpActivity.class));
            }
        });
        findViewById(R.id.toolButtonImport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ImportActivity.class));
            }
        });
        findViewById(R.id.toolButtonFetchGood).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FetchGoodActivity.class));
            }
        });
        findViewById(R.id.toolButton4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentSkipCount = 0;
                progressDialog.show();
                fetchContentToFilter(1);
            }
        });

    }
    void fetchContentToFilter(int type) {

        BmobQuery<ContentList> mainQuery = new BmobQuery<>();
        if(type  == 0){
            mainQuery
                    .addWhereLessThan("cLevel",10)
                    .addWhereGreaterThan("cLevel",0)
                    .setLimit(PageSize).setSkip(ContentSkipCount);
        }
        else if(type == 1){
            mainQuery
                    .addWhereEqualTo("category",ContentList.category_temp)
                    .addWhereGreaterThan("cLevel",10)
                    .setLimit(PageSize).setSkip(ContentSkipCount);
        }


        mainQuery.findObjects(new FindListener<ContentList>() {
            @Override
            public void done(List<ContentList> object, BmobException e) {

                if(e==null){

                    Log.d("sort","fetchContentToFilter number："+object.size());

                    if(object == null || object.size() == 0){
                        progressDialog.dismiss();

                        showToast("fetchContentToFilter Returned Empty, have a rest.");
                    }
                    else{
                        openFilterActivity(object);
                    }
                }else{

                    Log.e("sort","fetchContentToFilter failed");
                    showToast("fetchContentToFilter failed");
                    progressDialog.dismiss();
                }
            }
        });
    }

    void fetchContentToSort(final int typeIndex) {

        List<BmobQuery<ContentToSort>> queries = new ArrayList<>();
        BmobQuery<ContentToSort> eq1 = new BmobQuery<>();
        eq1.addWhereEqualTo("sortStatus", 0);
        queries.add(eq1);
        BmobQuery<ContentToSort> eq2 = new BmobQuery<>();
        eq2.addWhereDoesNotExists("sortStatus");
        queries.add(eq2);

        BmobQuery<ContentToSort> mainQuery = new BmobQuery<>();
        mainQuery.or(queries)
                .setLimit(PageSize).setSkip(ContentSkipCount);

        mainQuery.findObjects(new FindListener<ContentToSort>() {
            @Override
            public void done(List<ContentToSort> object, BmobException e) {

                if(e==null){

                    Log.d("sort","fetchContentToSort number："+object.size());

                    if(object == null || object.size() == 0){
                        progressDialog.dismiss();

                        if(contentToSortList.size() > 0){
                            SortActivity.listContentToSorts = contentToSortList;
                            startActivity(new Intent(MainActivity.this, SortActivity.class));
                        }
                        else{
                            showToast("fetchContentToSort Returned Empty, have a rest.");
                        }
                    }
                    else{
                        openSortActivity(object,typeIndex);
                    }
                }else{

                    Log.e("sort","fetchContentToSort failed");
                    showToast("fetchContentToSort failed");
                    progressDialog.dismiss();
                }
            }
        });
    }

    void openFilterActivity(List<ContentList> objects) {

        progressDialog.dismiss();
        FilterActivity.contentListsToFilter.clear();
        FilterActivity.contentListsToFilter.addAll(objects);
        startActivity(new Intent(MainActivity.this, FilterActivity.class));

    }

    void openSortActivity(List<ContentToSort> objects, int typeIndex) {

        contentToSortList.addAll(objects);
//        for(ContentToSort contentToSort: objects){
//            if(contentToSort.getIndex()%TypeSize == typeIndex){
//                contentToSortList.add(contentToSort);
//            }
//        }
        if(contentToSortList.size() < MinSortSize){
            ContentSkipCount = ContentSkipCount + objects.size();
            fetchContentToSort(typeIndex);
        }
        else {
            progressDialog.dismiss();
            SortActivity.listContentToSorts = contentToSortList;
            startActivity(new Intent(MainActivity.this, SortActivity.class));
        }

    }

    void initTabs() {

//        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
//        viewPager = (ViewPager) findViewById(R.id.viewPager);
//
//        //setting the tabs title
//        tabLayout.addTab(tabLayout.newTab().setText("Channel"));
//        tabLayout.addTab(tabLayout.newTab().setText("PlayList"));
//        tabLayout.addTab(tabLayout.newTab().setText("Live"));
//
//        //setup the view pager
//        final PagerAdapter adapter = new com.nativeenglish.tool.adapters.PagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
//        viewPager.setAdapter(adapter);
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//
//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
    }

}

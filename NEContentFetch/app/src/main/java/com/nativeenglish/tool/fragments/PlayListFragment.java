package com.nativeenglish.tool.fragments;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import com.nativeenglish.tool.DetailsActivity;
import com.nativeenglish.tool.R;
import com.nativeenglish.tool.adapters.VideoPostAdapter;
import com.nativeenglish.tool.interfaces.OnItemClickListener;
import com.nativeenglish.tool.models.YoutubeDataModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayListFragment extends Fragment {

    private static String GOOGLE_YOUTUBE_API_KEY = "AIzaSyAdDix7i7a3an-gyXiquTV_14cIsr8-DZg";//here you should use your api key for testing purpose you can use this api also
    private static String PLAYLIST_ID = "UU7V6hW6xqPAiUfataAZZtWA";//here you should use your playlist id for testing purpose you can use this api also
    private static String CHANNLE_GET_URL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=" + PLAYLIST_ID + "&maxResults=20&key=" + GOOGLE_YOUTUBE_API_KEY + "";

    private RecyclerView mList_videos = null;
    private VideoPostAdapter adapter = null;
    private ArrayList<YoutubeDataModel> mListData = new ArrayList<>();

    public PlayListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_play_list, container, false);
        mList_videos = (RecyclerView) view.findViewById(R.id.mList_videos);
        initList(mListData);
        new RequestYoutubeAPI().execute();
        return view;
    }


    private void initList(ArrayList<YoutubeDataModel> mListData) {
        mList_videos.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new VideoPostAdapter(getActivity(), mListData, new OnItemClickListener() {
            @Override
            public void onItemClick(YoutubeDataModel item) {
                YoutubeDataModel youtubeDataModel = item;
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(YoutubeDataModel.class.toString(), youtubeDataModel);
                startActivity(intent);
            }
        });
        mList_videos.setAdapter(adapter);

    }


    //create an asynctask to get all the data from youtube
    private class RequestYoutubeAPI extends AsyncTask<Void, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(CHANNLE_GET_URL);
            Log.e("URL", CHANNLE_GET_URL);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                String json = EntityUtils.toString(httpEntity);
                return json;
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("response", jsonObject.toString());
                    mListData = parseVideoListFromResponse(jsonObject);
                    initList(mListData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ArrayList<YoutubeDataModel> parseVideoListFromResponse(JSONObject jsonObject) {
        ArrayList<YoutubeDataModel> mList = new ArrayList<>();

        if (jsonObject.has("items")) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    if (json.has("kind")) {
                        if (json.getString("kind").equals("youtube#playlistItem")) {
                            YoutubeDataModel youtubeObject = new YoutubeDataModel();
                            JSONObject jsonSnippet = json.getJSONObject("snippet");
                            String vedio_id = "";
                            if (jsonSnippet.has("resourceId")) {
                                JSONObject jsonResource = jsonSnippet.getJSONObject("resourceId");
                                vedio_id = jsonResource.getString("videoId");

                            }
                            String title = jsonSnippet.getString("title");
                            String description = jsonSnippet.getString("description");
                            String publishedAt = jsonSnippet.getString("publishedAt");
                            String thumbnail = jsonSnippet.getJSONObject("thumbnails").getJSONObject("high").getString("url");

                            youtubeObject.setTitle(title);
                            youtubeObject.setDescription(description);
                            youtubeObject.setPublishedAt(publishedAt);
                            youtubeObject.setThumbnail(thumbnail);
                            youtubeObject.setVideo_id(vedio_id);
                            mList.add(youtubeObject);

                        }
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return mList;

    }


}

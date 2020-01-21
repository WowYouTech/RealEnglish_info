package com.nativeenglish.tool.models;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Subscription;

import java.util.ArrayList;
import java.util.List;

public class SearchChannel {
    public Subscription subscription;
    public String keyStr;
    private String channelTitle;

    public static ArrayList<SearchChannel> fromSubscriptions(List<Subscription> subscriptions) {

        ArrayList<SearchChannel> list = new ArrayList<>();
        for(Subscription subscription : subscriptions) {
            String channel = subscription.getSnippet().getTitle();
            if(channel != null && channel.length() > 0){
                SearchChannel searchChannel = new SearchChannel();
                searchChannel.subscription = subscription;
                searchChannel.channelTitle = channel;
                list.add(searchChannel);
            }
        }
        return list;
    }

    public static ArrayList<SearchChannel> fromSeachKeys(List<SearchKeys> searchKeys) {

        ArrayList<SearchChannel> list = new ArrayList<>();
        for(SearchKeys searchKey : searchKeys) {
            SearchChannel searchChannel = new SearchChannel();
            searchChannel.keyStr = searchKey.getKeyStr();
            searchChannel.channelTitle = searchKey.getKeyStr();
            list.add(searchChannel);
        }
        return list;
    }

    public String getChannelId() {
        if(subscription != null){
            return subscription.getSnippet().getResourceId().getChannelId();
        }
        else if(keyStr != null){
            return keyStr;
        }
        return "";
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }
}


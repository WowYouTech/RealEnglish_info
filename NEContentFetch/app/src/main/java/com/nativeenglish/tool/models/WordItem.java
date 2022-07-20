package com.nativeenglish.tool.models;

import java.util.List;

public class WordItem {
    private String type;//   word, info
    private String originWord;
    private String matchStr;
    private String phoneticStr;
    private List<LocalTranslatedItem> translatedItems;
    private boolean searchedByUser;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOriginWord() {
        return originWord;
    }

    public void setOriginWord(String originWord) {
        this.originWord = originWord;
    }

    public String getMatchStr() {
        return matchStr;
    }

    public void setMatchStr(String matchStr) {
        this.matchStr = matchStr;
    }

    public String getPhoneticStr() {
        return phoneticStr;
    }

    public void setPhoneticStr(String phoneticStr) {
        this.phoneticStr = phoneticStr;
    }

    public List<LocalTranslatedItem> getTranslatedItems() {
        return translatedItems;
    }

    public void setTranslatedItems(List<LocalTranslatedItem> translatedItems) {
        this.translatedItems = translatedItems;
    }

    public boolean getSearchedByUser() {
        return searchedByUser;
    }

    public void setSearchedByUser(boolean searchedByUser) {
        this.searchedByUser = searchedByUser;
    }
}

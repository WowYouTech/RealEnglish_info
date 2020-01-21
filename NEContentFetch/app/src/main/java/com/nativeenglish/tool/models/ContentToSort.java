package com.nativeenglish.tool.models;

import java.util.List;

import cn.bmob.v3.BmobObject;

public class ContentToSort extends BmobObject {
    private Integer index;
    private Integer sortStatus;//0:to sort,1: sorted

    private String fileName;
    private Integer pubTime;

    private String originType;
    private String originId;
    private String originChannel;
    private String  originLink;
    private Integer duration;
    private Integer subKind;


    //From User Selection
    private Integer level;// Standard: 1, Hard: 2
    private Integer category;// Info:1 Show:2  Rubbish:3
    private Integer toEdit;//0:no edit,1:to edit

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getPubTime() {
        return pubTime;
    }
    public long getPubTimeInMs() {
        if(null == pubTime){
            return 0;
        }
        return pubTime.longValue()*1000;
    }

    public void setPubTime(long pubTime) {
        this.pubTime = (int)pubTime;
    }

    public String getOriginType() {
        return originType;
    }

    public void setOriginType(String originType) {
        this.originType = originType;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public String getOriginLink() {
        return originLink;
    }

    public void setOriginLink(String originLink) {
        this.originLink = originLink;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }


    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public boolean isSorted() {
        if(sortStatus != null && sortStatus > 0){
            return true;
        }
        return false;
    }

    public void setSortStatus(Integer sortStatus) {
        this.sortStatus = sortStatus;
    }

    private boolean getToEdit() {
        if(toEdit != null && toEdit > 0){
            return true;
        }
        return false;
    }

    public int getToEditInteger() {
        if(toEdit != null && toEdit > 0){
            return 1;
        }
        return 0;
    }

    public void setToEdit(Integer toEdit) {
        this.toEdit = toEdit;
    }

    public String getOriginChannel() {
        return originChannel;
    }

    public void setOriginChannel(String originChannel) {
        this.originChannel = originChannel;
    }

    public int getDurationInt() {
        if(duration != null){
            return duration.intValue();
        }
        return 0;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getSubKind() {
        return subKind;
    }

    public void setSubKind(Integer subKind) {
        this.subKind = subKind;
    }
}

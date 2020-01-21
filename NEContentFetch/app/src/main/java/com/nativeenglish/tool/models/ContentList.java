package com.nativeenglish.tool.models;

import cn.bmob.v3.BmobObject;

public class ContentList extends BmobObject {

    private Integer cIndex;
    private String fileName;
    private Integer pubTime;
    private Integer duration;
    private Integer cStatus;//0:none,1:sorted,2:processing,3:processed,4:verified


    //OriginInfo:
    private String originType;//Ytb,Record
    private String originId;
    private String originLink;
    private String originChannel;

    //PreProcessInfo:
    private Integer cLevel;//11:Clear_Simple,12:Clear_Complex,13:UnClear_Simple,14:UnClear_Complex
    private Integer category;//info,show,fun,real
    private Integer toEdit;
    private Integer subKind;//1:has sub, 2: has auto, -1:no sub, 0: not checked

    //11:Clear_Simple,12:Clear_Complex,13:UnClear_Simple,14:UnClear_Complex
    final public static int subKind_has_sub = 1,subKind_auto_sub=2,subKind_no_sub=-1;
    final public static int category_info = 1,category_show=2,category_fun=3,category_temp=4,category_real=5;;
    final public static int LEVEL_DISCARDED = -1,LEVEL_Clear_Simple = 11,LEVEL_Clear_Complex=12, LEVEL_UnClear_Simple=13,LEVEL_UnClear_Complex=14;
    final public static int cStatus_none=0,cStatus_sorted = 1,cStatus_processing=2,
            cStatus_processed=3, cStatus_verified=4;

    public boolean isYtbContent() {
        if(originType != null && originType.equals("Ytb")){
            return true;
        }
        return false;
    }

    public Integer getcIndex() {
        return cIndex;
    }

    public int getLeveViewIndex() {

        int level = 0;
        if(cLevel != null){
            level = cLevel.intValue();
        }

        if(level == LEVEL_Clear_Simple){
            return 0;
        }
        else if(level == LEVEL_Clear_Complex){
            return 1;
        }
        else if(level == LEVEL_UnClear_Simple){
            return 2;
        }
        else if(level == LEVEL_UnClear_Complex){
            return 3;
        }
        else if(level == LEVEL_DISCARDED){
            return 4;
        }
        return -1;
    }

    public void setcIndex(Integer cIndex) {
        this.cIndex = cIndex;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getPubTime() {
        return pubTime;
    }

    public void setPubTime(Integer puTime) {
        this.pubTime = puTime;
    }

    public long getPubTimeInMs() {
        if(null == pubTime){
            return 0;
        }
        return pubTime.longValue()*1000;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getcStatus() {
        return cStatus;
    }

    public void setcStatus(Integer cStatus) {
        this.cStatus = cStatus;
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

    public Integer getcLevel() {
        return cLevel;
    }

    public void setcLevel(Integer cLevel) {
        this.cLevel = cLevel;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
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

    public Integer getSubKind() {
        return subKind;
    }

    public void setSubKind(Integer subKind) {
        this.subKind = subKind;
    }

    public String getOriginChannel() {
        return originChannel;
    }

    public void setOriginChannel(String originChannel) {
        this.originChannel = originChannel;
    }
}

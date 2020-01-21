package com.nativeenglish.tool.models;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

public class ChannelStatus extends BmobObject {

    private String chaId;
    private Integer startTime;
    private Integer endTime;

    public String getChaId() {
        return chaId;
    }
    public void setChaId(String value) {
        this.chaId = value;
    }

    private Integer getStartTime() {
        return startTime;
    }
    public void setStartTime(int value) {
        this.startTime = value;
    }

    public long getEndTimeInMs() {
        return endTime.longValue()*1000;
    }

    public long getStartTimeInMs() {
        return startTime.longValue()*1000;
    }

    public Integer getEndTime() {
        return endTime;
    }
    public void setEndTime(int value) {
        this.endTime = value;
    }
}

package com.nativeenglish.tool.models;

public class ImitateItem implements Cloneable {

    private int subtitleIndex;
    private String contentStr;

    private int imitateTimes;
    private int shadowTimes;
    private int retellTimes;

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getContentStr() {
        return contentStr;
    }

    public void setContentStr(String contentStr) {
        this.contentStr = contentStr;
    }

    public int getSubtitleIndex() {
        return subtitleIndex;
    }

    public void setSubtitleIndex(int subtitleIndex) {
        this.subtitleIndex = subtitleIndex;
    }

    public int getImitateTimes() {
        return imitateTimes;
    }

    public void setImitateTimes(int imitateTimes) {
        this.imitateTimes = imitateTimes;
    }

    public int getShadowTimes() {
        return shadowTimes;
    }

    public void setShadowTimes(int shadowTimes) {
        this.shadowTimes = shadowTimes;
    }

    public int getRetellTimes() {
        return retellTimes;
    }

    public void setRetellTimes(int retellTimes) {
        this.retellTimes = retellTimes;
    }
}

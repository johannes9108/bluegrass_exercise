package com.jh.dangerzone.domain;

public enum Frequency{
    HOUR(3600000), DAY(86400000),WEEK(604800000),
    FIVESEC(5000),TENSEC(10000), NONE(0);

    private long milis;

    Frequency(long milis) {
        this.milis = milis;
    }

    public long getMilis() {
        return milis;
    }
}
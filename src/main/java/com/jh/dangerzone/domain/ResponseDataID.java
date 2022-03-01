package com.jh.dangerzone.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class ResponseDataID implements Serializable {

    private int stationID;

    private LocalDateTime timeStamp;

    public ResponseDataID(int stationID, LocalDateTime timeStamp) {
        this.stationID = stationID;
        this.timeStamp = timeStamp;
    }

    public ResponseDataID() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseDataID that = (ResponseDataID) o;
        return stationID == that.stationID && Objects.equals(timeStamp, that.timeStamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationID, timeStamp);
    }
}

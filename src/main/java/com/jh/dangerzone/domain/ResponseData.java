package com.jh.dangerzone.domain;


//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/** Representation of sought data
 * Also serves as JPA model
 */
@Entity
@Table(name = "weather")
@IdClass(ResponseDataID.class)
public class ResponseData {


    @Id
    @Column(name = "station_id")
    private int stationID;

    @Id
    @Column(name = "timestamp")
    private LocalDateTime timeStamp;


    @Column(name = "station_name")
    private String stationName;
    @Column(name = "temp")
    private float temperature;
    @Column(name = "wind_direction")
    private int windDirection;
    @Column(name = "wind_speed")
    private float windSpeed;


    public ResponseData(int stationID, String stationName, long timeStamp, float temperature, int windDirection, float windSpeed) {
        this.stationID = stationID;
        this.stationName = stationName;
        this.timeStamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp),ZoneId.systemDefault());
        this.temperature = temperature;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
    }

    public ResponseData() {

    }

    @Override
    public String toString() {
        return "ResponseData{" +
                "stationID='" + stationID + '\'' +
                ", stationName='" + stationName + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", temperature=" + temperature +
                ", windDirection=" + windDirection +
                ", windSpeed=" + windSpeed +
                '}';
    }

    public int getStationID() {
        return stationID;
    }

    public void setStationID(int stationID) {
        this.stationID = stationID;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp),ZoneId.systemDefault());
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public int getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(int windDirection) {
        this.windDirection = windDirection;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }
}

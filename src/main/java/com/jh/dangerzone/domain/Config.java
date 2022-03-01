package com.jh.dangerzone.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Business object for representation of config file
 */
public class Config {

    @NotNull(message = "Required stationId")
    private int stationId;

    @NotNull(message = "Requires a directory for log storage")
    @Pattern(regexp = "[A-z0-9-_\\/\\\\]{2,}")
    private String directoryLocation;

    private Frequency frequency;


    public Config() {
        this.frequency = frequency;
        this.stationId = stationId;
        this.directoryLocation = directoryLocation;
    }

    public Config(int stationId, String directoryLocation, Frequency frequency) {
        this.stationId = stationId;
        this.directoryLocation = directoryLocation;
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return "Config{" +
                "frequency='" + frequency + '\'' +
                ", stationId=" + stationId +
                ", directoryLocation='" + directoryLocation + '\'' +
                '}';
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public String getDirectoryLocation() {
        return directoryLocation;
    }

    public void setDirectoryLocation(String directoryLocation) {
        this.directoryLocation = directoryLocation;
    }
}

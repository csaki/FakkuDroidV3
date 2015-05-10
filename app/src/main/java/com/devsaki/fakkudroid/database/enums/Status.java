package com.devsaki.fakkudroid.database.enums;

/**
 * Created by DevSaki on 10/05/2015.
 */
public enum Status {

    SAVED(0, "Saved"), DOWNLOADED(1, "Downloaded"), DOWNLOADING(2, "Downloading"), PAUSED(3, "Paused"), ERROR(4, "Error"), MIGRATED(5, "Migrated");

    private int code;
    private String description;

    Status(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}

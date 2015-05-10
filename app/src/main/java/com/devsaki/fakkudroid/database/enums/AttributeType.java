package com.devsaki.fakkudroid.database.enums;

/**
 * Created by DevSaki on 10/05/2015.
 */
public enum AttributeType {

    ARTIST(0, "Artist"), PUBLISHER(1, "Publisher"), LANGUAGE(2, "Language"), TAG(3, "Tag"), TRANSLATOR(4, "Translator"), SERIE(5, "Serie");

    private int code;
    private String description;

    AttributeType(int code, String description) {
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

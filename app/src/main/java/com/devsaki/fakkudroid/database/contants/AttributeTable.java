package com.devsaki.fakkudroid.database.contants;

/**
 * Created by DevSaki on 10/05/2015.
 */
public abstract class AttributeTable {

    public static final String TABLE_NAME = "attribute";

    public static final String ID_COLUMN = "id";
    public static final String URL_COLUMN = "url";
    public static final String TITLE_COLUMN = "title";
    public static final String TYPE_COLUMN = "type";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + ID_COLUMN + " INTEGER PRIMARY KEY," + URL_COLUMN + " TEXT,"
            + TITLE_COLUMN + " TEXT" + "," + TYPE_COLUMN + " INTEGER" + ")";
}

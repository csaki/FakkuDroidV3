package com.devsaki.fakkudroid.database.contants;

/**
 * Created by DevSaki on 10/05/2015.
 */
public abstract class ContentTable {

    public static final String TABLE_NAME = "content";

    public static final String ID_COLUMN = "id";
    public static final String URL_COLUMN = "url";
    public static final String TITLE_COLUMN = "title";
    public static final String HTML_DESCRIPTION_COLUMN = "html_description";
    public static final String QTY_PAGES_COLUMN = "qty_pages";
    public static final String UPLOAD_DATE_COLUMN = "upload_date";
    public static final String DOWNLOAD_DATE_COLUMN = "download_date";
    public static final String STATUS_COLUMN = "status";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + ID_COLUMN + " INTEGER PRIMARY KEY," + URL_COLUMN + " TEXT," + HTML_DESCRIPTION_COLUMN + " TEXT,"
            + TITLE_COLUMN + " TEXT" + "," + QTY_PAGES_COLUMN + " INTEGER" + ","
            + UPLOAD_DATE_COLUMN + " INTEGER" + "," + DOWNLOAD_DATE_COLUMN + " INTEGER" + ","
            + STATUS_COLUMN + " INTEGER" + ")";
}

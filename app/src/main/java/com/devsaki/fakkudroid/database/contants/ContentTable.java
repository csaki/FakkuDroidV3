package com.devsaki.fakkudroid.database.contants;

/**
 * Created by DevSaki on 10/05/2015.
 */
public abstract class ContentTable {

    public static final String TABLE_NAME = "content";

    public static final String ID_COLUMN = "id";
    public static final String FAKKU_ID_COLUMN = "fakku_id";
    public static final String CATEGORY_COLUMN = "category";
    public static final String URL_COLUMN = "url";
    public static final String TITLE_COLUMN = "title";
    public static final String HTML_DESCRIPTION_COLUMN = "html_description";
    public static final String QTY_PAGES_COLUMN = "qty_pages";
    public static final String UPLOAD_DATE_COLUMN = "upload_date";
    public static final String DOWNLOAD_DATE_COLUMN = "download_date";
    public static final String STATUS_COLUMN = "status";
    public static final String COVER_IMAGE_URL_COLUMN = "cover_image_url";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + ID_COLUMN + " INTEGER PRIMARY KEY," + FAKKU_ID_COLUMN + " TEXT," + CATEGORY_COLUMN + " TEXT,"
            + URL_COLUMN + " TEXT," + HTML_DESCRIPTION_COLUMN + " TEXT,"
            + TITLE_COLUMN + " TEXT" + "," + QTY_PAGES_COLUMN + " INTEGER" + ","
            + UPLOAD_DATE_COLUMN + " INTEGER" + "," + DOWNLOAD_DATE_COLUMN + " INTEGER" + ","
            + STATUS_COLUMN + " INTEGER"+ "," + COVER_IMAGE_URL_COLUMN + " TEXT" + ")";

    public static final String INSERT_STATEMENT = "INSERT OR REPLACE INTO " + TABLE_NAME + " VALUES (?,?,?,?,?,?,?,?,?,?,?);";
    public static final String UPDATE_CONTENT_DOWNLOAD_DATE_STATUS_STATEMENT = "UPDATE " + TABLE_NAME + " SET " + DOWNLOAD_DATE_COLUMN + " = ?, " + STATUS_COLUMN
            + " = ? WHERE " + ID_COLUMN + " = ?";

    public static final String SELECT_BY_CONTENT_ID = "SELECT " + ID_COLUMN + ", " + FAKKU_ID_COLUMN + ", " + CATEGORY_COLUMN + ", " + URL_COLUMN + ", "
            + TITLE_COLUMN + ", " + HTML_DESCRIPTION_COLUMN + ", " + QTY_PAGES_COLUMN + ", "
            + UPLOAD_DATE_COLUMN + ", " + DOWNLOAD_DATE_COLUMN + ", " + STATUS_COLUMN  + ", " + COVER_IMAGE_URL_COLUMN + " FROM " + TABLE_NAME + " C WHERE C." + ID_COLUMN + " = ?";
}

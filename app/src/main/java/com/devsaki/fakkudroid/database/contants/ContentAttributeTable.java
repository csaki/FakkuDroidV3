package com.devsaki.fakkudroid.database.contants;

/**
 * Created by DevSaki on 10/05/2015.
 */
public abstract class ContentAttributeTable {

    public static final String TABLE_NAME = "content_attribute";

    public static final String CONTENT_ID_COLUMN = "content_id";
    public static final String ATTRIBUTE_ID_COLUMN = "attribute_id";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + CONTENT_ID_COLUMN + " INTEGER," + ATTRIBUTE_ID_COLUMN + " INTEGER" + ")";

    public static final String INSERT_STATEMENT = "INSERT OR IGNORE INTO "+ TABLE_NAME +" VALUES (?,?);";
}

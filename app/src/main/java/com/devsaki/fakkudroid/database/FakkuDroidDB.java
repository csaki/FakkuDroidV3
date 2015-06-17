package com.devsaki.fakkudroid.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.devsaki.fakkudroid.database.contants.AttributeTable;
import com.devsaki.fakkudroid.database.contants.ContentAttributeTable;
import com.devsaki.fakkudroid.database.contants.ContentTable;
import com.devsaki.fakkudroid.database.contants.ImageFileTable;
import com.devsaki.fakkudroid.database.domains.Attribute;
import com.devsaki.fakkudroid.database.domains.Content;
import com.devsaki.fakkudroid.database.domains.ImageFile;
import com.devsaki.fakkudroid.database.enums.AttributeType;
import com.devsaki.fakkudroid.database.enums.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by DevSaki on 10/05/2015.
 */
public class FakkuDroidDB extends SQLiteOpenHelper {

    private static FakkuDroidDB instance;
    private static final String TAG = FakkuDroidDB.class.getName();
    private static final Object locker = new Object();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "fakkudroid.db";

    public FakkuDroidDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ContentTable.CREATE_TABLE);
        db.execSQL(AttributeTable.CREATE_TABLE);
        db.execSQL(ContentAttributeTable.CREATE_TABLE);
        db.execSQL(ImageFileTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + ContentAttributeTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AttributeTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ContentTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ImageFileTable.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public void insertContent(Content row) {
        insertContents(new Content[]{row});
    }

    public void insertContents(Content[] rows) {
        synchronized (locker) {
            Log.i(TAG, "insertContents");
            SQLiteDatabase db = null;
            try {
                db = this.getWritableDatabase();
                SQLiteStatement statement = db.compileStatement(Content.INSERT_STATEMENT);
                db.beginTransaction();
                for (Content row : rows) {

                    deleteContent(db, row);

                    statement.clearBindings();
                    statement.bindLong(1, row.getId());
                    statement.bindString(2, row.getFakkuId());
                    statement.bindString(3, row.getCategory());
                    statement.bindString(4, row.getUrl());
                    if (row.getHtmlDescription() == null)
                        statement.bindNull(5);
                    else
                        statement.bindString(5, row.getHtmlDescription());
                    if(row.getTitle()==null)
                        statement.bindNull(6);
                    else
                        statement.bindString(6, row.getTitle());
                    statement.bindLong(7, row.getQtyPages());
                    statement.bindLong(8, row.getUploadDate());
                    statement.bindLong(9, row.getDownloadDate());
                    statement.bindLong(10, row.getStatus().getCode());
                    if (row.getCoverImageUrl() == null)
                        statement.bindNull(11);
                    else
                        statement.bindString(11, row.getCoverImageUrl());
                    statement.execute();

                    if (row.getImageFiles() != null)
                        insertImageFiles(db, row);

                    List<Attribute> attributes = new ArrayList<>();
                    if (row.getSerie() != null)
                        attributes.add(row.getSerie());
                    if (row.getArtists() != null)
                        attributes.addAll(row.getArtists());
                    if (row.getPublishers() != null)
                        attributes.addAll(row.getPublishers());
                    if (row.getLanguage() != null)
                        attributes.add(row.getLanguage());
                    if (row.getTags() != null)
                        attributes.addAll(row.getTags());
                    if (row.getTranslators() != null)
                        attributes.addAll(row.getTranslators());
                    if (row.getUser() != null)
                        attributes.add(row.getUser());
                    insertAttributes(db, row, attributes);
                }
                db.setTransactionSuccessful();
                db.endTransaction();

            } finally {
                Log.i(TAG, "insertContents - trying to close the db connection. Condition : " + (db != null && db.isOpen()));
                if (db != null && db.isOpen())
                    db.close(); // Closing database connection
            }
        }
    }

    public void insertImageFiles(Content content) {
        synchronized (locker) {
            Log.i(TAG, "insertImageFiles");
            SQLiteDatabase db = null;
            try {
                db = this.getWritableDatabase();
                db.beginTransaction();
                SQLiteStatement statement = db.compileStatement(ImageFileTable.INSERT_STATEMENT);
                SQLiteStatement statementImages = db.compileStatement(ImageFileTable.DELETE_STATEMENT);
                statementImages.clearBindings();
                statementImages.bindLong(1, content.getId());
                statementImages.execute();
                for (ImageFile row : content.getImageFiles()) {
                    statement.clearBindings();
                    statement.bindLong(1, row.getId());
                    statement.bindLong(2, content.getId());
                    statement.bindLong(3, row.getOrder());
                    statement.bindString(4, row.getUrl());
                    statement.bindString(5, row.getName());
                    statement.bindLong(6, row.getStatus().getCode());
                    statement.execute();
                }
                db.setTransactionSuccessful();
                db.endTransaction();

            } finally {
                Log.i(TAG, "insertImageFiles - trying to close the db connection. Condition : " + (db != null && db.isOpen()));
                if (db != null && db.isOpen())
                    db.close(); // Closing database connection
            }
        }
    }

    private void insertAttributes(SQLiteDatabase db, Content content, List<Attribute> rows) {
        SQLiteStatement statement = db.compileStatement(Attribute.INSERT_STATEMENT);
        SQLiteStatement statementContentAttribute = db.compileStatement(ContentAttributeTable.INSERT_STATEMENT);

        for (Attribute row : rows) {
            statement.clearBindings();
            statement.bindLong(1, row.getId());
            statement.bindString(2, row.getUrl());
            statement.bindString(3, row.getName());
            statement.bindLong(4, row.getType().getCode());
            statement.execute();

            statementContentAttribute.clearBindings();
            statementContentAttribute.bindLong(1, content.getId());
            statementContentAttribute.bindLong(2, row.getId());
            statementContentAttribute.execute();
        }
    }

    private void insertImageFiles(SQLiteDatabase db, Content content) {
        SQLiteStatement statement = db.compileStatement(ImageFileTable.INSERT_STATEMENT);

        for (ImageFile row : content.getImageFiles()) {
            statement.clearBindings();
            statement.bindLong(1, row.getId());
            statement.bindLong(2, content.getId());
            statement.bindLong(3, row.getOrder());
            statement.bindString(4, row.getUrl());
            statement.bindString(5, row.getName());
            statement.bindLong(6, row.getStatus().getCode());
            statement.execute();
        }
    }

    public Content selectContentById(int id) {
        Content result = null;
        synchronized (locker){
            Log.i(TAG, "selectContentById");
            SQLiteDatabase db = null;
            Cursor cursorContents = null;
            try {

                db = this.getReadableDatabase();
                cursorContents = db.rawQuery(ContentTable.SELECT_BY_CONTENT_ID, new String[]{id + ""});

                // looping through all rows and adding to list
                if (cursorContents.moveToFirst()) {
                    result = populateContent(cursorContents, db);
                }
            } finally {
                if(cursorContents!=null){
                    cursorContents.close();
                }
                Log.i(TAG, "selectContentById - trying to close the db connection. Condition : " + (db != null && db.isOpen()));
                if (db != null && db.isOpen())
                    db.close(); // Closing database connection
            }
        }
        return result;
    }

    public Content selectContentByStatus(Status status) {
        Content result = null;

        synchronized (locker){
            Log.i(TAG, "selectContentByStatus");

            SQLiteDatabase db = null;
            Cursor cursorContent = null;
            try {

                db = this.getReadableDatabase();
                cursorContent = db.rawQuery(ContentTable.SELECT_BY_STATUS, new String[]{status.getCode() + ""});

                if (cursorContent.moveToFirst()) {
                    result = populateContent(cursorContent, db);
                }
            } finally {
                if(cursorContent!=null){
                    cursorContent.close();
                }
                Log.i(TAG, "selectContentByStatus - trying to close the db connection. Condition : " + (db != null && db.isOpen()));
                if (db != null && db.isOpen())
                    db.close(); // Closing database connection
            }
        }


        return result;
    }

    public List<Content> selectContentInDownloadManager() {
        List<Content> result = null;
        synchronized (locker){
            Log.i(TAG, "selectContentInDownloadManager");
            SQLiteDatabase db = null;
            Cursor cursorContent = null;
            try {

                db = this.getReadableDatabase();
                cursorContent = db.rawQuery(ContentTable.SELECT_IN_DOWNLOAD_MANAGER, new String[]{Status.DOWNLOADING.getCode() + "", Status.PAUSED.getCode() + ""});

                if (cursorContent.moveToFirst()) {
                    result = new ArrayList<>();
                    do {
                        result.add(populateContent(cursorContent, db));
                    } while (cursorContent.moveToNext());
                }
            } finally {
                if(cursorContent!=null){
                    cursorContent.close();
                }
                Log.i(TAG, "selectContentInDownloadManager - trying to close the db connection. Condition : " + (db != null && db.isOpen()));
                if (db != null && db.isOpen())
                    db.close(); // Closing database connection
            }
        }

        return result;
    }

    public List<Content> selectContentByQuery(String query, int page, int qty, boolean orderAlphabetic) {
        List<Content> result = null;

        synchronized (locker){
            Log.i(TAG, "selectContentByQuery");

            SQLiteDatabase db = null;
            Cursor cursorContent = null;
            int start = (page - 1) * qty;
            try {
                query = "%" + query + "%";
                db = this.getReadableDatabase();
                String sql = ContentTable.SELECT_DOWNLOADS;
                if (orderAlphabetic) {
                    sql += ContentTable.ORDER_ALPHABETIC;
                } else {
                    sql += ContentTable.ORDER_BY_DATE;
                }
                if (qty < 0) {
                    cursorContent = db.rawQuery(sql, new String[]{Status.DOWNLOADED.getCode() + "", Status.ERROR.getCode() + "", Status.MIGRATED.getCode() + "", query, query, AttributeType.ARTIST.getCode() + "", AttributeType.TAG.getCode() + "", AttributeType.SERIE.getCode() + ""});
                } else {
                    cursorContent = db.rawQuery(sql + ContentTable.LIMIT_BY_PAGE, new String[]{Status.DOWNLOADED.getCode() + "", Status.ERROR.getCode() + "", Status.MIGRATED.getCode() + "", query, query, AttributeType.ARTIST.getCode() + "", AttributeType.TAG.getCode() + "", AttributeType.SERIE.getCode() + "", start + "", qty + ""});
                }


                if (cursorContent.moveToFirst()) {
                    result = new ArrayList<>();
                    do {
                        result.add(populateContent(cursorContent, db));
                    } while (cursorContent.moveToNext());
                }
            } finally {
                if(cursorContent!=null){
                    cursorContent.close();
                }
                Log.i(TAG, "selectContentByQuery - trying to close the db connection. Condition : " + (db != null && db.isOpen()));
                if (db != null && db.isOpen())
                    db.close(); // Closing database connection
            }
        }

        return result;
    }

    private Content populateContent(Cursor cursorContent, SQLiteDatabase db) {
        Content content = new Content();
        content.setUrl(cursorContent.getString(3));
        content.setTitle(cursorContent.getString(4));
        content.setHtmlDescription(cursorContent.getString(5));
        content.setQtyPages(cursorContent.getInt(6));
        content.setUploadDate(cursorContent.getLong(7));
        content.setDownloadDate(cursorContent.getLong(8));
        content.setStatus(Status.searchByCode(cursorContent.getInt(9)));
        content.setCoverImageUrl(cursorContent.getString(10));

        content.setImageFiles(selectImageFilesByContentId(db, content.getId()));

        //populate attributes
        List<Attribute> attributes = selectAttributesByContentId(db, content.getId());
        if (attributes != null)
            for (Attribute attribute : attributes) {
                if (attribute.getType() == AttributeType.ARTIST) {
                    if (content.getArtists() == null)
                        content.setArtists(new ArrayList<Attribute>());
                    content.getArtists().add(attribute);
                } else if (attribute.getType() == AttributeType.SERIE) {
                    content.setSerie(attribute);
                } else if (attribute.getType() == AttributeType.PUBLISHER) {
                    if (content.getPublishers() == null)
                        content.setPublishers(new ArrayList<Attribute>());
                    content.getPublishers().add(attribute);
                } else if (attribute.getType() == AttributeType.UPLOADER) {
                    content.setUser(attribute);
                } else if (attribute.getType() == AttributeType.LANGUAGE) {
                    content.setLanguage(attribute);
                } else if (attribute.getType() == AttributeType.TAG) {
                    if (content.getTags() == null)
                        content.setTags(new ArrayList<Attribute>());
                    content.getTags().add(attribute);
                } else if (attribute.getType() == AttributeType.TRANSLATOR) {
                    if (content.getTranslators() == null)
                        content.setTranslators(new ArrayList<Attribute>());
                    content.getTranslators().add(attribute);
                }
            }
        return content;
    }

    private List<ImageFile> selectImageFilesByContentId(SQLiteDatabase db, int id) {
        List<ImageFile> result = null;
        Cursor cursorImageFiles = null;
        try {
            cursorImageFiles = db.rawQuery(ImageFileTable.SELECT_BY_CONTENT_ID, new String[]{id + ""});

            // looping through all rows and adding to list
            if (cursorImageFiles.moveToFirst()) {
                result = new ArrayList<>();
                do {
                    ImageFile item = new ImageFile();
                    item.setOrder(cursorImageFiles.getInt(2));
                    item.setStatus(Status.searchByCode(cursorImageFiles.getInt(3)));
                    item.setUrl(cursorImageFiles.getString(4));
                    item.setName(cursorImageFiles.getString(5));
                    result.add(item);
                } while (cursorImageFiles.moveToNext());
            }
        }finally {
            if(cursorImageFiles!=null){
                cursorImageFiles.close();
            }
        }

        return result;
    }

    private List<Attribute> selectAttributesByContentId(SQLiteDatabase db, int id) {
        List<Attribute> result = null;
        Cursor cursorAttributes = null;
        try {
            cursorAttributes = db.rawQuery(AttributeTable.SELECT_BY_CONTENT_ID, new String[]{id + ""});

            // looping through all rows and adding to list
            if (cursorAttributes.moveToFirst()) {
                result = new ArrayList<>();
                do {
                    Attribute item = new Attribute();
                    item.setUrl(cursorAttributes.getString(1));
                    item.setName(cursorAttributes.getString(2));
                    item.setType(AttributeType.searchByCode(cursorAttributes.getInt(3)));
                    result.add(item);
                } while (cursorAttributes.moveToNext());
            }
        }finally {
            if(cursorAttributes!=null){
                cursorAttributes.close();
            }
        }

        return result;
    }

    public void updateImageFileStatus(ImageFile row) {
        synchronized (locker){
            Log.i(TAG, "updateImageFileStatus");
            SQLiteDatabase db = null;
            try {
                db = this.getWritableDatabase();
                SQLiteStatement statement = db.compileStatement(ImageFileTable.UPDATE_IMAGE_FILE_STATUS_STATEMENT);
                db.beginTransaction();
                statement.clearBindings();
                statement.bindLong(1, row.getStatus().getCode());
                statement.bindLong(2, row.getId());
                statement.execute();
                db.setTransactionSuccessful();
                db.endTransaction();
            } finally {
                Log.i(TAG, "updateImageFileStatus - trying to close the db connection. Condition : " + (db != null && db.isOpen()));
                if (db != null && db.isOpen())
                    db.close(); // Closing database connection
            }
        }
    }

    private void deleteContent(SQLiteDatabase db, Content content) {
        SQLiteStatement statement = db.compileStatement(ContentTable.DELETE_STATEMENT);
        SQLiteStatement statementImages = db.compileStatement(ImageFileTable.DELETE_STATEMENT);
        SQLiteStatement statementAttributes = db.compileStatement(ContentAttributeTable.DELETE_STATEMENT);
        statement.clearBindings();
        statement.bindLong(1, content.getId());
        statement.execute();
        statementImages.clearBindings();
        statementImages.bindLong(1, content.getId());
        statementImages.execute();
        statementAttributes.clearBindings();
        statementAttributes.bindLong(1, content.getId());
        statementAttributes.execute();
    }

    public void deleteContent(Content content) {
        synchronized (locker){
            Log.i(TAG, "deleteContent");
            SQLiteDatabase db = null;
            try {
                db = this.getWritableDatabase();
                SQLiteStatement statement = db.compileStatement(ContentTable.DELETE_STATEMENT);
                SQLiteStatement statementImages = db.compileStatement(ImageFileTable.DELETE_STATEMENT);
                SQLiteStatement statementAttributes = db.compileStatement(ContentAttributeTable.DELETE_STATEMENT);
                db.beginTransaction();
                statement.clearBindings();
                statement.bindLong(1, content.getId());
                statement.execute();
                statementImages.clearBindings();
                statementImages.bindLong(1, content.getId());
                statementImages.execute();
                statementAttributes.clearBindings();
                statementAttributes.bindLong(1, content.getId());
                statementAttributes.execute();
                db.setTransactionSuccessful();
                db.endTransaction();
            } finally {
                Log.i(TAG, "deleteContent - trying to close the db connection. Condition : " + (db != null && db.isOpen()));
                if (db != null && db.isOpen())
                    db.close(); // Closing database connection
            }
        }
    }

    public void updateContentStatus(Content row) {
        synchronized (locker){
            Log.i(TAG, "updateContentStatus");
            SQLiteDatabase db = null;
            try {
                db = this.getWritableDatabase();
                SQLiteStatement statement = db.compileStatement(ContentTable.UPDATE_CONTENT_DOWNLOAD_DATE_STATUS_STATEMENT);
                db.beginTransaction();
                statement.clearBindings();
                statement.bindLong(1, row.getDownloadDate());
                statement.bindLong(2, row.getStatus().getCode());
                statement.bindLong(3, row.getId());
                statement.execute();
                db.setTransactionSuccessful();
                db.endTransaction();
            } finally {
                Log.i(TAG, "updateContentStatus - trying to close the db connection. Condition : " + (db != null && db.isOpen()));
                if (db != null && db.isOpen())
                    db.close(); // Closing database connection
            }
        }
    }

    public void updateContentStatus(Status updateTo, Status updateFrom) {
        synchronized (locker){
            Log.i(TAG, "updateContentStatus2");
            SQLiteDatabase db = null;
            try {
                db = this.getWritableDatabase();
                SQLiteStatement statement = db.compileStatement(ContentTable.UPDATE_CONTENT_STATUS_STATEMENT);
                db.beginTransaction();
                statement.clearBindings();
                statement.bindLong(1, updateTo.getCode());
                statement.bindLong(2, updateFrom.getCode());
                statement.execute();
                db.setTransactionSuccessful();
                db.endTransaction();
            } finally {
                Log.i(TAG, "updateContentStatus2 - trying to close the db connection. Condition : " + (db != null && db.isOpen()));
                if (db != null && db.isOpen())
                    db.close(); // Closing database connection
            }
        }
    }
}

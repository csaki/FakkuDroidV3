package com.devsaki.fakkudroid.database;

import android.content.ContentValues;
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

/**
 * Created by DevSaki on 10/05/2015.
 */
public class FakkuDroidDB extends SQLiteOpenHelper {

    private static final String TAG = FakkuDroidDB.class.getName();

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
        Log.i(TAG, "insertContents");
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            SQLiteStatement statement = db.compileStatement(Content.INSERT_STATEMENT);
            db.beginTransaction();
            for (Content row : rows) {

                deleteContent(db, row);

                int indexColumn = 1;
                statement.clearBindings();
                statement.bindLong(indexColumn++, row.getId());
                statement.bindString(indexColumn++, row.getFakkuId());
                statement.bindString(indexColumn++, row.getCategory());
                statement.bindString(indexColumn++, row.getUrl());
                statement.bindString(indexColumn++, row.getHtmlDescription());
                statement.bindString(indexColumn++, row.getTitle());
                statement.bindLong(indexColumn++, row.getQtyPages());
                statement.bindLong(indexColumn++, row.getUploadDate());
                statement.bindLong(indexColumn++, row.getDownloadDate());
                statement.bindLong(indexColumn++, row.getStatus().getCode());
                statement.bindString(indexColumn++, row.getCoverImageUrl());
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

    public void insertImageFiles(Content content) {
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
                int indexColumn = 1;
                statement.clearBindings();
                statement.bindLong(indexColumn++, row.getId());
                statement.bindLong(indexColumn++, content.getId());
                statement.bindLong(indexColumn++, row.getOrder());
                statement.bindString(indexColumn++, row.getUrl());
                statement.bindString(indexColumn++, row.getName());
                statement.bindLong(indexColumn++, row.getStatus().getCode());
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

    private void insertAttributes(SQLiteDatabase db, Content content, List<Attribute> rows) {
        SQLiteStatement statement = db.compileStatement(Attribute.INSERT_STATEMENT);
        SQLiteStatement statementContentAttribute = db.compileStatement(ContentAttributeTable.INSERT_STATEMENT);

        for (Attribute row : rows) {
            int indexColumn = 1;
            statement.clearBindings();
            statement.bindLong(indexColumn++, row.getId());
            statement.bindString(indexColumn++, row.getUrl());
            statement.bindString(indexColumn++, row.getName());
            statement.bindLong(indexColumn++, row.getType().getCode());
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
            int indexColumn = 1;
            statement.clearBindings();
            statement.bindLong(indexColumn++, row.getId());
            statement.bindLong(indexColumn++, content.getId());
            statement.bindLong(indexColumn++, row.getOrder());
            statement.bindString(indexColumn++, row.getUrl());
            statement.bindString(indexColumn++, row.getName());
            statement.bindLong(indexColumn++, row.getStatus().getCode());
            statement.execute();
        }
    }

    public Content selectContentById(int id) {
        Log.i(TAG, "selectContentById");
        Content result = null;
        SQLiteDatabase db = null;
        try {

            db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(ContentTable.SELECT_BY_CONTENT_ID, new String[]{id + ""});

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                result = populateContent(cursor, db);
            }
        } finally {
            Log.i(TAG, "selectContentById - trying to close the db connection. Condition : " + (db != null && db.isOpen()));
            if (db != null && db.isOpen())
                db.close(); // Closing database connection
        }

        return result;
    }

    public Content selectContentByStatus(Status status) {
        Log.i(TAG, "selectContentByStatus");
        Content result = null;
        SQLiteDatabase db = null;
        try {

            db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(ContentTable.SELECT_BY_STATUS, new String[]{status.getCode() + ""});

            if (cursor.moveToFirst()) {
                result = populateContent(cursor, db);
            }
        } finally {
            Log.i(TAG, "selectContentByStatus - trying to close the db connection. Condition : " + (db != null && db.isOpen()));
            if (db != null && db.isOpen())
                db.close(); // Closing database connection
        }

        return result;
    }

    public List<Content> selectContentInDownloadManager() {
        Log.i(TAG, "selectContentInDownloadManager");
        List<Content> result = null;
        SQLiteDatabase db = null;
        try {

            db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(ContentTable.SELECT_IN_DOWNLOAD_MANAGER, new String[]{Status.DOWNLOADING.getCode() + "", Status.PAUSED.getCode() + ""});

            if (cursor.moveToFirst()) {
                result = new ArrayList<>();
                do {
                    result.add(populateContent(cursor, db));
                } while (cursor.moveToNext());
            }
        } finally {
            Log.i(TAG, "selectContentInDownloadManager - trying to close the db connection. Condition : " + (db != null && db.isOpen()));
            if (db != null && db.isOpen())
                db.close(); // Closing database connection
        }

        return result;
    }

    public List<Content> selectContentByQuery(String query, boolean orderAlphabetic) {
        return selectContentByQuery(query, 1, -1, orderAlphabetic);
    }

    public List<Content> selectContentByQuery(String query, int page, int qty, boolean orderAlphabetic) {
        Log.i(TAG, "selectContentByQuery");
        List<Content> result = null;
        SQLiteDatabase db = null;
        int start = (page - 1) * qty;
        try {
            query = "%" + query + "%";
            db = this.getWritableDatabase();
            String sql = ContentTable.SELECT_DOWNLOADS;
            if(orderAlphabetic){
                sql += ContentTable.ORDER_ALPHABETIC;
            }else{
                sql += ContentTable.ORDER_BY_DATE;
            }
            Cursor cursor = null;
            if(qty<0){
                cursor = db.rawQuery(sql, new String[]{Status.DOWNLOADED.getCode() + "", Status.ERROR.getCode() + "", Status.MIGRATED.getCode() + "", query, query, AttributeType.ARTIST.getCode() + "", AttributeType.TAG.getCode() + "", AttributeType.SERIE.getCode() + ""});
            }else{
                cursor = db.rawQuery(sql + ContentTable.LIMIT_BY_PAGE, new String[]{Status.DOWNLOADED.getCode() + "", Status.ERROR.getCode() + "", Status.MIGRATED.getCode() + "", query, query, AttributeType.ARTIST.getCode() + "", AttributeType.TAG.getCode() + "", AttributeType.SERIE.getCode() + "", start + "", qty + ""});
            }


            if (cursor.moveToFirst()) {
                result = new ArrayList<>();
                do {
                    result.add(populateContent(cursor, db));
                } while (cursor.moveToNext());
            }
        } finally {
            Log.i(TAG, "selectContentByQuery - trying to close the db connection. Condition : " + (db != null && db.isOpen()));
            if (db != null && db.isOpen())
                db.close(); // Closing database connection
        }

        return result;
    }

    private Content populateContent(Cursor cursor, SQLiteDatabase db) {
        int indexColumn = 3;
        Content content = new Content();
        content.setUrl(cursor.getString(indexColumn++));
        content.setTitle(cursor.getString(indexColumn++));
        content.setHtmlDescription(cursor.getString(indexColumn++));
        content.setQtyPages(cursor.getInt(indexColumn++));
        content.setUploadDate(cursor.getLong(indexColumn++));
        content.setDownloadDate(cursor.getLong(indexColumn++));
        content.setStatus(Status.searchByCode(cursor.getInt(indexColumn++)));
        content.setCoverImageUrl(cursor.getString(indexColumn++));

        content.setImageFiles(selectImageFilesByContentId(db, content.getId()));

        //populate attributes
        List<Attribute> attributes = selectAttributesByContentId(db, content.getId());
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
        Cursor cursor = db.rawQuery(ImageFileTable.SELECT_BY_CONTENT_ID, new String[]{id + ""});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            result = new ArrayList<>();
            do {
                int indexColumn = 2;
                ImageFile item = new ImageFile();
                item.setOrder(cursor.getInt(indexColumn++));
                item.setStatus(Status.searchByCode(cursor.getInt(indexColumn++)));
                item.setUrl(cursor.getString(indexColumn++));
                item.setName(cursor.getString(indexColumn++));
                result.add(item);
            } while (cursor.moveToNext());
        }

        return result;
    }

    private List<Attribute> selectAttributesByContentId(SQLiteDatabase db, int id) {
        List<Attribute> result = null;
        Cursor cursor = db.rawQuery(AttributeTable.SELECT_BY_CONTENT_ID, new String[]{id + ""});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            result = new ArrayList<>();
            do {
                int indexColumn = 1;
                Attribute item = new Attribute();
                item.setUrl(cursor.getString(indexColumn++));
                item.setName(cursor.getString(indexColumn++));
                item.setType(AttributeType.searchByCode(cursor.getInt(indexColumn++)));
                result.add(item);
            } while (cursor.moveToNext());
        }
        return result;
    }

    public void updateImageFileStatus(ImageFile row) {
        Log.i(TAG, "updateImageFileStatus");
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            SQLiteStatement statement = db.compileStatement(ImageFileTable.UPDATE_IMAGE_FILE_STATUS_STATEMENT);
            db.beginTransaction();
            int indexColumn = 1;
            statement.clearBindings();
            statement.bindLong(indexColumn++, row.getStatus().getCode());
            statement.bindLong(indexColumn++, row.getId());
            statement.execute();
            db.setTransactionSuccessful();
            db.endTransaction();
        } finally {
            Log.i(TAG, "updateImageFileStatus - trying to close the db connection. Condition : " + (db != null && db.isOpen()));
            if (db != null && db.isOpen())
                db.close(); // Closing database connection
        }
    }

    private void deleteContent(SQLiteDatabase db, Content content) {
        SQLiteStatement statement = db.compileStatement(ContentTable.DELETE_STATEMENT);
        SQLiteStatement statementImages = db.compileStatement(ImageFileTable.DELETE_STATEMENT);
        SQLiteStatement statementAttributes = db.compileStatement(ContentAttributeTable.DELETE_STATEMENT);
        int indexColumn = 1;
        statement.clearBindings();
        statement.bindLong(indexColumn, content.getId());
        statement.execute();
        statementImages.clearBindings();
        statementImages.bindLong(indexColumn, content.getId());
        statementImages.execute();
        statementAttributes.clearBindings();
        statementAttributes.bindLong(indexColumn, content.getId());
        statementAttributes.execute();
    }

    public void deleteContent(Content content) {
        Log.i(TAG, "deleteContent");
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            SQLiteStatement statement = db.compileStatement(ContentTable.DELETE_STATEMENT);
            SQLiteStatement statementImages = db.compileStatement(ImageFileTable.DELETE_STATEMENT);
            SQLiteStatement statementAttributes = db.compileStatement(ContentAttributeTable.DELETE_STATEMENT);
            db.beginTransaction();
            int indexColumn = 1;
            statement.clearBindings();
            statement.bindLong(indexColumn, content.getId());
            statement.execute();
            statementImages.clearBindings();
            statementImages.bindLong(indexColumn, content.getId());
            statementImages.execute();
            statementAttributes.clearBindings();
            statementAttributes.bindLong(indexColumn, content.getId());
            statementAttributes.execute();
            db.setTransactionSuccessful();
            db.endTransaction();
        } finally {
            Log.i(TAG, "deleteContent - trying to close the db connection. Condition : " + (db != null && db.isOpen()));
            if (db != null && db.isOpen())
                db.close(); // Closing database connection
        }
    }

    public void updateContentStatus(Content row) {
        Log.i(TAG, "updateContentStatus");
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            SQLiteStatement statement = db.compileStatement(ContentTable.UPDATE_CONTENT_DOWNLOAD_DATE_STATUS_STATEMENT);
            db.beginTransaction();
            int indexColumn = 1;
            statement.clearBindings();
            statement.bindLong(indexColumn++, row.getDownloadDate());
            statement.bindLong(indexColumn++, row.getStatus().getCode());
            statement.bindLong(indexColumn++, row.getId());
            statement.execute();
            db.setTransactionSuccessful();
            db.endTransaction();
        } finally {
            Log.i(TAG, "updateContentStatus - trying to close the db connection. Condition : " + (db != null && db.isOpen()));
            if (db != null && db.isOpen())
                db.close(); // Closing database connection
        }
    }

    public void updateContentStatus(Status updateTo, Status updateFrom) {
        Log.i(TAG, "updateContentStatus2");
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            SQLiteStatement statement = db.compileStatement(ContentTable.UPDATE_CONTENT_STATUS_STATEMENT);
            db.beginTransaction();
            int indexColumn = 1;
            statement.clearBindings();
            statement.bindLong(indexColumn++, updateTo.getCode());
            statement.bindLong(indexColumn++, updateFrom.getCode());
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

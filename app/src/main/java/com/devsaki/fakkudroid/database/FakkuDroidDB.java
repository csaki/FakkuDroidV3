package com.devsaki.fakkudroid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

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
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            SQLiteStatement statement = db.compileStatement(Content.INSERT_STATEMENT);
            db.beginTransaction();
            for (Content row : rows) {
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
        Content result = null;
        SQLiteDatabase db = null;
        try {

            db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(ContentTable.SELECT_BY_CONTENT_ID, new String[]{id + ""});

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                int indexColumn = 3;
                result = new Content();
                result.setUrl(cursor.getString(indexColumn++));
                result.setTitle(cursor.getString(indexColumn++));
                result.setHtmlDescription(cursor.getString(indexColumn++));
                result.setQtyPages(cursor.getInt(indexColumn++));
                result.setUploadDate(cursor.getLong(indexColumn++));
                result.setDownloadDate(cursor.getLong(indexColumn++));
                result.setStatus(Status.searchByCode(cursor.getInt(indexColumn++)));
                result.setCoverImageUrl(cursor.getString(indexColumn++));
            }
        } finally {
            if (db != null && db.isOpen())
                db.close(); // Closing database connection
        }

        return result;
    }

    public Content selectContentByStatus(Status status) {
        Content result = null;
        SQLiteDatabase db = null;
        try {

            db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(ContentTable.SELECT_BY_STATUS, new String[]{status.getCode() + ""});

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                int indexColumn = 3;
                result = new Content();
                result.setUrl(cursor.getString(indexColumn++));
                result.setTitle(cursor.getString(indexColumn++));
                result.setHtmlDescription(cursor.getString(indexColumn++));
                result.setQtyPages(cursor.getInt(indexColumn++));
                result.setUploadDate(cursor.getLong(indexColumn++));
                result.setDownloadDate(cursor.getLong(indexColumn++));
                result.setStatus(Status.searchByCode(cursor.getInt(indexColumn++)));
                result.setCoverImageUrl(cursor.getString(indexColumn++));
            }
        } finally {
            if (db != null && db.isOpen())
                db.close(); // Closing database connection
        }

        return result;
    }

    public Content selectContentByQuery(String query) {
        Content result = null;
        SQLiteDatabase db = null;
        try {

            db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(ContentTable.SELECT_DOWNLOADS, new String[]{Status.DOWNLOADED.getCode() + "", Status.ERROR.getCode() + "", query, query});

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                int indexColumn = 3;
                result = new Content();
                result.setUrl(cursor.getString(indexColumn++));
                result.setTitle(cursor.getString(indexColumn++));
                result.setHtmlDescription(cursor.getString(indexColumn++));
                result.setQtyPages(cursor.getInt(indexColumn++));
                result.setUploadDate(cursor.getLong(indexColumn++));
                result.setDownloadDate(cursor.getLong(indexColumn++));
                result.setStatus(Status.searchByCode(cursor.getInt(indexColumn++)));
                result.setCoverImageUrl(cursor.getString(indexColumn++));
            }
        } finally {
            if (db != null && db.isOpen())
                db.close(); // Closing database connection
        }

        return result;
    }

    public List<ImageFile> selectImageFilesByContentId(int id) {
        List<ImageFile> result = new ArrayList<>();
        SQLiteDatabase db = null;
        try {

            db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(ImageFileTable.SELECT_BY_CONTENT_ID, new String[]{id + ""});

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do{
                    int indexColumn = 2;
                    ImageFile item = new ImageFile();
                    item.setOrder(cursor.getInt(indexColumn++));
                    item.setStatus(Status.searchByCode(cursor.getInt(indexColumn++)));
                    item.setUrl(cursor.getString(indexColumn++));
                    item.setName(cursor.getString(indexColumn++));
                    result.add(item);
                }while (cursor.moveToNext());
            }
        } finally {
            if (db != null && db.isOpen())
                db.close(); // Closing database connection
        }

        return result;
    }

    public List<Attribute> selectAttributeByContentId(int id, AttributeType type) {
        List<Attribute> result = new ArrayList<>();
        SQLiteDatabase db = null;
        try {

            db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(AttributeTable.SELECT_BY_CONTENT_ID, new String[]{id + "", type.getCode() + ""});

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do{
                    int indexColumn = 1;
                    Attribute item = new Attribute();
                    item.setUrl(cursor.getString(indexColumn++));
                    item.setName(cursor.getString(indexColumn++));
                    item.setType(AttributeType.searchByCode(cursor.getInt(indexColumn++)));
                    result.add(item);
                }while (cursor.moveToNext());
            }
        } finally {
            if (db != null && db.isOpen())
                db.close(); // Closing database connection
        }

        return result;
    }

    public void updateImageFileStatus(ImageFile row) {
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
            if (db != null && db.isOpen())
                db.close(); // Closing database connection
        }
    }

    public void updateContentStatus(Content row) {
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
            if (db != null && db.isOpen())
                db.close(); // Closing database connection
        }
    }
}

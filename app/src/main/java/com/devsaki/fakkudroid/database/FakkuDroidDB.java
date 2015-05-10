package com.devsaki.fakkudroid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.devsaki.fakkudroid.database.contants.AttributeTable;
import com.devsaki.fakkudroid.database.contants.ContentAttributeTable;
import com.devsaki.fakkudroid.database.contants.ContentTable;
import com.devsaki.fakkudroid.database.domains.Attribute;
import com.devsaki.fakkudroid.database.domains.Content;

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + ContentAttributeTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AttributeTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ContentTable.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public void insertContent(Content row) {
        insertContents(new Content[]{row});
    }

    public void insertContents(Content[] rows) {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(Content.INSERT_STATEMENT);
        db.beginTransaction();
        try {
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
                statement.execute();

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
}

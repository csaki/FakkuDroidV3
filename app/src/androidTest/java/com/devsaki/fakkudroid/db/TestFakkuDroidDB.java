package com.devsaki.fakkudroid.db;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import com.devsaki.fakkudroid.database.FakkuDroidDB;
import com.devsaki.fakkudroid.database.domains.Attribute;
import com.devsaki.fakkudroid.database.domains.Content;
import com.devsaki.fakkudroid.database.enums.AttributeType;
import com.devsaki.fakkudroid.database.enums.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by neko on 15/06/2015.
 */
public class TestFakkuDroidDB extends AndroidTestCase {

    boolean locker1,locker2,locker3,locker4;

    public void testLock(){
        try {
            final List<Content> contents = new ArrayList<>();
            for (int i=0; i<10;i++){
                Content content = new Content();
                List<Attribute> attributes = new ArrayList<>();
                for (int j=0; j<10;j++){
                    Attribute attribute = new Attribute();
                    attribute.setUrl("" + j);
                    attribute.setName("n" + j);
                    attribute.setType(AttributeType.ARTIST);
                    attributes.add(attribute);
                }
                content.setArtists(attributes);
                content.setUrl("/doujinshi/u" + i);
                content.setCoverImageUrl("c" + i);
                content.setDownloadable(false);
                content.setDownloadDate(1000 * i);
                content.setHtmlDescription("html " + i);
                content.setLanguage(attributes.get(i));
                content.setPercent(10.0 * i);
                content.setPublishers(attributes);
                content.setTranslators(attributes);
                content.setUser(attributes.get(i));
                content.setQtyFavorites(i * 11);
                content.setQtyPages(i * 12);
                content.setTitle("t " + i);
                content.setStatus(Status.DOWNLOADED);
                content.setSampleImageUrl("asdas " + i);
                content.setTags(attributes);
                content.setUploadDate(i * 2000);
                contents.add(content);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
                        FakkuDroidDB db = new FakkuDroidDB(context);
                        for (int i = 0; i <100; i++){
                            db.insertContents(contents.toArray(new Content[contents.size()]));
                        }
                    }catch (Exception ex){
                        Log.e("error", "error" , ex);
                    }
                    locker1 = true;
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
                        FakkuDroidDB db = new FakkuDroidDB(context);
                        for (int i = 0; i <100; i++){
                            db.insertContents(contents.toArray(new Content[contents.size()]));
                        }
                    }catch (Exception ex){
                        Log.e("error", "error" , ex);
                    }
                    locker2=true;
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
                        FakkuDroidDB db = new FakkuDroidDB(context);
                        for (int i = 0; i <100; i++){
                            db.selectContentByQuery("", 1, 10, false);
                        }
                    }catch (Exception ex){
                        Log.e("error", "error" , ex);
                    }
                    locker3=true;
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
                        FakkuDroidDB db = new FakkuDroidDB(context);
                        for (int i = 0; i <100; i++){
                            db.selectContentByStatus(Status.DOWNLOADED);
                        }
                    }catch (Exception ex){
                        Log.e("error", "error" , ex);
                    }
                    locker4=true;
                }
            }).start();
            while (!(locker1&&locker2&&locker3&&locker4));
            Log.i("Test DB lock", "Success");
        }catch (Exception ex){
            Log.e("test DB lock", "error", ex);
        }
    }
}

package com.devsaki.fakkudroid.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.devsaki.fakkudroid.database.FakkuDroidDB;
import com.devsaki.fakkudroid.database.domains.Content;
import com.devsaki.fakkudroid.database.domains.ImageFile;
import com.devsaki.fakkudroid.database.enums.Status;
import com.devsaki.fakkudroid.util.Helper;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class DownloadManagerService extends Service {

    private static final String TAG = DownloadManagerService.class.getName();

    private List<Content> contents;
    private FakkuDroidDB db;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

        db = new FakkuDroidDB(this);
    }

    public DownloadManagerService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void updateNotification(Content content, int percent) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    private void startDownload(final Content content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Content content = null;
                while ((content = nextContent()) != null) {
                    Log.i(TAG, "Start Download Content : " + content.getTitle());

                    boolean error = false;
                    //Directory
                    File dir = Helper.getDir(content.getFakkuId(), DownloadManagerService.this);

                    try {
                        //Download Cover Image
                        Helper.saveInStorage(new File(dir, "thumb.jpg"), content.getCoverImageUrl());
                    } catch (Exception e) {
                        Log.e(TAG, "Error Saving cover image " + content.getTitle(), e);
                        error = true;
                    }
                    for (ImageFile imageFile : content.getImageFiles()) {
                        boolean imageFileErrorDownload = false;
                        try {
                            if (imageFile.getStatus() != Status.IGNORED) {
                                Helper.saveInStorage(new File(dir, imageFile.getName()), imageFile.getUrl());
                                Log.i(TAG, "Download Image File (" + imageFile.getName() + ") / " + content.getTitle());
                            }
                        } catch (Exception ex) {
                            Log.e(TAG, "Error Saving Image File (" + imageFile.getName() + ") " + content.getTitle(), ex);
                            error = true;
                        }
                        if (imageFileErrorDownload) {
                            imageFile.setStatus(Status.ERROR);
                        } else {
                            imageFile.setStatus(Status.DOWNLOADED);
                        }
                        db.updateImageFileStatus(imageFile);
                    }
                    db.updateContentStatus(content);
                    //Save JSON file
                    try {
                        Helper.saveJson(content, dir);
                    } catch (IOException e) {
                        Log.e(TAG, "Error Save JSON " + content.getTitle(), e);
                        error = true;
                    }
                    content.setDownloadDate(new Date().getTime());
                    if (error) {
                        content.setStatus(Status.DOWNLOADED);
                    } else {
                        content.setStatus(Status.ERROR);
                    }
                    db.updateContentStatus(content);
                    Log.i(TAG, "Finish Download Content : " + content.getTitle());
                }
                stopSelf();
            }
        }).start();
    }

    private Content nextContent() {
        if (contents == null)
            return null;

        for (Content content : contents) {
            if (content.getStatus() == Status.DOWNLOADING) {
                return content;
            }
        }
        return null;
    }
}

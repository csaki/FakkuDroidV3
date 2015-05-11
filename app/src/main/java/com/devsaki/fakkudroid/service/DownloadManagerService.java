package com.devsaki.fakkudroid.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.devsaki.fakkudroid.ContentListActivity;
import com.devsaki.fakkudroid.DownloadManagerActivity;
import com.devsaki.fakkudroid.MainActivity;
import com.devsaki.fakkudroid.R;
import com.devsaki.fakkudroid.database.FakkuDroidDB;
import com.devsaki.fakkudroid.database.domains.Content;
import com.devsaki.fakkudroid.database.domains.ImageFile;
import com.devsaki.fakkudroid.database.enums.Status;
import com.devsaki.fakkudroid.util.Helper;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class DownloadManagerService extends IntentService {
    private NotificationManager notificationManager;
    NotificationCompat.Builder mBuilder;

    private static final String TAG = DownloadManagerService.class.getName();
    private FakkuDroidDB db;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

        db = new FakkuDroidDB(this);
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public DownloadManagerService() {
        super(DownloadManagerService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int id = intent.getIntExtra("content_id", 0);
        Content content = null;
        if(id==0){
            content = db.selectContentByStatus(Status.DOWNLOADING);
            content.setImageFiles(db.selectImageFilesByContentId(content.getId()));
        }else{
            content = db.selectContentById(id);
            content.setImageFiles(db.selectImageFilesByContentId(content.getId()));
        }
        if(content==null||content.getStatus()==Status.DOWNLOADED||content.getStatus()==Status.ERROR)
            return;

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

        mBuilder = new NotificationCompat.Builder(
                DownloadManagerService.this).setSmallIcon(
                R.drawable.ic_launcher).setContentTitle(content.getTitle());
        showNotification(0, content);
        int count = 0;
        for (ImageFile imageFile : content.getImageFiles()) {
            boolean imageFileErrorDownload = false;
            try {
                if (imageFile.getStatus() != Status.IGNORED) {
                    Helper.saveInStorage(new File(dir, imageFile.getName()), imageFile.getUrl());
                    Log.i(TAG, "Download Image File (" + imageFile.getName() + ") / " + content.getTitle());
                }
                count++;
                showNotification(count*100.0/content.getImageFiles().size(), content);
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
            content.setStatus(Status.ERROR);
        } else {
            content.setStatus(Status.DOWNLOADED);
        }
        db.updateContentStatus(content);
        Log.i(TAG, "Finish Download Content : " + content.getTitle());
        showNotification(0, content);
        content = db.selectContentByStatus(Status.DOWNLOADING);
        if(content!=null){
            Intent intentService = new Intent(Intent.ACTION_SYNC, null, this, DownloadManagerService.class);
            intentService.putExtra("content_id", content.getId());
            startService(intentService);
        }
    }

    private void showNotification(double percent, Content content) {
        Intent resultIntent = null;
        if(content.getStatus()==Status.DOWNLOADED||content.getStatus()==Status.ERROR){
            resultIntent= new Intent(DownloadManagerService.this,
                    ContentListActivity.class);
        }else if(content.getStatus()==Status.DOWNLOADING||content.getStatus()==Status.PAUSED){
            resultIntent= new Intent(DownloadManagerService.this,
                    DownloadManagerActivity.class);
        }else if(content.getStatus()==Status.SAVED){
            resultIntent = new Intent(DownloadManagerService.this,
                    MainActivity.class);
            resultIntent.putExtra("url", content.getUrl());
        }

        // Adds the Intent to the top of the stack
        // Gets a PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent = PendingIntent.getActivity(DownloadManagerService.this,
                0, resultIntent, PendingIntent.FLAG_ONE_SHOT);


        if (content.getStatus()==Status.DOWNLOADING) {
            mBuilder.setContentText(getResources().getString(R.string.downloading)
                    + String.format("%.2f", percent) + "%");
            mBuilder.setProgress(100, (int)percent, false);
        } else {
            int resource = 0;
            if(content.getStatus()==Status.DOWNLOADED){
                resource = R.string.download_completed;
            }else if(content.getStatus()==Status.PAUSED){
                resource = R.string.download_paused;
            }else if(content.getStatus()==Status.SAVED){
                resource = R.string.download_cancelled;
            }else if(content.getStatus()==Status.ERROR){
                resource = R.string.download_error;
            }
            mBuilder.setContentText(getResources().getString(resource));
            mBuilder.setProgress(0, 0, false);
        }
        Notification notif = mBuilder.build();
        notif.contentIntent = resultPendingIntent;
        if (percent > 0)
            notif.flags = Notification.FLAG_ONGOING_EVENT;
        else
            notif.flags = notif.flags | Notification.DEFAULT_LIGHTS
                    | Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(content.getId(), notif);
    }
}

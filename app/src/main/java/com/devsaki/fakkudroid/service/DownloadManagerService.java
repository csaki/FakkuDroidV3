package com.devsaki.fakkudroid.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.devsaki.fakkudroid.database.domains.Content;

public class DownloadManagerService extends Service {

    public DownloadManagerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startDownload(Content content){
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }
}

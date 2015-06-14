package com.devsaki.fakkudroid;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.devsaki.fakkudroid.database.FakkuDroidDB;
import com.devsaki.fakkudroid.database.enums.Status;
import com.devsaki.fakkudroid.dto.LastVersionDto;
import com.devsaki.fakkudroid.dto.UserRequest;
import com.devsaki.fakkudroid.util.AndroidHelper;
import com.devsaki.fakkudroid.util.ConstantsPreferences;
import com.devsaki.fakkudroid.util.Helper;
import com.devsaki.fakkudroid.util.HttpClientHelper;
import com.devsaki.fakkudroid.util.ImageQuality;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import java.io.File;
import java.net.URL;

/**
 * Created by DevSaki on 20/05/2015.
 */
@ReportsCrashes(formUri = "http://devsaki.me:5984/acra-fakkudroid/_design/acra-storage/_update/report",
        httpMethod = HttpSender.Method.PUT,
        reportType = HttpSender.Type.JSON,
        formUriBasicAuthLogin = "reportuser",
        formUriBasicAuthPassword = "\\~hRcq#o?UVtT!7G",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_app)
public class FakkuDroidApplication extends Application {

    private static final String TAG = FakkuDroidApplication.class.getName();
    private LruCache<String, Bitmap> mMemoryCache;
    private SharedPreferences sharedPreferences;
    private LastVersionDto lastVersionDto;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder mBuilder;

    @Override
    public void onCreate() {
        super.onCreate();

        ACRA.init(this);

        AndroidHelper.ignoreSslErros();

        FakkuDroidDB db = new FakkuDroidDB(this);
        db.updateContentStatus(Status.PAUSED, Status.DOWNLOADING);

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        AndroidHelper.executeAsyncTask(new UpdateCheckerTask());
    }


    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (key != null && bitmap != null) {
            if (getBitmapFromMemCache(key) == null) {
                mMemoryCache.put(key, bitmap);
            }
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public void loadBitmap(File file, ImageView mImageView) {
        final String imageKey = file.getAbsolutePath();

        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            mImageView.setImageBitmap(bitmap);
        } else {
            mImageView.setImageResource(R.drawable.ic_fakkudroid_launcher);
            BitmapWorkerTask task = new BitmapWorkerTask(mImageView);
            task.execute(file);
        }
    }

    public LastVersionDto getLastVersionDto() {
        return lastVersionDto;
    }

    class UpdateCheckerTask extends AsyncTask<String, Void, LastVersionDto> {

        @Override
        protected void onPreExecute() {
            FakkuDroidApplication.this.lastVersionDto = null;
        }

        @Override
        protected LastVersionDto doInBackground(String... params) {
            try {
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                UserRequest userRequest = new UserRequest();
                String androidId = Settings.Secure.getString(FakkuDroidApplication.this.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                userRequest.setIdDevice(androidId);
                userRequest.setManufacturer(Build.MANUFACTURER);
                userRequest.setModel(Build.MODEL);
                userRequest.setAppVersionCode(pInfo.versionCode);
                userRequest.setAppVersionName(pInfo.versionName);

                return HttpClientHelper.checkLastVersion(userRequest);
            } catch (Exception ex) {
                Log.e(TAG, "update checker asynctask", ex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(LastVersionDto result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

                    if (result.getLastVersionCode() > pInfo.versionCode) {
                        FakkuDroidApplication.this.lastVersionDto = result;
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(lastVersionDto.getDocumentationLink()));
                        PendingIntent resultPendingIntent = PendingIntent.getActivity(FakkuDroidApplication.this,
                                0, intent, PendingIntent.FLAG_ONE_SHOT);
                        mBuilder = new NotificationCompat.Builder(
                                FakkuDroidApplication.this).setSmallIcon(
                                R.drawable.ic_fakkudroid_launcher).setContentTitle(getString(R.string.new_version_available));
                        mBuilder.setContentText(getString(R.string.version_number).replace("@oldVersion",pInfo.versionName).replace("@newVersion", lastVersionDto.getLastVersionName()));
                        mBuilder.setProgress(0, 0, false);
                        Notification notif = mBuilder.build();
                        notif.contentIntent = resultPendingIntent;
                        notif.flags = notif.flags | Notification.DEFAULT_LIGHTS
                                | Notification.FLAG_AUTO_CANCEL;

                        notificationManager.notify(0, notif);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(TAG, "update checker asynctask - onpost ", e);
                }
            }
        }
    }

    class BitmapWorkerTask extends AsyncTask<File, Void, Bitmap> {

        private ImageView imageView;

        public BitmapWorkerTask(ImageView imageView) {
            this.imageView = imageView;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(File... params) {
            if (params[0].exists()) {
                String imageQualityPref = sharedPreferences.getString(ConstantsPreferences.PREF_QUALITY_IMAGE_LISTS, ConstantsPreferences.PREF_QUALITY_IMAGE_DEFAULT);
                ImageQuality imageQuality = ImageQuality.LOW;
                switch (imageQualityPref) {
                    case "Medium":
                        imageQuality = ImageQuality.MEDIUM;
                        break;
                    case "High":
                        imageQuality = ImageQuality.HIGH;
                        break;
                    case "Low":
                        imageQuality = ImageQuality.LOW;
                        break;
                }

                Bitmap thumbBitmap = Helper.decodeSampledBitmapFromFile(
                        params[0].getAbsolutePath(), imageQuality.getWidth(),
                        imageQuality.getHeight());
                addBitmapToMemoryCache(params[0].getAbsolutePath(), thumbBitmap);
                return thumbBitmap;
            } else
                return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(R.drawable.ic_fakkudroid_launcher);
            }
        }
    }
}

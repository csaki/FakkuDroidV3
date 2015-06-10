package com.devsaki.fakkudroid.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by DevSaki on 10/05/2015.
 */
public class Helper {

    public static File getDownloadDir(String dir, Context context) {
        File file = null;
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String settingDir = prefs.getString(Constants.SETTINGS_FAKKUDROID_FOLDER, "");
        if (settingDir.isEmpty()) {
            return getDefaultDir(Constants.DEFAULT_DOWNLOAD_LOCAL_DIRECTORY + "/" + dir, context);
        }
        file = new File(settingDir, Constants.DEFAULT_DOWNLOAD_LOCAL_DIRECTORY + "/" + dir);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                file = new File(settingDir + Constants.DEFAULT_DOWNLOAD_LOCAL_DIRECTORY + "/" + dir);
                if (!file.exists()) {
                    file.mkdirs();
                }
            }
        }
        return file;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromFile(String file, int reqWidth,
                                                     int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file, options);
    }

    public static File getDefaultDir(String dir, Context context) {
        File file = null;
        try {
            file = new File(Environment.getExternalStorageDirectory()
                    + Constants.DEFAULT_LOCAL_DIRECTORY + "/" + dir);
        } catch (Exception e) {
            file = context.getDir("", Context.MODE_WORLD_WRITEABLE);
            file = new File(file, Constants.DEFAULT_LOCAL_DIRECTORY);
        }

        if (!file.exists()) {
            if (!file.mkdirs()) {
                file = context.getDir("", Context.MODE_WORLD_WRITEABLE);
                file = new File(file, Constants.DEFAULT_LOCAL_DIRECTORY + "/" + dir);
                if (!file.exists()) {
                    file.mkdirs();
                }
            }
        }
        return file;
    }

    public static String escapeURL(String link) {
        try {
            String path = link;
            path = java.net.URLEncoder.encode(path, "utf8");
            path = path.replace("%3A", ":");
            path = path.replace("%2F", "/");
            path = path.replace("+", "%20");
            path = path.replace("%23", "#");
            path = path.replace("%3D", "=");
            return path;
        } catch (Exception e) {
            link = link.replaceAll("\\[", "%5B");
            link = link.replaceAll("\\]", "%5D");
            link = link.replaceAll("\\s", "%20");
        }
        return link;
    }

    public static <K> void saveJson(K object, File dir)
            throws IOException {
        File file = new File(dir, Constants.JSON_FILE_NAME);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        // convert java object to JSON format,
        // and returned as JSON formatted string
        String json = gson.toJson(object);
        FileWriter writer = new FileWriter(file, false);
        writer.write(json);
        writer.close();
    }

    public static String readTextFile(File f) throws IOException {
        BufferedReader br = null;
        String json = "";
        try {

            String sCurrentLine;
            br = new BufferedReader(new FileReader(f));

            while ((sCurrentLine = br.readLine()) != null) {
                json+=sCurrentLine;
            }

        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {}
        }
        return json;
    }

    public static void saveInStorage(File file, String imageUrl)
            throws Exception {

        imageUrl = Helper.escapeURL(imageUrl);

        OutputStream output = null;
        InputStream input = null;

        try {
            if (!file.exists()) {
                final int BUFFER_SIZE = 23 * 1024;

                URL url = new URL(imageUrl);

                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                CookieManager cookieManager = (CookieManager) CookieHandler.getDefault();
                if(cookieManager.getCookieStore().getCookies().size() > 0)
                {
                    connection.setRequestProperty("Cookie",
                            TextUtils.join(",", cookieManager.getCookieStore().getCookies()));
                }
                connection.setDoInput(true);

                connection.connect();

                input = new BufferedInputStream(url.openStream(), BUFFER_SIZE);

                output = new FileOutputStream(file);

                byte data[] = new byte[BUFFER_SIZE];
                int count;
                while ((count = input.read(data, 0, BUFFER_SIZE)) != -1) {
                    output.write(data, 0, count);
                }
                output.flush();
            }
        } catch (Exception e) {
            if (file.exists()) {
                file.delete();
            }
            throw e;
        } finally {
            if (output != null) {
                output.close();
            }
            if (input != null) {
                input.close();
            }
        }
    }
}

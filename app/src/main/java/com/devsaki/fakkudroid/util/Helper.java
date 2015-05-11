package com.devsaki.fakkudroid.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by DevSaki on 10/05/2015.
 */
public class Helper {

    public static File getDir(String dir, Context context) {
        File file = null;
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String settingDir = prefs.getString(Constants.SETTINGS_DIR, "");
        if(settingDir.isEmpty()){
            return getDefaultDir(dir, context);
        }
        if (!file.exists()) {
            if(!file.mkdirs()){
                file = context.getDir("", Context.MODE_WORLD_WRITEABLE);
                file = new File(settingDir + "/" + dir);
                if (!file.exists()) {
                    file.mkdirs();
                }
            }
        }
        return file;
    }

    public static File getDefaultDir(String dir, Context context){
        File file = null;
        try {
            file = new File(Environment.getExternalStorageDirectory()
                    + Constants.LOCAL_DIRECTORY + "/" + dir);
        }catch (Exception e){
            file = context.getDir("", Context.MODE_WORLD_WRITEABLE);
            file = new File(file, Constants.LOCAL_DIRECTORY);
        }

        if (!file.exists()) {
            if(!file.mkdirs()){
                file = context.getDir("", Context.MODE_WORLD_WRITEABLE);
                file = new File(file, Constants.LOCAL_DIRECTORY + "/" + dir);
                if (!file.exists()) {
                    file.mkdirs();
                }
            }
        }
        return file;
    }

    public static String escapeURL(String link) {
        try{
            String path = link;
            path = java.net.URLEncoder.encode(path, "utf8");
            path = path.replace("%3A",":");
            path = path.replace("%2F","/");
            path = path.replace("+","%20");
            path = path.replace("%23","#");
            path = path.replace("%3D","=");
            return path;
        }catch(Exception e){
            link = link.replaceAll("\\[", "%5B");
            link = link.replaceAll("\\]", "%5D");
            link = link.replaceAll("\\s", "%20");
        }
        return link;
    }

    public static <K> void saveJson(K object, File dir)
            throws IOException {
        File file = new File(dir, "content.json");

        if (!file.exists()) {
            Gson gson = new Gson();
            // convert java object to JSON format,
            // and returned as JSON formatted string
            String json = gson.toJson(object);
            FileWriter writer = new FileWriter(file);
            writer.write(json);
            writer.close();
        }
    }

    public static void ignoreSslErros(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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

                URLConnection connection = url.openConnection();
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

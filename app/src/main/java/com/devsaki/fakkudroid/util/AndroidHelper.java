package com.devsaki.fakkudroid.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.widget.Toast;

import com.devsaki.fakkudroid.R;

import java.io.File;

/**
 * Created by DevSaki on 20/05/2015.
 */
public class AndroidHelper {

    public static void openFile(File aFile, Context context) {
        Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW);
        File file = new File(aFile.getAbsolutePath());
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        myIntent.setDataAndType(Uri.fromFile(file), mimetype);

        context.startActivity(myIntent);
    }

    public static void openPerfectViewer(File firstImage, Context context) {
        try {
            Intent intent = context
                    .getPackageManager()
                    .getLaunchIntentForPackage("com.rookiestudio.perfectviewer");
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(firstImage), "image/*");
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, R.string.error_open_perfect_viewer,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static <T> void executeAsyncTask(AsyncTask<T, ?, ?> task,
                                            T... params) {
        task.execute(params);
    }

    public static void ignoreSslErros() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}

package net.fakku.api;

import android.test.AndroidTestCase;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by neko on 25/04/2015.
 */
public class FakkuClientTest extends AndroidTestCase{

    public void testCallIndex(){
        String tag = "Test Call Index";
        Log.d(tag, "Start test");
        for(int i = 1; i<=10; i++)
        try {
            Log.d(tag, "Page : " + i);
            Log.d(tag, FakkuClient.callApiIndex(i, false).toString());
        } catch (IOException e) {
            Log.e(tag, e.getMessage());
        } catch (JSONException e) {
            Log.e(tag, e.getMessage());
        }
    }
}

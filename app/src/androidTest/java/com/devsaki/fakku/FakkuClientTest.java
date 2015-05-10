package com.devsaki.fakku;

import android.test.AndroidTestCase;
import android.util.Log;

import com.devsaki.fakku.dto.Content;
import com.devsaki.fakku.exception.FakkuException;
import java.util.List;

/**
 * Created by DevSaki on 09/05/2015.
 */
public class FakkuClientTest extends AndroidTestCase {

    public void testCallIndex(){
        String tag = "Test Call Index";
        Log.d(tag, "Start test testCallIndex");
        for(int i = 1; i<=1; i++)
            try {
                Log.d(tag, "Page : " + i);
                List<Content> contents = FakkuClient.callIndex(i, true);
                for (Content content : contents){
                    Log.d(tag, content.toString());
                }
                Log.d(tag, "--------------------------------------------------");
            } catch (FakkuException e) {
                Log.e(tag, e.getMessage(), e);
            }
    }
}

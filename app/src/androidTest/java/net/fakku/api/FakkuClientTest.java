package net.fakku.api;

import android.test.AndroidTestCase;
import android.util.Log;

import net.fakku.api.dto.conteiners.ContentConteinerDto;
import net.fakku.api.exceptions.FakkuApiException;

/**
 * Created by DevSaki on 17/05/2015.
 */
public class FakkuClientTest extends AndroidTestCase{

    private static final String TAG = FakkuClientTest.class.getName();

    public void testCallIndex(){
        try {
            ContentConteinerDto conteinerDto = FakkuClient.callContent("manga", "otoshigoro-english");
            Log.i(TAG, conteinerDto.toString());
        } catch (FakkuApiException e) {
            Log.e(TAG, "testCallIndex", e);
        }
    }
}

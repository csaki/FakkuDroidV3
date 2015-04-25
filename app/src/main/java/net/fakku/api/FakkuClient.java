package net.fakku.api;

import android.net.Uri;

import net.fakku.api.documents.Content;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by neko on 24/04/2015.
 */
public class FakkuClient {

    private static final String PROTOCOL_HTTP = "http://";
    private static final String PROTOCOL_HTTPS = "https://";
    private static final String PATH_PAGE = "page";
    private static final String URL_API_INDEX = "api.fakku.net/index";


    private static String callRest(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setConnectTimeout(10000);
        urlConnection.connect();

        // Read the input stream into a String
        InputStream inputStream = urlConnection.getInputStream();
        StringBuffer buffer = new StringBuffer();
        if (inputStream == null) {
            // Nothing to do.
            return null;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }

        if (buffer.length() == 0) {
            // Stream was empty.  No point in parsing.
            return null;
        }

        return buffer.toString();
    }

    public static List<Content> callApiIndex(long page, boolean https) throws IOException, JSONException {
        String url = (https ? PROTOCOL_HTTPS : PROTOCOL_HTTP) + URL_API_INDEX;

        final String OWM_LIST = "index";

        Uri builtUri = Uri.parse(url).buildUpon().appendPath(PATH_PAGE).appendPath("" + page)
                .build();
        String json = callRest(new URL(builtUri.toString()));

        if(json!=null){
            JSONObject fakkuJson = new JSONObject(json);
            return FakkuParser.parseListContent(fakkuJson.getJSONArray(OWM_LIST));
        }

        return null;
    }
}

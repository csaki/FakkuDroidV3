package com.devsaki.fakku;

import android.net.Uri;

import com.devsaki.fakku.dto.Content;
import com.devsaki.fakku.exception.FakkuException;
import com.devsaki.fakku.parser.FakkuParser;
import com.devsaki.util.android.ClientHttp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.util.List;

/**
 * Created by DevSaki on 09/05/2015.
 */
public class FakkuClient {

    private static final String PROTOCOL_HTTP = "http://";
    private static final String PROTOCOL_HTTPS = "https://";
    private static final String URL_INDEX = "www.fakku.net";
    private static final String PATH_PAGE = "page";

    public static List<Content> callIndex(int page, boolean https) throws FakkuException{
        List<Content> result = null;

        try {
            String url = (https ? PROTOCOL_HTTPS : PROTOCOL_HTTP) + URL_INDEX;
            URL urlIndex = null;
            if(page>1)
                urlIndex = new URL(Uri.parse(url).buildUpon().appendPath(PATH_PAGE).appendPath("" + page).build().toString());
            else
                urlIndex = new URL(url);
            String html = ClientHttp.callHttpRequest(urlIndex);

            result = FakkuParser.parseListContents(html);
        }catch (Exception e){
            throw new FakkuException(e);
        }

        return result;
    }

    public static Content callContent(String category, String id, boolean https) throws FakkuException{
        Content result = null;

        try {
            String url = (https ? PROTOCOL_HTTPS : PROTOCOL_HTTP) + URL_INDEX;
            URL urlIndex = new URL(Uri.parse(url).buildUpon().appendPath(category).appendPath(id).build().toString());
            String html = ClientHttp.callHttpRequest(urlIndex);

            result = FakkuParser.parseContent(html);
        }catch (Exception e){
            throw new FakkuException(e);
        }

        return result;
    }
}

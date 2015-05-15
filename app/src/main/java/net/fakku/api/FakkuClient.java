package net.fakku.api;

import com.devsaki.fakkudroid.exceptions.HttpClientException;
import com.devsaki.fakkudroid.util.Constants;
import com.devsaki.fakkudroid.util.HttpClientHelper;
import com.google.gson.Gson;

import net.fakku.api.dto.conteiners.ContentConteinerDto;
import net.fakku.api.dto.single.ContentDto;
import net.fakku.api.dto.single.ErrorMessageDto;
import net.fakku.api.dto.single.PageDto;
import net.fakku.api.exceptions.FakkuApiException;

import org.json.JSONException;
import org.json.JSONObject;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by DevSaki on 15/05/2015.
 */
public class FakkuClient {

    public static ContentConteinerDto callContent(String category, String idContent) throws FakkuApiException{
        ContentConteinerDto result = null;

        String json = null;
        try {
            json = HttpClientHelper.call(new URL(Constants.FAKKU_API_URL + "/" + category + "/" + idContent + Constants.FAKKU_READ));
            result = new Gson().fromJson(json, ContentConteinerDto.class);
            JSONObject jsonObject = new JSONObject(json);
            if(jsonObject.has("pages")){
                result.setPages(new ArrayList<PageDto>());
                JSONObject pages = jsonObject.getJSONObject("pages");
                Iterator<String> iter = jsonObject.keys();

                while (iter.hasNext()) {
                    String key = iter.next();
                    JSONObject page = pages.getJSONObject(key);
                    PageDto pageDto = new PageDto();
                    pageDto.setPage(Integer.parseInt(key));
                    pageDto.setPageContent(new PageDto.PageContent(page.getString("thumb"), page.getString("image")));

                    result.getPages().add(pageDto);
                }
            }
        } catch (HttpClientException e) {
            try {
                new JSONObject(e.getResult());
                throw new FakkuApiException(new Gson().fromJson(e.getResult(), ErrorMessageDto.class), e.getCode());
            } catch (JSONException ex) {
                throw new FakkuApiException(e.getResult(), e.getCode());
            }
        } catch (Exception ex){
            throw new FakkuApiException(ex);
        }

        return result;
    }
}

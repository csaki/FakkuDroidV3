package net.fakku.api;

import net.fakku.api.documents.Attribute;
import net.fakku.api.documents.Content;
import net.fakku.api.documents.Images;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by neko on 24/04/2015.
 */
public class FakkuParser {

    private static final String CONTENT_NAME = "content_name";
    private static final String CONTENT_URL = "content_url";
    private static final String CONTENT_DESCRIPTION = "content_description";
    private static final String CONTENT_LANGUAGE = "content_language";
    private static final String CONTENT_CATEGORY = "content_category";
    private static final String CONTENT_DATE = "content_date";
    private static final String CONTENT_FILESIZE = "content_filesize";
    private static final String CONTENT_FAVORITES = "content_favorites";
    private static final String CONTENT_COMMENTS = "content_comments";
    private static final String CONTENT_PAGES = "content_pages";
    private static final String CONTENT_POSTER = "content_poster";
    private static final String CONTENT_POSTER_URL = "content_poster_url";
    private static final String CONTENT_TAGS = "content_tags";
    private static final String CONTENT_TRANSLATORS = "content_translators";
    private static final String CONTENT_PUBLISHERS = "content_publishers";
    private static final String CONTENT_SERIES = "content_series";
    private static final String CONTENT_ARTISTS = "content_artists";
    private static final String CONTENT_IMAGES = "content_images";

    private static final String IMAGES_COVER = "cover";
    private static final String IMAGES_SAMPLE = "sample";

    private static final String ATTRIBUTE_NAME = "attribute";
    private static final String ATTRIBUTE_LINK = "attribute_link";
    private static final String ATTRIBUTE_ID = "attribute_id";

    public static List<Content> parseListContent(JSONArray listArray) throws JSONException {
        List<Content> result = null;
        if (listArray.length() == 0)
            return null;
        result = new ArrayList<>(listArray.length());

        for (int i = 0; i < listArray.length(); i++) {
            JSONObject object = listArray.getJSONObject(i);
            if (object.has(CONTENT_NAME)) {
                Content content = new Content();
                content.setName(object.getString(CONTENT_NAME));
                content.setArtists(parseListAttribute(object.getJSONArray(CONTENT_ARTISTS)));
                content.setCategory(object.getString(CONTENT_CATEGORY));
                content.setComments(object.getLong(CONTENT_COMMENTS));
                content.setDate(object.getLong(CONTENT_DATE));
                content.setDescription(object.getString(CONTENT_DESCRIPTION));
                content.setUrl(object.getString(CONTENT_URL));
                content.setPosterUrl(object.getString(CONTENT_POSTER_URL));
                content.setLanguage(object.getString(CONTENT_LANGUAGE));
                content.setFilesize(object.getLong(CONTENT_FILESIZE));
                content.setFavorites(object.getLong(CONTENT_FAVORITES));
                content.setPages(object.getLong(CONTENT_PAGES));
                content.setPoster(object.getString(CONTENT_POSTER));
                content.setTags(parseListAttribute(object.getJSONArray(CONTENT_TAGS)));
                if (object.has(CONTENT_TRANSLATORS))
                    content.setTranslators(parseListAttribute(object.getJSONArray(CONTENT_TRANSLATORS)));
                if (object.has(CONTENT_PUBLISHERS))
                    content.setPublishers(parseListAttribute(object.getJSONArray(CONTENT_PUBLISHERS)));
                content.setSeries(parseListAttribute(object.getJSONArray(CONTENT_SERIES)));
                content.setImages(parseImages(object.getJSONObject(CONTENT_IMAGES)));
                result.add(content);
            }
        }
        return result;
    }

    public static List<Attribute> parseListAttribute(JSONArray listArray) throws JSONException {
        List<Attribute> result = null;
        if (listArray.length() == 0)
            return null;
        result = new ArrayList<>(listArray.length());

        for (int i = 0; i < listArray.length(); i++) {

            JSONObject object = listArray.getJSONObject(i);
            Attribute attribute = new Attribute();
            attribute.setId(object.getString(ATTRIBUTE_ID));
            attribute.setName(object.getString(ATTRIBUTE_NAME));
            attribute.setLink(object.getString(ATTRIBUTE_LINK));
            result.add(attribute);
        }
        return result;
    }

    public static Images parseImages(JSONObject object) throws JSONException {
        if (object == null) {
            return null;
        }
        Images result = new Images();
        result.setCover(object.getString(IMAGES_COVER));
        result.setSample(object.getString(IMAGES_SAMPLE));
        return result;
    }
}

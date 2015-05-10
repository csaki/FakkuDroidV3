package com.devsaki.fakku.parser;

import com.devsaki.fakku.dto.Attribute;
import com.devsaki.fakku.dto.Content;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DevSaki on 09/05/2015.
 */
public class FakkuParser {

    public static List<Content> parseListContents(String html){
        Document doc = Jsoup.parse(html);
        Elements contentRow = doc.select(".content-row");
        List<Content> result = null;
        if(contentRow.size()>0){
            result = new ArrayList<>(contentRow.size());
            for (Element content : contentRow){
                result.add(parseContent(content));
            }
        }
        return result;
    }

    public static Content parseContent(String html){
        Content result = null;

        return result;
    }

    private static Content parseContent(Element content){
        Content result = new Content();
        Element contentTitle = content.select(".content-title").first();
        result.setUrl(contentTitle.attr("href"));
        result.setTitle(contentTitle.attr("title"));

        int rowIndex = 1;

        Elements rows = content.select(".row");
        //images
        result.setCoverImageUrl(content.select(".cover").attr("src"));
        result.setSampleImageUrl(content.select(".sample").attr("src"));
        //series
        result.setSerie(parseAttribute(rows.get(rowIndex++).select("a").first()));
        //Artist
        result.setArtists(parseAttributes(rows.get(rowIndex++).select("a")));
        //Publisher or Translator
        if(rows.get(rowIndex).select("div.left").html().equals("Publisher")){
            result.setPublishers(parseAttributes(rows.get(rowIndex++).select("a")));
        }else if(rows.get(rowIndex).select("div.left").html().equals("Translator")){
            result.setTranslators(parseAttributes(rows.get(rowIndex++).select("a")));
        }
        //Language
        result.setLanguage(parseAttribute(rows.get(rowIndex++).select("a").first()));
        //Description
        result.setHtmlDescription(rows.get(rowIndex++).select(".right").html());
        //Tags
        result.setTags(parseAttributes(rows.get(rowIndex++).select("a")));
        return result;
    }

    private static Attribute parseAttribute(Element attribute){
        Attribute result = new Attribute();
        result.setTitle(attribute.html());
        result.setUrl(attribute.attr("href"));
        return result;
    }

    private static List<Attribute> parseAttributes(Elements attributes){
        List<Attribute> result = null;

        if(attributes.size()>0){
            result = new ArrayList<>(attributes.size());
            for (Element attribute : attributes){
                result.add(parseAttribute(attribute));
            }
        }

        return result;
    }
}

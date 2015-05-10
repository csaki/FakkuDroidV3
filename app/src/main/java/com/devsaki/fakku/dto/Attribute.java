package com.devsaki.fakku.dto;

/**
 * Created by DevSaki on 09/05/2015.
 */
public class Attribute {

    private String url;
    private String title;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Attribute{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}

package com.devsaki.fakku.dto;

import java.util.List;

/**
 * Created by DevSaki on 09/05/2015.
 */
public class Content {

    private String url;
    private String title;
    private String htmlDescription;
    private Attribute serie;
    private List<Attribute> artists;
    private List<Attribute> publishers;
    private Attribute language;
    private List<Attribute> tags;
    private List<Attribute> translators;
    private String coverImageUrl;
    private String sampleImageUrl;

    public String getId() {
        return url.substring(url.lastIndexOf("/")+1);
    }

    public String getCategory() {
        return url.substring(1, url.lastIndexOf("/"));
    }

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

    public String getHtmlDescription() {
        return htmlDescription;
    }

    public void setHtmlDescription(String htmlDescription) {
        this.htmlDescription = htmlDescription;
    }

    public Attribute getSerie() {
        return serie;
    }

    public void setSerie(Attribute serie) {
        this.serie = serie;
    }

    public List<Attribute> getArtists() {
        return artists;
    }

    public void setArtists(List<Attribute> artists) {
        this.artists = artists;
    }

    public List<Attribute> getPublishers() {
        return publishers;
    }

    public void setPublishers(List<Attribute> publishers) {
        this.publishers = publishers;
    }

    public Attribute getLanguage() {
        return language;
    }

    public void setLanguage(Attribute language) {
        this.language = language;
    }

    public List<Attribute> getTags() {
        return tags;
    }

    public void setTags(List<Attribute> tags) {
        this.tags = tags;
    }

    public List<Attribute> getTranslators() {
        return translators;
    }

    public void setTranslators(List<Attribute> translators) {
        this.translators = translators;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getSampleImageUrl() {
        return sampleImageUrl;
    }

    public void setSampleImageUrl(String sampleImageUrl) {
        this.sampleImageUrl = sampleImageUrl;
    }

    @Override
    public String toString() {
        return "Content{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", htmlDescription='" + htmlDescription + '\'' +
                ", serie=" + serie +
                ", artists=" + artists +
                ", publishers=" + publishers +
                ", language=" + language +
                ", tags=" + tags +
                ", translators=" + translators +
                ", coverImageUrl='" + coverImageUrl + '\'' +
                ", sampleImageUrl='" + sampleImageUrl + '\'' +
                '}';
    }
}

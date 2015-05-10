package com.devsaki.fakkudroid.database.domains;

import com.devsaki.fakkudroid.database.contants.ContentTable;
import com.devsaki.fakkudroid.database.enums.Status;
import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by DevSaki on 09/05/2015.
 */
public class Content extends ContentTable{

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
    @Expose(serialize = false, deserialize = false)
    private String sampleImageUrl;
    private Integer qtyPages;
    @Expose(serialize = false, deserialize = false)
    private Integer qtyFavorites;
    private long uploadDate;
    private Attribute user;
    private long downloadDate;
    private Status status;
    private List<ImageFile> imageFiles;

    public Integer getId() {
        return url.hashCode();
    }

    public String getFakkuId() {
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

    public Integer getQtyPages() {
        return qtyPages;
    }

    public void setQtyPages(Integer qtyPages) {
        this.qtyPages = qtyPages;
    }

    public Integer getQtyFavorites() {
        return qtyFavorites;
    }

    public void setQtyFavorites(Integer qtyFavorites) {
        this.qtyFavorites = qtyFavorites;
    }

    public Attribute getUser() {
        return user;
    }

    public void setUser(Attribute user) {
        this.user = user;
    }

    public long getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(long uploadDate) {
        this.uploadDate = uploadDate;
    }

    public long getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(long downloadDate) {
        this.downloadDate = downloadDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<ImageFile> getImageFiles() {
        return imageFiles;
    }

    public void setImageFiles(List<ImageFile> imageFiles) {
        this.imageFiles = imageFiles;
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

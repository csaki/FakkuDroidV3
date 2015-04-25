package net.fakku.api.documents;

import java.util.List;

/**
 * Created by neko on 24/04/2015.
 */
public class Content {

    private String name;
    private String url;
    private String description;
    private String language;
    private String category;
    private Long date;
    private Long filesize;
    private Long favorites;
    private Long comments;
    private Long pages;
    private String poster;
    private String posterUrl;
    private List<Attribute> tags;
    private List<Attribute> translators;
    private List<Attribute> publishers;
    private List<Attribute> series;
    private List<Attribute> artists;
    private Images images;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getFilesize() {
        return filesize;
    }

    public void setFilesize(Long filesize) {
        this.filesize = filesize;
    }

    public Long getFavorites() {
        return favorites;
    }

    public void setFavorites(Long favorites) {
        this.favorites = favorites;
    }

    public Long getComments() {
        return comments;
    }

    public void setComments(Long comments) {
        this.comments = comments;
    }

    public Long getPages() {
        return pages;
    }

    public void setPages(Long pages) {
        this.pages = pages;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
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

    public List<Attribute> getPublishers() {
        return publishers;
    }

    public void setPublishers(List<Attribute> publishers) {
        this.publishers = publishers;
    }

    public List<Attribute> getSeries() {
        return series;
    }

    public void setSeries(List<Attribute> series) {
        this.series = series;
    }

    public List<Attribute> getArtists() {
        return artists;
    }

    public void setArtists(List<Attribute> artists) {
        this.artists = artists;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "Content{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                ", language='" + language + '\'' +
                ", category='" + category + '\'' +
                ", date=" + date +
                ", filesize=" + filesize +
                ", favorites=" + favorites +
                ", comments=" + comments +
                ", pages=" + pages +
                ", poster='" + poster + '\'' +
                ", posterUrl='" + posterUrl + '\'' +
                ", tags=" + tags +
                ", translators=" + translators +
                ", publishers=" + publishers +
                ", series=" + series +
                ", artists=" + artists +
                ", images=" + images +
                '}';
    }
}

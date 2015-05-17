package net.fakku.api.dto.single;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DevSaki on 15/05/2015.
 */
public class ContentDto {

    @SerializedName("content_name")
    private String name;
    @SerializedName("content_url")
    private String url;
    @SerializedName("content_description")
    private String description;
    @SerializedName("content_language")
    private String language;
    @SerializedName("content_category")
    private String category;
    @SerializedName("content_date")
    private Long date;
    @SerializedName("content_filesize")
    private Long filesize;
    @SerializedName("content_favorites")
    private Long favorites;
    @SerializedName("content_comments")
    private Long comments;
    @SerializedName("content_pages")
    private Long pages;
    @SerializedName("content_poster")
    private String poster;
    @SerializedName("content_poster_url")
    private String posterUrl;
    @SerializedName("content_tags")
    private List<AttributeDto> tags;
    @SerializedName("content_translators")
    private List<AttributeDto> translators;
    @SerializedName("content_publishers")
    private List<AttributeDto> publishers;
    @SerializedName("content_series")
    private List<AttributeDto> series;
    @SerializedName("content_artists")
    private List<AttributeDto> artists;
    @SerializedName("content_images")
    private ImagesDto images;

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

    public List<AttributeDto> getTags() {
        return tags;
    }

    public void setTags(List<AttributeDto> tags) {
        this.tags = tags;
    }

    public List<AttributeDto> getTranslators() {
        return translators;
    }

    public void setTranslators(List<AttributeDto> translators) {
        this.translators = translators;
    }

    public List<AttributeDto> getPublishers() {
        return publishers;
    }

    public void setPublishers(List<AttributeDto> publishers) {
        this.publishers = publishers;
    }

    public List<AttributeDto> getSeries() {
        return series;
    }

    public void setSeries(List<AttributeDto> series) {
        this.series = series;
    }

    public List<AttributeDto> getArtists() {
        return artists;
    }

    public void setArtists(List<AttributeDto> artists) {
        this.artists = artists;
    }

    public ImagesDto getImages() {
        return images;
    }

    public void setImages(ImagesDto images) {
        this.images = images;
    }
}



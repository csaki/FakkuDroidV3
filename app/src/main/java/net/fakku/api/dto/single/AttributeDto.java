package net.fakku.api.dto.single;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DevSaki on 15/05/2015.
 */
public class AttributeDto {

    @SerializedName("attribute")
    private String name;
    @SerializedName("attribute_id")
    private String id;
    @SerializedName("attribute_link")
    private String link;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}

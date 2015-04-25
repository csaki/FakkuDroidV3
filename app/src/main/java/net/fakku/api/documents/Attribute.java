package net.fakku.api.documents;

/**
 * Created by neko on 24/04/2015.
 */
public class Attribute {

    private String name;
    private String id;
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

    @Override
    public String toString() {
        return "Attribute{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}

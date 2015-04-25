package net.fakku.api.documents;

/**
 * Created by neko on 25/04/2015.
 */
public class Images {

    private String cover;
    private String sample;

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    @Override
    public String toString() {
        return "Images{" +
                "cover='" + cover + '\'' +
                ", sample='" + sample + '\'' +
                '}';
    }
}

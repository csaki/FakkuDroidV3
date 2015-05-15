package net.fakku.api.dto.single;

/**
 * Created by DevSaki on 15/05/2015.
 */
public class PageDto {

    private int page;
    private PageContent pageContent;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public PageContent getPageContent() {
        return pageContent;
    }

    public void setPageContent(PageContent pageContent) {
        this.pageContent = pageContent;
    }

    public static class PageContent{
        private String thumb;
        private String image;

        public PageContent(String thumb, String image) {
            this.thumb = thumb;
            this.image = image;
        }

        public PageContent() {
        }

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}

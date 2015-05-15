package net.fakku.api.dto.conteiners;

import com.google.gson.annotations.Expose;

import net.fakku.api.dto.single.ContentDto;
import net.fakku.api.dto.single.PageDto;

import java.util.List;

/**
 * Created by DevSaki on 15/05/2015.
 */
public class ContentConteinerDto {

    private ContentDto content;
    @Expose(serialize = false, deserialize = false)
    private List<PageDto> pages;

    public ContentDto getContent() {
        return content;
    }

    public void setContent(ContentDto content) {
        this.content = content;
    }

    public List<PageDto> getPages() {
        return pages;
    }

    public void setPages(List<PageDto> pages) {
        this.pages = pages;
    }
}

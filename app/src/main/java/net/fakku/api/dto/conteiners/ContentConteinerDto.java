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
    private List<PageDto> pagesDto;

    public ContentDto getContent() {
        return content;
    }

    public void setContent(ContentDto content) {
        this.content = content;
    }

    public List<PageDto> getPagesDto() {
        return pagesDto;
    }

    public void setPagesDto(List<PageDto> pagesDto) {
        this.pagesDto = pagesDto;
    }
}

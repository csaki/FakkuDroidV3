package com.devsaki.fakkudroid.database.domains;

import com.devsaki.fakkudroid.database.contants.ImageFileTable;
import com.devsaki.fakkudroid.database.enums.Status;

/**
 * Created by DevSaki on 10/05/2015.
 */
public class ImageFile extends ImageFileTable{

    private Integer order;
    private String url;
    private String name;
    private Status status;

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

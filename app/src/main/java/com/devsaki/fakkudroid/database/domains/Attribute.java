package com.devsaki.fakkudroid.database.domains;

import com.devsaki.fakkudroid.database.contants.AttributeTable;
import com.devsaki.fakkudroid.database.enums.AttributeType;

/**
 * Created by DevSaki on 09/05/2015.
 */
public class Attribute extends AttributeTable{

    private String url;
    private String name;
    private AttributeType type;

    public Integer getId(){
        return url.hashCode();
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

    public AttributeType getType() {
        return type;
    }

    public void setType(AttributeType type) {
        this.type = type;
    }
}

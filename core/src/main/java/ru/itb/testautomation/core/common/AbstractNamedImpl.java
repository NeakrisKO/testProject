package ru.itb.testautomation.core.common;

import com.google.common.base.Strings;

public class AbstractNamedImpl implements Named, DescriptionAble, IdAble {
    private String name;
    private Object id;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public void setId(Object id) {
        this.id = id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("%s: [%s]", getClass().getSimpleName(), Strings.nullToEmpty(name));
    }
}

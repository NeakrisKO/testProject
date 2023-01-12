package ru.itb.web.model.ui;

import java.util.List;

public class UIObjectNode {
    private String type;
    private String id;
    private String name;
    private String description;
    private Integer childCount;
    private List<UIObjectNode> child;

    public UIObjectNode() {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getChildCount() {
        return childCount;
    }

    public void setChildCount(Integer childCount) {
        this.childCount = childCount;
    }

    public List<UIObjectNode> getChild() {
        return child;
    }

    public void setChild(List<UIObjectNode> child) {
        this.child = child;
    }
}

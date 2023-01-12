package ru.itb.web.model.ui;

import java.util.List;

public class UIObject {
    private String id;
    private String name;
    private String description;
    private String status;
    private String startTime;
    private String endTime;
    private UIErrorObject error;
    private List<UIObject> child;

    public UIObject(){}

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public UIErrorObject getError() {
        return error;
    }

    public void setError(UIErrorObject error) {
        this.error = error;
    }

    public List<UIObject> getChild() {
        return child;
    }

    public void setChild(List<UIObject> child) {
        this.child = child;
    }
}

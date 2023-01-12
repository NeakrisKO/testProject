package ru.itb.web.model.ui;

import java.util.List;

public class UITestCase {
    private Integer id;
    private String name;
    private String description;
    private Integer sortOrder;
    private String dataset;
    private List<UIStep> steps;

    public UITestCase(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UIStep> getSteps() {
        return steps;
    }

    public void setSteps(List<UIStep> steps) {
        this.steps = steps;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    //    public UIServerStandart getServer() {
//        return server;
//    }
//
//    public void setServer(UIServerStandart server) {
//        this.server = server;
//    }
}

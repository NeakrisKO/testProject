package ru.itb.web.model.ui;

import java.util.List;

public class UITestCaseGroup {
    private Integer id;
    private String name;
    private String description;
    private Integer sortOrder;
    private List<UITestCase> testCases;

    public UITestCaseGroup() {}

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<UITestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<UITestCase> testCases) {
        this.testCases = testCases;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}

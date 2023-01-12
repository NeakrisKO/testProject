package ru.itb.web.model.ui;

import java.util.List;

public class UIProject {
    private Integer id;
    private String name;
    private String description;
    private UIJiraProfile jiraProfile;
    private UIMailProfile mailProfile;
    private List<UITestCaseGroup> groups;

    public UIProject() {}

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

    public List<UITestCaseGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<UITestCaseGroup> groups) {
        this.groups = groups;
    }

    public UIJiraProfile getJiraProfile() {
        return jiraProfile;
    }

    public void setJiraProfile(UIJiraProfile jiraProfile) {
        this.jiraProfile = jiraProfile;
    }

    public UIMailProfile getMailProfile() {
        return mailProfile;
    }

    public void setMailProfile(UIMailProfile mailProfile) {
        this.mailProfile = mailProfile;
    }
}

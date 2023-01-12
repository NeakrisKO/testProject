package ru.itb.web.model.ui;

public class UIJiraProfile {
    private Integer id;
    private String name;
    private String url;
    private String projectCode;
    private boolean writeError;
    private String description;
    private String userName;
    private String userPassword;
    private String userResponsible;
    private String userWatcher;

    public UIJiraProfile() {
    }

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean getWriteError() {
        return writeError;
    }

    public void setWriteError(boolean writeError) {
        this.writeError = writeError;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserResponsible() {
        return userResponsible;
    }

    public void setUserResponsible(String userResponsible) {
        this.userResponsible = userResponsible;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getUserWatcher() { return userWatcher; }

    public void setUserWatcher(String userWatcher) { this.userWatcher = userWatcher; }
}

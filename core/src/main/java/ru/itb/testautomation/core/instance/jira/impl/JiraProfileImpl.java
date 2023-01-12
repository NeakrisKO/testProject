package ru.itb.testautomation.core.instance.jira.impl;

import ru.itb.testautomation.core.instance.jira.intf.JiraProfile;

import java.time.LocalDateTime;

public class JiraProfileImpl implements JiraProfile {
    private Object id;
    private String name;
    private String description;
    private String userName;
    private String userPassword;
    private String projectCode;
    private String url;
    private String responsibleUser;
    private boolean writeError;
    private LocalDateTime dateCreate;
    private LocalDateTime dateUpdate;
    private String userCreate;
    private String userUpdate;
    private String watcherUser;

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getUserPassword() {
        return userPassword;
    }

    @Override
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    @Override
    public String getProjectCode() {
        return projectCode;
    }

    @Override
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getResponsibleUser() {
        return responsibleUser;
    }

    @Override
    public void setResponsibleUser(String responsibleUser) {
        this.responsibleUser = responsibleUser;
    }

    @Override
    public boolean getWriteError() {
        return writeError;
    }

    @Override
    public void setWriteError(boolean writeError) {
        this.writeError = writeError;
    }

    @Override
    public String getWatcherUser() { return watcherUser; }

    @Override
    public void setWatcherUser(String watcherUser) { this.watcherUser = watcherUser; }

    @Override
    public LocalDateTime getDateCreate() {
        return dateCreate;
    }

    @Override
    public void setDateCreate(LocalDateTime dateTime) {
        this.dateCreate = dateTime;
    }

    @Override
    public LocalDateTime getDateUpdate() {
        return dateUpdate;
    }

    @Override
    public void setDateUpdate(LocalDateTime dateTime) {
        this.dateUpdate = dateTime;
    }

    @Override
    public String getUserCreate() {
        return userCreate;
    }

    @Override
    public void setUserCreate(String userCreate) {
        this.userCreate = userCreate;
    }

    @Override
    public String getUserUpdate() {
        return userUpdate;
    }

    @Override
    public void setUserUpdate(String userUpdate) {
        this.userUpdate = userUpdate;
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
    public Object getId() {
        return id;
    }

    @Override
    public void setId(Object id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}

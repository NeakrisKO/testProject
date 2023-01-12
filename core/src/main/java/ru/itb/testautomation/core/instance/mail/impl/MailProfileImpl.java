package ru.itb.testautomation.core.instance.mail.impl;

import ru.itb.testautomation.core.instance.mail.MailProfile;

import java.time.LocalDateTime;

public class MailProfileImpl implements MailProfile {
    private Object id;
    private String name;
    private String description;
    private String user;
    private String password;
    private String server;
    private LocalDateTime dateCreate;
    private LocalDateTime dateUpdate;
    private String userCreate;
    private String userUpdate;

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public void setUser(String userName) {
        user = userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String userPassword) {
        password = userPassword;
    }

    @Override
    public String getServer() {
        return server;
    }

    @Override
    public void setServer(String url) {
        server = url;
    }

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

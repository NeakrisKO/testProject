package ru.itb.web.model.ui;

public class UILoggedUserInfo {
    private String login;
    private String remodeAddr;
    private String computerName;
    private String user;

    public UILoggedUserInfo(String login, String user, String remodeAddr, String computerName) {
        this.login = login;
        this.user = user;
        this.remodeAddr = remodeAddr;
        this.computerName = computerName;
    }

    public UILoggedUserInfo() {

    }

    public String getLogin() {
        return login;
    }

    public String getRemodeAddr() {
        return remodeAddr;
    }

    public String getComputerName() {
        return computerName;
    }

    public String getUser() {
        return user;
    }
}

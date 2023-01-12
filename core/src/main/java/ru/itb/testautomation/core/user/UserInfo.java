package ru.itb.testautomation.core.user;

public class UserInfo {
    private String firstName;
    private String lastName;
    private String email;
    private String login;

    public UserInfo(String firstName, String lastName, String email, String login) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.login = login;
    }

    public String getLogin() { return login; }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }
}

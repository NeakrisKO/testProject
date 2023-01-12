package ru.itb.web.model.ui;

import ru.itb.testautomation.core.utils.Parameter;
import ru.itb.web.model.ui.user.UIUserProfile;

import java.util.List;

public class UIUserInfo {
    @Parameter(shortName = "id")
    private Integer id;
    @Parameter(shortName = "firstName")
    private String firstName;
    @Parameter(shortName = "lastName")
    private String lastName;
    @Parameter(shortName = "email")
    private String email;
    private String login;
    private String password;
    @Parameter(shortName = "userProfiles", hasConverter = true, converter = "ru.itb.web.converter.UIUserProfileConverter")
    private List<UIUserProfile> userProfiles;

    public UIUserInfo(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() { return login; }

    public void setLogin(String login) { this.login = login; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<UIUserProfile> getUserProfiles() {
        return userProfiles;
    }

    public void setUserProfiles(List<UIUserProfile> userProfiles) {
        this.userProfiles = userProfiles;
    }
}

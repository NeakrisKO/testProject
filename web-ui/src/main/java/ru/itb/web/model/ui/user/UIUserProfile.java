package ru.itb.web.model.ui.user;

import ru.itb.testautomation.core.utils.Parameter;

public class UIUserProfile {
    @Parameter(shortName = "id")
    private Integer id;
    @Parameter(shortName = "role")
    private String role;

    public UIUserProfile() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

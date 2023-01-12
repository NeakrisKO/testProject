package ru.itb.configuration.DAO.jira;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "JIRA_PROFILE")
@DynamicUpdate()
@SelectBeforeUpdate()
public class JiraProfileDAO implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "NAME", nullable = false, length = 128)
    private String name;
    @Column(name = "DESCRIPTION", length = 1024)
    private String description;
    @Column(name = "URL", length = 1024)
    private String url;
    @Column(name = "WRITE_ERROR")
    private Boolean writeError;
    @Column(name = "USER_NAME", length = 128)
    private String userName;
    @Column(name = "USER_PASSWORD", length = 32)
    private String userPassword;
    @Column(name = "USER_RESPONSIBLE", length = 128)
    private String userResponsible;
    @Column(name = "PROJECT_CODE", length = 64)
    private String projectCode;
    @Column(name = "DATE_CREATE")
    private LocalDateTime dateCreate;
    @Column(name = "DATE_UPDATE")
    private LocalDateTime dateUpdate;
    @Column(name = "USER_CREATE")
    private String userCreate;
    @Column(name = "USER_UPDATE")
    private String userUpdate;
    @Column(name = "USER_WATCHER", length = 128)
    private String userWatcher;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getWriteError() {
        return writeError;
    }

    public void setWriteError(Boolean writeError) {
        this.writeError = writeError;
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

    public LocalDateTime getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(LocalDateTime dateCreate) {
        this.dateCreate = dateCreate;
    }

    public LocalDateTime getDateUpdate() {
        return dateUpdate;
    }

    public void setDateUpdate(LocalDateTime dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    public String getUserCreate() {
        return userCreate;
    }

    public void setUserCreate(String userCreate) {
        this.userCreate = userCreate;
    }

    public String getUserUpdate() {
        return userUpdate;
    }

    public void setUserUpdate(String userUpdate) {
        this.userUpdate = userUpdate;
    }

    public String getUserWatcher() {
        return Optional.ofNullable(userWatcher).orElseGet(this::getDefaultUserWatcher);
    }

    public void setUserWatcher(String userWatcher) { this.userWatcher = userWatcher; }

    private String getDefaultUserWatcher() {
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JiraProfileDAO that = (JiraProfileDAO) o;

        if (!Objects.equals(id, that.id)) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JiraProfileDAO [id=" + id + ", name=" + name + ", description=" + description +"]";
    }
}

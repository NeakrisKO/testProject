package ru.itb.configuration.DAO.project;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.SelectBeforeUpdate;
import ru.itb.configuration.DAO.jira.JiraProfileDAO;
import ru.itb.configuration.DAO.mail.MailProfileDAO;
import ru.itb.configuration.DAO.testcase.TestCaseGroupDAO;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "PROJECT")
@DynamicUpdate()
@SelectBeforeUpdate()
public class ProjectDAO implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "NAME", nullable = false, length = 128)
    private String name;
    @Column(name = "DESCRIPTION", length = 1024)
    private String description;
    @OneToOne(cascade = {CascadeType.ALL})
    @Fetch(value = FetchMode.JOIN)
    @JoinColumn(name = "project_id")
    private JiraProfileDAO jiraProfile;
    @OneToOne(cascade = {CascadeType.ALL})
    @Fetch(value = FetchMode.JOIN)
    @JoinColumn(name = "mail_profile_id")
    private MailProfileDAO mailProfile;
    @OneToMany(cascade = {CascadeType.ALL})
    @Fetch(value = FetchMode.JOIN)
    @JoinColumn(name = "project_id")
    @OrderBy(value = "sortOrder ASC ")
    private Set<TestCaseGroupDAO> testcaseGroup = new HashSet<>();
    @Column(name = "DATE_CREATE")
    private LocalDateTime dateCreate;
    @Column(name = "DATE_UPDATE")
    private LocalDateTime dateUpdate;
    @Column(name = "USER_CREATE")
    private String userCreate;
    @Column(name = "USER_UPDATE")
    private String userUpdate;


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

    public Set<TestCaseGroupDAO> getTestcaseGroup() {
        return testcaseGroup;
    }

    public void setTestcaseGroup(Set<TestCaseGroupDAO> testcaseGroup) {
        this.testcaseGroup = testcaseGroup;
    }

    public JiraProfileDAO getJiraProfile() {
        return jiraProfile;
    }

    public void setJiraProfile(JiraProfileDAO jiraProfile) {
        this.jiraProfile = jiraProfile;
    }

    public MailProfileDAO getMailProfile() {
        return mailProfile;
    }

    public void setMailProfile(MailProfileDAO mailProfile) {
        this.mailProfile = mailProfile;
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



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProjectDAO that = (ProjectDAO) o;

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
        return "ProjectDAO [id=" + id + ", name=" + name + ", description=" + description +"]";
    }
}

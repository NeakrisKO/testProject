package ru.itb.configuration.DAO.testcase;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@DynamicUpdate()
@SelectBeforeUpdate()
@Table(name = "TESTCASE_GROUP")
public class TestCaseGroupDAO implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "NAME", nullable = false, length = 128)
    private String name;
    @Column(name = "DESCRIPTION", length = 1024)
    private String description;
    @Column(name = "SORT_ORDER", nullable = false)
    private int sortOrder;
    @OneToMany( cascade = {CascadeType.ALL})
    @Fetch(value = FetchMode.JOIN)
    @JoinColumn(name = "testcase_group_id")
    @OrderBy(value = "sortOrder ASC ")
    private Set<TestCaseDAO> testCases = new HashSet<>();

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

    public Set<TestCaseDAO> getTestCases() {
        return testCases;
    }

    public void setTestCases(Set<TestCaseDAO> testCases) {
        this.testCases = testCases;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestCaseGroupDAO that = (TestCaseGroupDAO) o;

        if (!Objects.equals(id, that.id)) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "TestCaseGroupDAO [id=" + id + ", name=" + name + ", description=" + description +"]";
    }

}

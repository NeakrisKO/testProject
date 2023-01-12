package ru.itb.testautomation.core.instance.project.impl;

import com.google.common.collect.Lists;
import ru.itb.testautomation.core.instance.jira.intf.JiraProfile;
import ru.itb.testautomation.core.instance.mail.MailProfile;
import ru.itb.testautomation.core.instance.project.intf.Project;
import ru.itb.testautomation.core.instance.testcase.intf.TestCaseGroup;

import java.time.LocalDateTime;
import java.util.List;

public class ProjectImpl implements Project {
    private Object id;
    private String name;
    private String description;
    private JiraProfile jiraProfile;
    private MailProfile mailProfile;
    private List<TestCaseGroup> groups = Lists.newLinkedList();
    private LocalDateTime dateCreate;
    private LocalDateTime dateUpdate;
    private String userCreate;
    private String userUpdate;

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
    public List<TestCaseGroup> getGroups() {
        return groups;
    }

    @Override
    public void setGroups(List<TestCaseGroup> groups) {
        this.groups = groups;
    }

    @Override
    public JiraProfile getJiraProfile() {
        return jiraProfile;
    }

    @Override
    public void setJiraProfile(JiraProfile jiraProfile) {
        this.jiraProfile = jiraProfile;
    }

    @Override
    public MailProfile getMailProfile() {
        return mailProfile;
    }

    @Override
    public void setMailProfile(MailProfile mailProfile) {
        this.mailProfile = mailProfile;
    }

    @Override
    public void addGroup(TestCaseGroup group) {
        prepareSortOrder(group);
        groups.add(group);
    }

    private void prepareSortOrder(TestCaseGroup testCaseGroup) {
        int size = groups.size();
        if (size > 0) {
            int lastSortOrder = groups.get(size-1).getSortOrder();
            testCaseGroup.setSortOrder(lastSortOrder + 1);
        } else {
            testCaseGroup.setSortOrder(1);
        }
    }
}

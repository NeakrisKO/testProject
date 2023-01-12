package ru.itb.testautomation.core.instance.testcase.impl;

import ru.itb.testautomation.core.instance.step.intf.Step;
import ru.itb.testautomation.core.instance.testcase.AbstractTestCase;
import ru.itb.testautomation.core.instance.testcase.TestCaseStep;
import ru.itb.testautomation.core.instance.testcase.intf.TestCaseGroup;

import java.time.LocalDateTime;

public class TestCaseGroupImpl extends AbstractTestCase implements TestCaseGroup {
    private Object id;
    private String name;
    private String description;
    private int sortOrder;
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
    public int getSortOrder() {
        return sortOrder;
    }

    @Override
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public void addGroupStep(TestCaseStep step) {
        prepareSortOrder(step);
        this.getSteps().add(step);
    }

    private void prepareSortOrder(TestCaseStep step) {
        int size = this.getSteps().size();
        if (size > 0) {
            int lastSortOrder = ((TestCaseStep) this.getSteps().get(size-1)).getTestCase().getSortOrder();
            step.getTestCase().setSortOrder(lastSortOrder + 1);
        } else {
            step.getTestCase().setSortOrder(1);
        }
    }

    @Override
    public void addStep(Step step) {

    }
}

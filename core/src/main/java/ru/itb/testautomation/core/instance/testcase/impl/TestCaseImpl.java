package ru.itb.testautomation.core.instance.testcase.impl;

import ru.itb.testautomation.core.instance.step.intf.Step;
import ru.itb.testautomation.core.instance.testcase.AbstractTestCase;

import java.time.LocalDateTime;

public class TestCaseImpl extends AbstractTestCase {
    private Object id;
    private String name;
    private String description;
    private int sortOrder;
    private LocalDateTime dateCreate;
    private LocalDateTime dateUpdate;
    private String userCreate;
    private String userUpdate;


    public void addStep(Step step) {
        prepareSortOrder(step);
        getSteps().add(step);
    }

    private void prepareSortOrder(Step step) {
        int size = steps.size();
        if (size > 0) {
            int lastSortOrder = steps.get(size-1).getSortOrder();
            step.setSortOrder(lastSortOrder + 1);
        } else {
            step.setSortOrder(1);
        }
    }

    public boolean removeStep(Object id) {
        boolean result = false;
        for (Step step : this.steps) {
            if (id.toString().equals(step.getId().toString())) {
                result = steps.remove(step);
            }
        }
        return result;
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
    public Object getId() {
        return this.id;
    }

    @Override
    public void setId(Object id) {
        this.id = id;
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
}

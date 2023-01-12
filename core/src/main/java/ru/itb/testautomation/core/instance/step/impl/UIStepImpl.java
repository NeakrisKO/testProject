package ru.itb.testautomation.core.instance.step.impl;

import ru.itb.testautomation.core.instance.Status;
import ru.itb.testautomation.core.instance.action.intf.Action;
import ru.itb.testautomation.core.instance.step.AbstractStep;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UIStepImpl extends AbstractStep {
    private Object id;
    private String name;
    private String description;
    private Integer delay;
    private TimeUnit unit;
    private int sortOrder;
    private String stepType;
    private List<Action> actions = new ArrayList<>();
    private LocalDateTime dateCreate;
    private LocalDateTime dateUpdate;
    private String userCreate;
    private String userUpdate;
    private Status status;

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public Integer getDelay() {
        return delay;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public List<Action> getAction() {
        return actions;
    }

    public void setAction(List<Action> actions) {
        this.actions = actions;
    }

    public void addAction(Action action) {
        prepareSortOrder(action);
        actions.add(action);
    }

    private void prepareSortOrder(Action action) {
        int size = actions.size();
        if (size > 0) {
            int lastSortOrder = actions.get(size-1).getSortOrder();
            action.setSortOrder(lastSortOrder + 1);
        } else {
            action.setSortOrder(1);
        }
    }

    public String getStepType() {
        return stepType;
    }

    public void setStepType(String stepType) {
        this.stepType = stepType;
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

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

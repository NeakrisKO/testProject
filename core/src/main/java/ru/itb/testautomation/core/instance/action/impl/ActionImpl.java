package ru.itb.testautomation.core.instance.action.impl;

import ru.itb.testautomation.core.instance.Status;
import ru.itb.testautomation.core.instance.action.ActionParam;
import ru.itb.testautomation.core.instance.action.intf.Action;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ActionImpl implements Action {
    private Object id;
    private int sortOrder;
    private Integer delay;
    private TimeUnit unit;
    private ActionParam actionParam;
    private LocalDateTime dateCreate;
    private LocalDateTime dateUpdate;
    private String userCreate;
    private String userUpdate;
    private Status status;
    private Date startTime;
    private Date endTime;

    @Override
    public void setActionParam(ActionParam param) {
        actionParam = param;
    }

    @Override
    public ActionParam getActionParam() {
        return actionParam;
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
    public Integer getDelay() {
        return delay;
    }

    @Override
    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    @Override
    public TimeUnit getUnit() {
        return unit;
    }

    @Override
    public void setUnit(TimeUnit unit) {
        this.unit = unit;
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
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}

package ru.itb.testautomation.core.event;

import java.util.Date;

public abstract class Event {
    private Date date;
    private boolean stop = false;

    protected Event() {
        date = new Date();
    }

    public Date getDate() {
        return date;
    }

    public boolean isStopped() {
        return stop;
    }

    public void stop() {
        this.stop = true;
    }
}

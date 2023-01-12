package ru.itb.testautomation.core.instance;

import ru.itb.testautomation.core.instance.context.InstanceContextImpl;

import java.util.Date;

public abstract class AbstractInstance {

    private Status status;
    private Date startTime;
    private Date endTime;
    private final InstanceContextImpl context = new InstanceContextImpl();
    private Throwable error;
    private AbstractInstance parent;

    protected AbstractInstance() {
        status = Status.NOT_STARTED;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status value) {
        this.status = value;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date value) {
        this.startTime = value;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date value) {
        this.endTime = value;
    }

    public InstanceContextImpl getContext() {
        return context;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public boolean isRunning() {
        return Status.IN_PROGRESS.equals(status);
    }

    public boolean isFinished() {
        return Status.FAILED.equals(status) || Status.PASSED.equals(status) || Status.STOPPED.equals(status);
    }

    public AbstractInstance getParent() {
        return parent;
    }

    public void setParent(AbstractInstance parent) {
        this.parent = parent;
    }

}

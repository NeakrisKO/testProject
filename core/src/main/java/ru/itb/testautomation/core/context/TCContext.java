package ru.itb.testautomation.core.context;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import ru.itb.testautomation.core.common.Config;
import ru.itb.testautomation.core.common.IdAble;
import ru.itb.testautomation.core.common.Named;
import ru.itb.testautomation.core.context.event.TCContextEvent;
import ru.itb.testautomation.core.context.impl.JSONContextImpl;
import ru.itb.testautomation.core.event.EventBusProvider;
import ru.itb.testautomation.core.generator.IdGenerator;
import ru.itb.testautomation.core.instance.AbstractContainerInstance;
import ru.itb.testautomation.core.instance.Status;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TCContext extends JSONContextImpl implements Named, IdAble {
    public static final String TC = "tc";
    private String name;
    private Object id;
    private List<AbstractContainerInstance> instances = Lists.newArrayListWithExpectedSize(100);
    private AbstractContainerInstance initiator;
    private Status status = Status.NOT_STARTED;
    private Set<String> renderendContextkeys = Sets.newHashSet();
    private long finishDate;

    private static final String TIME_UNIT_PROPERTY = Config.getConfig().getString("tc.timeout.fail.timeunit", "minutes");

    private static final int TIMEOUT_PROPERTY = Config.getConfig().getInt("tc.timeout.fail", 20);

    {
        TimeUnit timeUnit = TimeUnit.valueOf(TIME_UNIT_PROPERTY.toUpperCase());
        finishDate = timeUnit.toMillis(TIMEOUT_PROPERTY) + java.lang.System.currentTimeMillis();
    }

    public TCContext() {
        name = "Context started at " + SimpleDateFormat.getInstance().format(new Date());
        id = IdGenerator.getInstance().getId();
    }

    public Set<String> getRenderendContextkeys() {
        return renderendContextkeys;
    }

    public void setRenderendContextkeys(Set<String> renderendContextkeys) {
        this.renderendContextkeys = renderendContextkeys;
    }

    public Status getStatus() {
        return status;
    }

    public void start() {
        if (Status.NOT_STARTED.equals(status)) {
            status = Status.IN_PROGRESS;
            EventBusProvider.getInstance().post(new TCContextEvent.Start(this));
        }
    }

    public void stop() {
        status = Status.STOPPED;
        EventBusProvider.getInstance().post(new TCContextEvent.Stop(this));
    }

    public void resume() {
        status = Status.IN_PROGRESS;
        EventBusProvider.getInstance().post(new TCContextEvent.Resume(this));
    }

    public boolean isRunning() {
        return Status.IN_PROGRESS.equals(status);
    }

    public void finish() {
        if (Status.IN_PROGRESS.equals(status)) {
            status = Status.PASSED;
            EventBusProvider.getInstance().post(new TCContextEvent.Finish(this));
        }
    }

    public void fail() {
        if (Status.NOT_STARTED.equals(status) || Status.IN_PROGRESS.equals(status)) {
            status = Status.FAILED;
            EventBusProvider.getInstance().post(new TCContextEvent.Fail(this));
        }
    }

    public void pause() {
        if (Status.IN_PROGRESS.equals(status)) {
            status = Status.PAUSED;
            EventBusProvider.getInstance().post(new TCContextEvent.Pause(this));
        }
    }

    public boolean isFinished() {
        return Status.PASSED.equals(status) || Status.FAILED.equals(status) || Status.STOPPED.equals(status);
    }

    public List<AbstractContainerInstance> getInstances() {
        return instances;
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

    private String generateName() {
        return String.format("Context for %s started at %s", initiator, SimpleDateFormat.getInstance().format(new Date()));
    }

    public AbstractContainerInstance getInitiator() {
        return initiator;
    }

    public void setInitiator(AbstractContainerInstance initiator) {
        this.initiator = initiator;
        name = generateName();
    }


    //TODO need implementation
    @Override
    public boolean equals(Object anObject) {
       return false;
    }

    public long getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(long finishDate) {
        this.finishDate = finishDate;
    }
}

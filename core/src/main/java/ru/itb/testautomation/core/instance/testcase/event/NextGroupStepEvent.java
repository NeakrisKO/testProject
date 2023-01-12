package ru.itb.testautomation.core.instance.testcase.event;

import ru.itb.testautomation.core.context.TCContext;
import ru.itb.testautomation.core.event.Event;

public class NextGroupStepEvent extends Event {
    private  TCContext context;

    public NextGroupStepEvent() {}

    public NextGroupStepEvent(TCContext context) {
        this.context = context;
    }

    public TCContext getContext() {
        return context;
    }

    public void setContext(TCContext context) {
        this.context = context;
    }
}

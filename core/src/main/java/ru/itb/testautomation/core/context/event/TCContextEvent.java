package ru.itb.testautomation.core.context.event;

import ru.itb.testautomation.core.context.TCContext;
import ru.itb.testautomation.core.event.Event;

public abstract class TCContextEvent extends Event {
    private TCContext context;

    public TCContextEvent(TCContext context) {
        this.context = context;
    }

    public TCContext getContext() {
        return context;
    }

    public static class Start extends TCContextEvent {
        public Start(TCContext context) {
            super(context);
        }
    }

    public static class Finish extends TCContextEvent {
        public Finish(TCContext context) {
            super(context);
        }
    }

    public static class Stop extends TCContextEvent {
        public Stop(TCContext context) {
            super(context);
        }
    }

    public static class Fail extends TCContextEvent {

        public Fail(TCContext context) {
            super(context);
        }
    }

    public static class Pause extends TCContextEvent {
        public Pause(TCContext context) {super(context);}
    }

    public static class Resume extends TCContextEvent {
        public Resume(TCContext context) {super(context);}
    }
}

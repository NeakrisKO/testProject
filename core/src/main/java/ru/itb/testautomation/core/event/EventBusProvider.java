package ru.itb.testautomation.core.event;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EventBusProvider {
    private static EventBusProvider instance = new EventBusProvider();
    private final EventBus normalPriorityEventBus = new EventBus(new LoggingSubscriberExceptionHandler("Normal priority bus"));
    private final EventBus highPriorityEventBus = new EventBus(new LoggingSubscriberExceptionHandler("High priority bus"));

    public static EventBusProvider getInstance() {
        return instance;
    }

    public void post(Event event) {
        highPriorityEventBus.post(event);
        normalPriorityEventBus.post(event);
    }

    public void register(Object subscriber) {
        register(subscriber, Priority.HIGH);
    }

    public void register(Object subscriber, Priority priority) {
        if (Priority.HIGH.equals(priority)) {
            highPriorityEventBus.register(subscriber);
        } else {
            normalPriorityEventBus.register(subscriber);
        }
    }

    //TODO add log
    public void unregister(Object subscriber) {
        try {
            highPriorityEventBus.unregister(subscriber);
        } catch (Exception e) {/*its ok*/}
        try {
            normalPriorityEventBus.unregister(subscriber);
        } catch (Exception e) {/*its ok*/}
    }

    private static final class LoggingSubscriberExceptionHandler implements SubscriberExceptionHandler {
        private final Logger LOGGER;
        private String name;

        LoggingSubscriberExceptionHandler(String name) {
            this.name = name;
            LOGGER = LogManager.getLogger(EventBusProvider.class);
        }

        public void handleException(Throwable exception, SubscriberExceptionContext context) {
            String event = String.valueOf(context.getEvent());
            String subscriber = String.valueOf(context.getSubscriber());
            String subscriberMethod = String.valueOf(context.getSubscriberMethod());
            LOGGER.error(String.format("Could not dispatch event %s; %s to %s at bus %s", event, subscriber, subscriberMethod, name), exception);
        }
    }

    public enum Priority {
        NORMAL, HIGH
    }
}

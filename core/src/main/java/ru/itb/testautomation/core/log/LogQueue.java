package ru.itb.testautomation.core.log;

import ru.itb.testautomation.core.log.model.LogEntry;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentLinkedQueue;


public class LogQueue {
    private static volatile LogQueue instance;

    private ConcurrentLinkedQueue<SoftReference<LogEntry>> queue = new ConcurrentLinkedQueue<>();

    public static LogQueue getInstance() {
        LogQueue localInstance = instance;
        if (localInstance == null) {
            synchronized (LogQueue.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new LogQueue();
                }
            }
        }
        return localInstance;
    }

    private LogQueue() {
    }

    public synchronized void add(LogEntry entry) {
        this.queue.add(new SoftReference<>(entry));
    }

    public synchronized LogEntry get() {
        LogEntry obj;
        SoftReference<LogEntry> ref;
        if ((ref = this.queue.poll()) != null) {
            if ((obj = ref.get()) != null) {
                return obj;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return this.queue.isEmpty();
    }
}

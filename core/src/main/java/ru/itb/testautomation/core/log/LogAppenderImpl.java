package ru.itb.testautomation.core.log;

import com.google.common.base.Strings;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import ru.itb.testautomation.core.common.Config;
import ru.itb.testautomation.core.log.model.LogEntry;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Plugin(name="TAPAppender", category="Core", elementType="appender", printObject=true)
public class LogAppenderImpl extends AbstractAppender {
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private static int counter;
    private static final String DATE_FORMAT = Config.getConfig().getString("log.appender.date.format","dd.MM.yy hh:mm:ss");

    private static final ThreadLocal<SimpleDateFormat> simpleDateFormat = ThreadLocal.withInitial(() -> new SimpleDateFormat(DATE_FORMAT));


    protected LogAppenderImpl(String name, Filter filter, Layout<? extends Serializable> layout, final boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    @Override
    public void append(LogEvent event) {
//        readLock.lock();
//        try {
//            addLog(event);
//        } catch (Exception ex) {
//            if (!ignoreExceptions()) {
//                throw new AppenderLoggingException(ex);
//            }
//        } finally {
//            readLock.unlock();
//        }
    }

    private void addLog(LogEvent event) {
        LogEntry entry = new LogEntry();
        entry.setId(String.valueOf(++counter));
        entry.setTime(simpleDateFormat.get().format(event.getTimeMillis()));
        entry.setLevel(event.getLevel().toString());
        entry.setLoggerName(event.getLoggerName());
        entry.setMessage(event.getMessage().getFormattedMessage());
        if (event.getThrown() != null) {
            StringBuilder sb = new StringBuilder(Strings.nullToEmpty(event.getThrown().getMessage()));
            sb.append("\n");
            for (StackTraceElement element : event.getThrown().getStackTrace()) {
                sb.append(element.toString());
                sb.append("\n");
            }
            entry.setStackTrace(sb.toString());
        }
        LogQueue.getInstance().add(entry);
    }

    @PluginFactory
    public static LogAppenderImpl createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter) {
        if (name == null) {
            LOGGER.error("No name provided for LogAppenderImpl");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new LogAppenderImpl(name, filter, layout, true);
    }
}

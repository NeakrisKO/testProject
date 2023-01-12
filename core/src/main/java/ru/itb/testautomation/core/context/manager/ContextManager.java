package ru.itb.testautomation.core.context.manager;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itb.testautomation.core.context.TCContext;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ContextManager {
    private static final Logger LOGGER = LogManager.getLogger(ContextManager.class);
    private static ContextManager instance = new ContextManager();
    private Map<String, TCContext> keyContextMap = Maps.newHashMapWithExpectedSize(1000);
    private Set<TCContext> contexts = Sets.newConcurrentHashSet(); //just to save all context, nothing more

    private ContextManager() {
    }

    public static ContextManager getInstance() {
        return instance;
    }

    public TCContext get(String key) {
        TCContext tcContext = keyContextMap.get(key);
        if (tcContext == null) {
            tcContext = new TCContext();
            tcContext.getRenderendContextkeys().add(key);
            keyContextMap.put(key, tcContext);
            contexts.add(tcContext);
            LOGGER.info("Create new context because - key[{}] not found", key);
        }
        return tcContext;
    }

    public TCContext findById(Object id) {
        for (TCContext entry : contexts) {
            if (entry.getId().toString().equals(id.toString()))
                return entry;
        }
        return null;
    }

    public TCContext findByIdEx(Object id) {
        return keyContextMap.get(id.toString());
    }

    public void put(TCContext context) {
        if (context.getRenderendContextkeys() == null) {
            throw new IllegalArgumentException("Context key cannot be null!");
        }
        for (String key : context.getRenderendContextkeys()) {
            keyContextMap.put(key, context);
        }
        if (!contexts.contains(context)) {
            contexts.add(context);
        }
        keyContextMap.put(context.getId().toString(), context);
    }

    public void remove(Object contextId) {
        keyContextMap.entrySet().removeIf(entry -> contextId.equals(entry.getValue().getId()));
        for (Iterator<TCContext> iter = contexts.iterator(); iter.hasNext(); ) {
            if (iter.next().getId().equals(contextId)) {
                iter.remove();
                break;
            }
        }
    }

    public Set<TCContext> getContexts() {
        return contexts;
    }
}

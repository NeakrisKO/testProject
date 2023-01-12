package ru.itb.testautomation.core.monitor;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itb.testautomation.core.context.TCContext;
import ru.itb.testautomation.core.context.intf.JSONContext;
import ru.itb.testautomation.core.context.manager.ContextManager;
import ru.itb.testautomation.core.instance.AbstractContainerInstance;
import ru.itb.testautomation.core.instance.Status;
import ru.itb.testautomation.core.instance.context.InstanceContextImpl;
import ru.itb.testautomation.core.instance.step.StepInstance;
import ru.itb.testautomation.core.instance.testcase.TestCaseInstance;
import ru.itb.testautomation.core.monitor.model.TestCaseMonitorItem;
import ru.itb.testautomation.core.user.UserInfo;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestCaseMonitor {
    private static final Logger LOGGER = LogManager.getLogger(TestCaseMonitor.class);
    private static volatile TestCaseMonitor instance;
    private Map<String, TestCaseMonitorItem> items = new LinkedHashMap<>();

    private TestCaseMonitor() {
    }

    public static TestCaseMonitor getInstance() {
        TestCaseMonitor localInstance = instance;
        if (localInstance == null) {
            synchronized (TestCaseMonitor.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new TestCaseMonitor();
                }
            }
        }
        return localInstance;
    }

    public Map<String, TestCaseMonitorItem> getItems() {
        return items;
    }

    public TestCaseMonitorItem getItem(String id) {
        return items.get(id);
    }

    public void add(InstanceContextImpl context) {
        if (!items.containsKey(context.tc().getId())) {
            TestCaseMonitorItem item = new TestCaseMonitorItem();
            item.setId(context.tc().getId());
            item.setName(context.tc().getInitiator().getStepContainer().getName());
            item.setInitiator(context.tc().getInitiator().getStepContainer().getName());
            item.setDescription(context.tc().getInitiator().getStepContainer().getDescription());
            item.setStatus(context.tc().getStatus());
            item.setStartTime(new Date(System.currentTimeMillis()));

            if (context.tc().getInitiator() instanceof TestCaseInstance) {
                UserInfo userInfo = ((TestCaseInstance) context.tc().getInitiator()).getUserInfo();
                item.setUserFirstName(userInfo.getFirstName());
                item.setUserLastName(userInfo.getLastName());
                item.setUserEmail(userInfo.getEmail());
                item.setUserLogin(userInfo.getLogin());
            }

            this.items.put(context.tc().getId().toString(), item);
        }
    }

    public void remove(Object id) {
        items.entrySet().removeIf(stringTestCaseMonitorItemEntry -> id.equals(stringTestCaseMonitorItemEntry.getValue().getId()));
    }

    public void updateAll() {
        for (Map.Entry<String, TestCaseMonitorItem> stringTestCaseMonitorItemEntry : items.entrySet()) {
            update(stringTestCaseMonitorItemEntry);
        }
    }

    private void update(Map.Entry<String, TestCaseMonitorItem> next) {
        TCContext context = findContextById(next.getValue().getId().toString());
        next.getValue().setStatus(context.getStatus());
        if (context.getStatus() == Status.FAILED || context.getStatus() == Status.STOPPED || context.getStatus() == Status.PASSED) {
            if (next.getValue().getEndTime() == null) {
                next.getValue().setEndTime(new Date());
            }
        }
    }

    public Set<String> getKeys(String id) {
        TCContext context = findContextById(id);
        if (context != null) {
            return context.getRenderendContextkeys();
        }
        return null;
    }

    public List<AbstractContainerInstance> getProcessInstances(String id) {
        TCContext context = findContextById(id);
        if (context != null) {
            return context.getInstances();
        } else {
            context = findContextByIdEx(id);
            if (context != null) {
                return context.getInstances();
            }
        }
        return null;
    }

    public void addToContext(String id, Object key, Object value) {
        TCContext context = findContextById(id);
        if (context != null) {
            context.put(key, value);
        }
    }

    private TCContext findContextById(String id) {
        return ContextManager.getInstance().findById(id);
    }

    private TCContext findContextByIdEx(String id) {
        return ContextManager.getInstance().findByIdEx(id);
    }

    private StepInstance findStepInstance(TCContext context, String parentId, String id) {
        for (AbstractContainerInstance entry : context.getInstances()) {
            if (checkParentId(entry, parentId)) {
                for (StepInstance stepEntry : entry.getStepInstances()) {
                    if (id.equals(stepEntry.getStep().getId().toString()))
                        return stepEntry;
                }
            }
        }
        return null;
    }

    public Map<Object, Pair<Object, Object>> getHistory(String contextId) {
        TCContext context = findContextById(contextId);
        if (context != null) {
            return context.getHistory();
        }
        return null;
    }

    public JSONContext getVariables(String contextId) {
        TCContext context = findContextById(contextId);
        if (context != null) {
            return context;
        }
        return null;
    }

    public void mergeVariables(String contextId, JSONContext jsonContext) {
        TCContext context = findContextById(contextId);
        if (context != null && jsonContext != null) {
            context.merge(jsonContext);
        }
    }

    public void switchCollectHistoryState(String contextId, boolean state) {
        TCContext context = findContextById(contextId);
        if (context != null) {
            context.setCollectHistory(state);
        }
    }

    public boolean getContextHistoryState(String contextId) {
        TCContext context = findContextById(contextId);
        return context != null && context.isCollectHistory();
    }

    public void setState(String contextId, Status status) {
        TCContext context = findContextById(contextId);
        if (context != null) {
            switch (status) {
                case PAUSED: {
                    context.pause();
                }
                break;
                case STOPPED: {
                    context.stop();
                }
                break;
                case IN_PROGRESS: {
                    if (Status.PAUSED.equals(context.getStatus())) {
                        context.resume();
                        //TODO add logic for resume process
                    }
                }
                break;
                default: {
                    LOGGER.error(String.format("Unexpected state [%s] for context [%s]", status, contextId));
                }
            }
        }
    }

    private boolean checkParentId(AbstractContainerInstance instance, Object id) {
        return id.equals(instance.getStepContainer().getId());
    }

    public void delete(Object id) {
            ContextManager.getInstance().remove(id);
            items.remove(id);
    }
}

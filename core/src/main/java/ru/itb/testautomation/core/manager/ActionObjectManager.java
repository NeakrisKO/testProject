package ru.itb.testautomation.core.manager;

import ru.itb.testautomation.core.common.inject.SpringInjection;
import ru.itb.testautomation.core.manager.intf.ScenarioManagerFactory;
import ru.itb.testautomation.core.manager.intf.ScenarioObjectManager;

public class ActionObjectManager {
    private static final ScenarioManagerFactory factory = SpringInjection.getInstance().getContext().getBean("actionObjectManager", ScenarioManagerFactory.class);
    private static ActionObjectManager manager = new ActionObjectManager();

    public static ActionObjectManager getInstance() {
        return manager;
    }

    public ScenarioObjectManager getManager(Object contextId) {
        return factory.getManager(contextId);
    }

    public ScenarioObjectManager initManager(Object contextId) {
        return factory.initManager(contextId);
    }
}

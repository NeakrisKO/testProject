package ru.itb.testautomation.core.manager;

import ru.itb.testautomation.core.common.inject.SpringInjection;
import ru.itb.testautomation.core.manager.intf.EntityObjectManagerExt;
import ru.itb.testautomation.core.manager.intf.ManagerFactory;

public class CoreObjectManager {
    private static final ManagerFactory factory = SpringInjection.getInstance().getContext().getBean("objectManager", ManagerFactory.class);
    private static CoreObjectManager manager = new CoreObjectManager();

    public static CoreObjectManager getInstance() {
        return manager;
    }

    public void init() {
        factory.init();
    }

    public EntityObjectManagerExt getManager() {
        return factory.getManager();
    }
}

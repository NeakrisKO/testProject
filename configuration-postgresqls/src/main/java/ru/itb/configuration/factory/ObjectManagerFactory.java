package ru.itb.configuration.factory;

import ru.itb.configuration.Manager.*;
import ru.itb.testautomation.core.manager.intf.EntityObjectManagerExt;
import ru.itb.testautomation.core.manager.intf.ManagerFactory;

public class ObjectManagerFactory implements ManagerFactory {
    @Override
    public EntityObjectManagerExt getManager() {
        return new EntityManager();
    }

    @Override
    public void init() {

    }
}

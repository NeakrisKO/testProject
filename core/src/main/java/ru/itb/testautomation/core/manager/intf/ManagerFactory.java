package ru.itb.testautomation.core.manager.intf;

public interface ManagerFactory {
    EntityObjectManagerExt getManager();
    void init();
}

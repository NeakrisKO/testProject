package ru.itb.testautomation.core.manager.intf;

public interface ScenarioManagerFactory {
    ScenarioObjectManager getManager(Object contextId);
    ScenarioObjectManager initManager(Object contextId);
}

package ru.itb.testautomation.core.manager.intf;

import ru.itb.testautomation.core.instance.action.ActionParam;

public interface ScenarioObjectManager {
    void execute(ActionParam param);
    void stop();
}

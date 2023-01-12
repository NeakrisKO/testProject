package ru.itb.testautomation.core.instance.step.intf;

import ru.itb.testautomation.core.instance.step.StepInstance;
import ru.itb.testautomation.core.instance.testcase.TestCaseInstance;

public interface StepExecutor {
    void execute(StepInstance step, TestCaseInstance parentInstance) throws Exception;
}

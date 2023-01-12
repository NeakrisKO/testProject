package ru.itb.testautomation.core.instance.testcase.intf;

import ru.itb.testautomation.core.bobject.BusinessObject;
import ru.itb.testautomation.core.instance.step.intf.StepContainer;
import ru.itb.testautomation.core.instance.testcase.TestCaseStep;

public interface TestCaseGroup extends BusinessObject, StepContainer {
    void addGroupStep(TestCaseStep step);
}

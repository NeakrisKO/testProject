package ru.itb.testautomation.core.instance.testcase.executor.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itb.testautomation.core.event.EventBusProvider;
import ru.itb.testautomation.core.instance.AbstractContainerInstance;
import ru.itb.testautomation.core.instance.Status;
import ru.itb.testautomation.core.instance.step.StepInstance;
import ru.itb.testautomation.core.instance.step.StepIterator;
import ru.itb.testautomation.core.instance.step.intf.StepExecutor;
import ru.itb.testautomation.core.instance.testcase.TestCaseInstance;
import ru.itb.testautomation.core.instance.testcase.TestCaseStep;
import ru.itb.testautomation.core.instance.testcase.event.TestCaseEvent;

import java.util.Date;

public class TestCaseExecutor implements StepExecutor {
    private static final Logger LOGGER = LogManager.getLogger(TestCaseExecutor.class);


    public TestCaseExecutor() {
    }

    @Override
    public void execute(StepInstance step, TestCaseInstance parentInstance) throws Exception {
        LOGGER.debug("execute() from TestCaseExecutor .......");
        TestCaseStep testCaseStep = (TestCaseStep) step.getStep();
        if (testCaseStep.isEnable()) {
            if (testCaseStep.isManualStart()) {
                step.getContext().tc().pause();
            } else
                TestCaseStepExecutor.getInstance().execute(testCaseStep.getTestCase(), step.getContext().tc(), ((TestCaseInstance)step.getParent()).getDataSet(), parentInstance, parentInstance.getUserInfo(), parentInstance.getJiraProfile());
        } else {
            //TODO add skip ability - need implementation
            LOGGER.warn("TestCase step '{}' has been skipped, because it has state 'Is Enable=FALSE'.", testCaseStep.getTestCase().getName());
            checkIfLast(step.getParent(), testCaseStep);
        }
    }

    private void checkIfLast(AbstractContainerInstance instance, TestCaseStep targetStep) {
        if (instance instanceof TestCaseInstance) {
            StepIterator stepIterator = instance.iterator();
            while (stepIterator.hasNext()) {
                TestCaseStep testCaseStep = (TestCaseStep) stepIterator.next().getStep();
                if (targetStep.getTestCase().getName().equals(testCaseStep.getTestCase().getName())) {
                    if (!stepIterator.hasNext()) {
                        if (!Status.FAILED.equals(instance.getStatus()) && Status.IN_PROGRESS.equals(instance.getStatus())) {
                            instance.setStatus(Status.PASSED);
                        }
                        instance.setEndTime(new Date());
                        LOGGER.info("TestCase {} executed", instance);
                        instance.getContext().tc().finish();
                        EventBusProvider.getInstance().post(new TestCaseEvent.Finish((TestCaseInstance) instance));
                    }
                }
            }
        }
    }
}

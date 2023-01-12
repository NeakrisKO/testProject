package ru.itb.testautomation.core.instance.testcase.event;

import com.google.common.eventbus.Subscribe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itb.testautomation.core.context.TCContext;
import ru.itb.testautomation.core.event.EventBusProvider;
import ru.itb.testautomation.core.instance.Status;
import ru.itb.testautomation.core.instance.StepExecutorFactory;
import ru.itb.testautomation.core.instance.step.StepInstance;
import ru.itb.testautomation.core.instance.step.StepIterator;
import ru.itb.testautomation.core.instance.testcase.TestCaseInstance;
import ru.itb.testautomation.core.manager.intf.ScenarioObjectManager;

import java.util.Date;

public class NextTestCaseSubscriberExecutor {
    private static final Logger LOGGER = LogManager.getLogger(NextTestCaseSubscriberExecutor.class);
    private final TCContext context;
    private final TestCaseInstance testCaseInstance;
    private final StepIterator stepIterator;

    public NextTestCaseSubscriberExecutor(TestCaseInstance testCaseInstance) {
        this.context = testCaseInstance.getContext().tc();
        this.testCaseInstance = testCaseInstance;
        this.stepIterator = testCaseInstance.iterator();
    }

    @Subscribe
    public void handle(NextGroupStepEvent event) {
        if (event.getContext().getId().equals(context.getId())) {
            if (context.getStatus().equals(Status.STOPPED)) {
                stooped();
            } else {
                LOGGER.info("Got event to trigger next test case in {} ...", testCaseInstance);
                try {
                    if (stepIterator.hasNext()) {
                        StepInstance stepInstance = stepIterator.next();
                        EventBusProvider.getInstance().register(new FireNextTestCaseSubscriber(context, stepIterator), EventBusProvider.Priority.HIGH);
                        StepExecutorFactory.getInstance().getExecutor(stepInstance).execute(stepInstance, testCaseInstance);
                    } else {
                        testCaseInstance.setStatus(Status.PASSED);
                        testCaseInstance.setEndTime(new Date());
                        LOGGER.info("Test case Group {} executed", testCaseInstance);
                        context.finish();
                        EventBusProvider.getInstance().post(new TestCaseGroupEvent.Finish(testCaseInstance));
                        ScenarioObjectManager scenarioObjectManager = testCaseInstance.getScenarioManager();
                        scenarioObjectManager.stop();
                        destroy();
                    }
                } catch (Exception exc) {
                    LOGGER.error(String.format("Failed test case group %s", testCaseInstance), exc);
                    testCaseInstance.setError(exc);
                    testCaseInstance.setStatus(Status.FAILED);
                    testCaseInstance.setEndTime(new Date());
                    EventBusProvider.getInstance().post(new TestCaseGroupEvent.Terminate(testCaseInstance));
                    testCaseInstance.getContext().tc().fail();
                    destroy();
                }
            }
        }
    }

    public void destroy() {
        EventBusProvider.getInstance().unregister(this);
    }

    private void stooped() {
        LOGGER.warn(String.format("Test case group %s - stopped", testCaseInstance));
        testCaseInstance.setStatus(Status.STOPPED);
        testCaseInstance.setEndTime(new Date());
        ScenarioObjectManager scenarioObjectManager = testCaseInstance.getScenarioManager();
        scenarioObjectManager.stop();
        destroy();
    }


}

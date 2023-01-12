package ru.itb.testautomation.core.instance.testcase.executor.impl;

import ru.itb.testautomation.core.context.TCContext;
import ru.itb.testautomation.core.context.manager.ContextManager;
import ru.itb.testautomation.core.dataset.intf.DataSet;
import ru.itb.testautomation.core.event.EventBusProvider;
import ru.itb.testautomation.core.execution.ExecutorServiceProviderFactory;
import ru.itb.testautomation.core.instance.Status;
import ru.itb.testautomation.core.instance.StepExecutorFactory;
import ru.itb.testautomation.core.instance.jira.intf.JiraProfile;
import ru.itb.testautomation.core.instance.step.StepInstance;
import ru.itb.testautomation.core.instance.step.StepIterator;
import ru.itb.testautomation.core.instance.testcase.TestCaseInstance;
import ru.itb.testautomation.core.instance.testcase.event.TestCaseEvent;
import ru.itb.testautomation.core.instance.testcase.executor.intf.TestCaseInstanceExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itb.testautomation.core.instance.testcase.intf.TestCase;
import ru.itb.testautomation.core.manager.ExcelDataSetListManager;
import ru.itb.testautomation.core.monitor.TestCaseMonitor;
import ru.itb.testautomation.core.user.UserInfo;

import java.util.Date;

public class TestCaseStepExecutor implements TestCaseInstanceExecutor {
    private static final Logger LOGGER = LogManager.getLogger(TestCaseStepExecutor.class);

    private static TestCaseStepExecutor instance = new TestCaseStepExecutor();

    private TestCaseStepExecutor() {
    }

    public static TestCaseStepExecutor getInstance() {
        return instance;
    }

    @Override
    public TestCaseInstance executeInstance(final TestCaseInstance testCaseInstance) {
        LOGGER.info("Executing TestCase {}...", testCaseInstance);
        testCaseInstance.getContext().tc().getInstances().add(testCaseInstance);
        testCaseInstance.getContext().tc().start();
        testCaseInstance.setStatus(Status.IN_PROGRESS);
        testCaseInstance.setStartTime(new Date());
        TestCaseMonitor.getInstance().add(testCaseInstance.getContext());
        ExecutorServiceProviderFactory.getInstance().getExecutionServiceProvider().requestForRegular().submit(() -> _executeInstance(testCaseInstance));
        return testCaseInstance;
    }

    private void _executeInstance(TestCaseInstance instance) {
        try {
            EventBusProvider.getInstance().post(new TestCaseEvent.Start(instance));
            StepIterator iterator = instance.iterator();
            while (iterator.hasNext()) {
                synchronized (TestCaseEvent.class) {
                    if (instance.getContext().tc().getStatus().equals(Status.STOPPED)) {
                        stop(instance);
                        break;
                    }
                    StepInstance stepInstance = iterator.next();
                    StepExecutorFactory.getInstance().getExecutor(stepInstance).execute(stepInstance, instance);
                }
            }
            instance.setStatus(Status.PASSED);
            instance.setEndTime(new Date());
            LOGGER.info("TestCase {} executed", instance);
            EventBusProvider.getInstance().post(new TestCaseEvent.Finish(instance));
        } catch (Exception e) {
            if (instance != null) {
                LOGGER.error(String.format("Failed testcase %s", instance), e);
                instance.setError(e);
                instance.setStatus(Status.FAILED);
                instance.setEndTime(new Date());
                EventBusProvider.getInstance().post(new TestCaseEvent.Terminate(instance));
                instance.getContext().tc().fail();
            }
        }
    }

    @Override
    public TestCaseInstance prepare(TestCase testCase, TCContext context, DataSet dataSet, TestCaseInstance parentInstance) throws Exception {
        if (testCase == null) {
            throw new NullPointerException("TestCase cannot be null");
        }
        LOGGER.info("Preparing instance for testcase {}...", testCase);
        TestCaseInstance instance = new TestCaseInstance();
        instance.setStepContainer(testCase);
        prepareContext(instance, context, dataSet);
        if (parentInstance!= null) {
            instance.setScenarioManager(parentInstance.getScenarioManager());
        } else {
            instance.initScenarioManager(instance.getContext().tc().getId());
        }
        prepareKeys(instance);
        return instance;
    }

    private void prepareKeys(TestCaseInstance instance) {
        ContextManager.getInstance().put(instance.getContext().tc());
    }

    @Override
    public TestCaseInstance execute(TestCase testCase, TCContext context, DataSet dataSet, TestCaseInstance parentInstance, UserInfo userInfo, JiraProfile jiraProfile) throws Exception {
        LOGGER.debug("execute() from TestCaseStepExecutor .......");
        if (testCase.getDatasetName()!= null) {
            DataSet ds = ExcelDataSetListManager.getInstance().get().get(testCase.getDatasetName()).getDataSet(testCase.getName());
            TestCaseInstance instance = prepare(testCase, context, ds, parentInstance);
            instance.setJiraProfile(jiraProfile);
            instance.setUserInfo(userInfo);
            return executeInstance(instance);
        }
        TestCaseInstance instance = prepare(testCase, context, dataSet, parentInstance);
        return executeInstance(instance);
    }

    private TCContext prepareContext(TestCaseInstance instance, TCContext context, DataSet dataSet) {
        if (context == null) {
            context = new TCContext();
            context.setInitiator(instance);
        }
        instance.getContext().setTC(context);

        if (context.isEmpty() && dataSet != null) {
            context.keySet().clear();
            context.merge(dataSet.read());
            instance.setDataSet(dataSet);
        } else if (dataSet!= null){
            context.keySet().clear();
            context.merge(dataSet.read());
            instance.setDataSet(dataSet);
        }
        return context;
    }

    private void stop(TestCaseInstance instance) {
        LOGGER.warn(String.format("TestCase %s - stopped", instance));
        instance.setStatus(Status.STOPPED);
        instance.setEndTime(new Date());
        EventBusProvider.getInstance().post(new TestCaseEvent.Stopped(instance));
        EventBusProvider.getInstance().unregister(this);
    }
}

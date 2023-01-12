package ru.itb.testautomation.core.instance.testcase.executor.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itb.testautomation.core.context.TCContext;
import ru.itb.testautomation.core.dataset.intf.DataSet;
import ru.itb.testautomation.core.event.EventBusProvider;
import ru.itb.testautomation.core.instance.Status;
import ru.itb.testautomation.core.instance.jira.intf.JiraProfile;
import ru.itb.testautomation.core.instance.testcase.TestCaseInstance;
import ru.itb.testautomation.core.instance.testcase.event.NextGroupStepEvent;
import ru.itb.testautomation.core.instance.testcase.event.NextTestCaseSubscriberExecutor;
import ru.itb.testautomation.core.instance.testcase.event.TestCaseGroupEvent;
import ru.itb.testautomation.core.instance.testcase.executor.intf.TestCaseInstanceExecutor;
import ru.itb.testautomation.core.instance.testcase.intf.TestCase;
import ru.itb.testautomation.core.user.UserInfo;

import java.util.Date;

public class TestCaseGroupExecutor implements TestCaseInstanceExecutor {
    private static final Logger LOGGER = LogManager.getLogger(TestCaseGroupExecutor.class);

    private static TestCaseGroupExecutor instance = new TestCaseGroupExecutor();

    private TestCaseGroupExecutor() {}

    public static TestCaseGroupExecutor getInstance() {
        return instance;
    }

    @Override
    public TestCaseInstance executeInstance(final TestCaseInstance testCaseInstance) {
        LOGGER.info("Execute test case group ...",testCaseInstance);
        testCaseInstance.getContext().tc().start();
        testCaseInstance.setStatus(Status.IN_PROGRESS);
        testCaseInstance.setStartTime(new Date());
        _executeInstance(testCaseInstance);
        return testCaseInstance;
    }

    private void _executeInstance(TestCaseInstance testCaseInstance) {
        EventBusProvider.getInstance().post(new TestCaseGroupEvent.Start(testCaseInstance));
        NextTestCaseSubscriberExecutor executor = new NextTestCaseSubscriberExecutor(testCaseInstance);
        EventBusProvider.getInstance().register(executor, EventBusProvider.Priority.HIGH);
        EventBusProvider.getInstance().post(new NextGroupStepEvent(testCaseInstance.getContext().tc()));
        //TODO ADD execution DB LOG
    }

    @Override
    public TestCaseInstance prepare(TestCase testCase, TCContext context, DataSet dataSet, TestCaseInstance parentInstance) {
        if (testCase == null) {
            throw new NullPointerException("Test case Group cannot be null");
        }
        LOGGER.info("Preparing instance for test case group {} ...", testCase);
        TestCaseInstance instance = new TestCaseInstance();
        instance.setStepContainer(testCase);
        prepareContext(instance, context,  dataSet);
        instance.initScenarioManager(instance.getContext().tc().getId());
        return instance;
    }

    @Override
    public TestCaseInstance execute(TestCase testCase, TCContext context, DataSet dataSet, TestCaseInstance parentInstance, UserInfo userInfo, JiraProfile jiraProfile) throws Exception {
        LOGGER.debug("execute() from TestCaseGroupExecutor .......");
        TestCaseInstance instance = prepare(testCase, context, dataSet, null);
        instance.setUserInfo(userInfo);
        instance.setJiraProfile(jiraProfile);
        return executeInstance(instance);
    }

    private TCContext prepareContext(TestCaseInstance testCaseInstance, TCContext context, DataSet dataSet) {
        TCContext _context = new TCContext();
        if (context == null) {
            _context = new TCContext();
            _context.setInitiator(testCaseInstance);
        }
        testCaseInstance.getContext().setTC(_context);

        if (_context.isEmpty() && dataSet != null) {
            _context.merge(dataSet.read());
            testCaseInstance.setDataSet(dataSet);
        } else if (dataSet!= null){
            testCaseInstance.setDataSet(dataSet);
        }

        return _context;
    }
}

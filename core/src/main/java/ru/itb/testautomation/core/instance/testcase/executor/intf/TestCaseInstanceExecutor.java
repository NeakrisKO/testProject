package ru.itb.testautomation.core.instance.testcase.executor.intf;

import ru.itb.testautomation.core.context.TCContext;
import ru.itb.testautomation.core.dataset.intf.DataSet;
import ru.itb.testautomation.core.instance.jira.intf.JiraProfile;
import ru.itb.testautomation.core.instance.testcase.TestCaseInstance;
import ru.itb.testautomation.core.instance.testcase.intf.TestCase;
import ru.itb.testautomation.core.user.UserInfo;

public interface TestCaseInstanceExecutor {

    TestCaseInstance executeInstance(TestCaseInstance testCaseInstance);

    TestCaseInstance prepare(TestCase testCase, TCContext context, DataSet dataSet, TestCaseInstance parentInstance) throws Exception;

    TestCaseInstance execute(TestCase testCase, TCContext context, DataSet dataSet, TestCaseInstance parentInstance, UserInfo userInfo, JiraProfile jiraProfile) throws Exception;
}

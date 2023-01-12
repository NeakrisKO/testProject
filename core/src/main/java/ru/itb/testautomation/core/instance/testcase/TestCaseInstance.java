package ru.itb.testautomation.core.instance.testcase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itb.testautomation.core.dataset.intf.DataSet;
import ru.itb.testautomation.core.instance.AbstractContainerInstance;
import ru.itb.testautomation.core.instance.jira.intf.JiraProfile;
import ru.itb.testautomation.core.instance.step.StepIterator;
import ru.itb.testautomation.core.instance.step.intf.StepContainer;
import ru.itb.testautomation.core.instance.testcase.intf.TestCase;
import ru.itb.testautomation.core.manager.ActionObjectManager;
import ru.itb.testautomation.core.manager.intf.ScenarioObjectManager;
import ru.itb.testautomation.core.user.UserInfo;

public class TestCaseInstance extends AbstractContainerInstance {
    private static final Logger LOGGER = LogManager.getLogger(TestCaseInstance.class);
    private TestCase testCase;
    private DataSet dataSet;
    private ScenarioObjectManager scenarioManager;
    private UserInfo userInfo;
    private JiraProfile jiraProfile;


    public TestCaseInstance() {
    }

    @Override
    public StepIterator iterator() {
        return new StepIterator(testCase.getSteps(), this);
    }

    @Override
    public StepContainer getStepContainer() {
        return testCase;
    }

    @Override
    public void setStepContainer(StepContainer container) {
        this.testCase = (TestCase) container;
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    public void setScenarioManager(ScenarioObjectManager scenarioManager) {
        this.scenarioManager = scenarioManager;
    }

    public ScenarioObjectManager getScenarioManager() {
        if (scenarioManager!= null)
            return scenarioManager;
        else {
            LOGGER.error("Scenario manager not initialized, perhaps Server Environment not configured for TestCase:[]"+testCase.getName());
        }
        return null;
    }

    public void initScenarioManager(Object contextId) {
        LOGGER.info("InitScenarioManager");
        scenarioManager = ActionObjectManager.getInstance().initManager(contextId);
    }
    public JiraProfile getJiraProfile() { return jiraProfile; }

    public void setJiraProfile(JiraProfile jiraProfile) { this.jiraProfile = jiraProfile; }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}

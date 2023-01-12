package ru.itb.testautomation.core.instance.testcase.executor.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itb.testautomation.core.IntegrationPack.Jira;
import ru.itb.testautomation.core.event.EventBusProvider;
import ru.itb.testautomation.core.exception.CommonException;
import ru.itb.testautomation.core.instance.Status;
import ru.itb.testautomation.core.instance.action.ActionParam;
import ru.itb.testautomation.core.instance.action.intf.Action;
import ru.itb.testautomation.core.instance.step.StepInstance;
import ru.itb.testautomation.core.instance.step.StepType;
import ru.itb.testautomation.core.instance.step.event.StepEvent;
import ru.itb.testautomation.core.instance.step.impl.UIStepImpl;
import ru.itb.testautomation.core.instance.step.intf.StepExecutor;
import ru.itb.testautomation.core.instance.testcase.TestCaseInstance;
import ru.itb.testautomation.core.manager.intf.ScenarioObjectManager;

import java.util.Date;

public class UIStepExecutor implements StepExecutor {
    private static final Logger LOGGER = LogManager.getLogger(UIStepExecutor.class);
    private Jira j = new Jira();

    @Override
    public void execute(StepInstance stepInstance, TestCaseInstance parentInstance) throws Exception {
        LOGGER.debug("execute() from UIStepExecutor .......");
        LOGGER.info(String.format("Execution of ui step '%s' in '%s'", stepInstance.getStep().getName(), stepInstance.getParent()));
        UIStepImpl step = (UIStepImpl) stepInstance.getStep();
        if (!step.isEnable()) {
            EventBusProvider.getInstance().post(new StepEvent.Skip(stepInstance));
            LOGGER.info("Execution of ui step '{}' in '{}'", stepInstance.getStep().getName(), stepInstance.getParent());
            return;
        }
        if (step.getUnit() != null && step.getDelay() > 0) {
            LOGGER.info("Waiting for timeout. UI Step '{}', timeout '{}' {}", step.getName(), step.getDelay(), step.getUnit().toString());
            Thread.sleep(step.getUnit().toMillis(step.getDelay()));
            LOGGER.info("Timeout '{}' {} elapsed for ui step {}", step.getDelay(), step.getUnit().toString(), step.getName());
        }

        stepInstance.getParent().getStepInstances().add(stepInstance);
        stepInstance.setStatus(Status.IN_PROGRESS);
        stepInstance.setStartTime(new Date());
        EventBusProvider.getInstance().post(new StepEvent.Start(stepInstance));
        try {
            for (Action action : step.getAction()) {
                action.setStatus(Status.IN_PROGRESS);
                if (step.getStepType().equals(StepType.UISTEP.toString())) {
                    executeActionUI(action, stepInstance);
                } else if (step.getStepType().equals(StepType.EMAILSTEP.toString())) {
                    executeActionEmailValidation(action, stepInstance);
                }
                action.setStatus(Status.PASSED);
            }

            stepInstance.setStatus(Status.PASSED);
            stepInstance.setEndTime(new Date());
            LOGGER.info(String.format("Executed ui step '%s' in '%s'", stepInstance.getStep().getName(), stepInstance.getParent()));
            EventBusProvider.getInstance().post(new StepEvent.Finish(stepInstance));
        } catch (CommonException exc) {
            LOGGER.error(String.format("Failed ui step '%s' in '%s'", stepInstance.getStep().getName(), stepInstance.getParent()), exc);
            stepInstance.setImageName(exc.getData());
            stepInstance.setError(exc);
            stepInstance.setStatus(Status.FAILED);
            stepInstance.setEndTime(new Date());
            if (parentInstance.getJiraProfile().getWriteError()) {
                j.createIssue(stepInstance.getError().getMessage(),
                        stepInstance.getStep().getName(),
                        stepInstance.getStep().getSortOrder(),
                        parentInstance.getJiraProfile().getProjectCode(),
                        parentInstance.getJiraProfile().getUrl(),
                        parentInstance.getJiraProfile().getResponsibleUser());
                j.addAttachmentToIssue(parentInstance.getJiraProfile().getUrl(), stepInstance.getImageName());
                j.addwatcher(parentInstance.getJiraProfile().getUrl(), parentInstance.getJiraProfile().getWatcherUser());
            }
            if (stepInstance.getParent() instanceof TestCaseInstance) {
                ScenarioObjectManager scenarioObjectManager = ((TestCaseInstance) stepInstance.getParent()).getScenarioManager();
                scenarioObjectManager.stop();
            }
            EventBusProvider.getInstance().post(new StepEvent.Terminate(stepInstance));
            throw exc;
        }
    }

    @SuppressWarnings("Duplicates")
    private void executeActionUI(Action action, StepInstance stepInstance) throws Exception {
        Action preparedAction = prepareActionParam(action, stepInstance);
        if (stepInstance.getParent() instanceof TestCaseInstance) {
            try {
                if (action.getUnit() != null && action.getDelay() > 0) {
                    LOGGER.info("Waiting for timeout. UI Action '{}', timeout '{}' {}", action.getSortOrder(), action.getDelay(), action.getUnit().toString());
                    Thread.sleep(action.getUnit().toMillis(action.getDelay()));
                    LOGGER.info("Timeout '{}' {} elapsed for ui Action {}", action.getDelay(), action.getUnit().toString(), action.getSortOrder());
                }
                action.setStartTime(new Date());
                ((TestCaseInstance) stepInstance.getParent()).getScenarioManager().execute(preparedAction.getActionParam());
                action.setEndTime(new Date());
            } catch (CommonException exc) {
                action.setEndTime(new Date());
                action.setStatus(Status.FAILED);
                LOGGER.error(exc);
                throw new CommonException("Действие №" + action.getSortOrder() + exc.getMessage(), exc.getData());
            }
        }
    }

    @SuppressWarnings("Duplicates")
    private void executeActionEmailValidation(Action action, StepInstance stepInstance) throws Exception {
        Action preparedAction = prepareActionParam(action, stepInstance);
        if (stepInstance.getParent() instanceof TestCaseInstance) {
            try {
                if (action.getUnit() != null && action.getDelay() > 0) {
                    LOGGER.info("Waiting for timeout. UI Action '{}', timeout '{}' {}", action.getSortOrder(), action.getDelay(), action.getUnit().toString());
                    Thread.sleep(action.getUnit().toMillis(action.getDelay()));
                    LOGGER.info("Timeout '{}' {} elapsed for ui Action {}", action.getDelay(), action.getUnit().toString(), action.getSortOrder());
                }
                action.setStartTime(new Date());
                ((TestCaseInstance) stepInstance.getParent()).getScenarioManager().execute(preparedAction.getActionParam());
                action.setEndTime(new Date());
            } catch (CommonException exc) {
                action.setEndTime(new Date());
                action.setStatus(Status.FAILED);
                LOGGER.error(exc);
                throw new CommonException("Действие №" + action.getSortOrder() + exc.getMessage(), exc.getData());
            }
        }
    }

    private Action prepareActionParam(Action action, StepInstance stepInstance) {
        ActionParam param = action.getActionParam();
        String dsValue = param.getValue();
        try {
            param.setValue(stepInstance.getParent().getContext().tc().get(dsValue).toString());
        } catch (Exception exc) {
            LOGGER.error("Error - prepareActionParam", exc);
            stepInstance.setError(new CommonException("Переменная DataSet`а: " + dsValue + " не существует!"));
            throw new CommonException("Переменная DataSet`а: " + dsValue + " не существует!");
        }

        action.setActionParam(param);

        return action;
    }
}

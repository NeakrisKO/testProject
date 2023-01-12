package ru.itb.web.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.itb.testautomation.core.exception.CommonException;
import ru.itb.testautomation.core.instance.AbstractContainerInstance;
import ru.itb.testautomation.core.instance.action.intf.Action;
import ru.itb.testautomation.core.instance.step.StepInstance;
import ru.itb.testautomation.core.instance.step.impl.UIStepImpl;
import ru.itb.testautomation.core.monitor.TestCaseMonitor;
import ru.itb.testautomation.core.monitor.model.TestCaseMonitorItem;
import ru.itb.web.model.ui.UIErrorObject;
import ru.itb.web.model.ui.UIObject;
import ru.itb.web.model.ui.UIReportItem;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/monitor")
public class ExecutionMonitorController {
    private static final Logger LOGGER = LogManager.getLogger(ExecutionMonitorController.class);
    private static final String DATE_FORMAt = "dd.MM.yy HH:mm:ss";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAt);

    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    public List<UIReportItem> getList() {
        List<UIReportItem> uiReportItems = new ArrayList<>();

        try {
            TestCaseMonitor.getInstance().updateAll();
            dateFormat.setTimeZone(TimeZone.getDefault());

            Iterator<Map.Entry<String, TestCaseMonitorItem>> iterator = TestCaseMonitor.getInstance().getItems().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, TestCaseMonitorItem> item = iterator.next();
                UIReportItem uiReportItem = buildUIReportItem(item.getValue());
                uiReportItems.add(uiReportItem);
            }
        } catch (Exception exc) {
            LOGGER.error("Can't get report items", exc);
        }
        uiReportItems.sort((item1, item2) -> {
            try {
                Date date1 = dateFormat.parse(item1.getStartTime());
                Date date2 = dateFormat.parse(item2.getStartTime());
                if (date1.getTime() > date2.getTime())
                    return -1;
            } catch (Exception exc) {
                LOGGER.error("Can't parse date", exc);
            }
            return 0;
        });
        return uiReportItems;
    }

    @RequestMapping(value = "get/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<UIObject> get(@PathVariable("id") String id) {
        List<UIObject> objectList = new ArrayList<>();
        TestCaseMonitor.getInstance().updateAll();
        List<AbstractContainerInstance> instances = TestCaseMonitor.getInstance().getProcessInstances(id);
        UIStepImpl uiStep;
        if (instances == null) {
            return objectList;
        } else {
            for (AbstractContainerInstance entry : instances) {
                UIObject uiObject = new UIObject();
                uiObject.setId(entry.getStepContainer().getId().toString());
                uiObject.setName(entry.getStepContainer().getName());
                uiObject.setDescription(entry.getStepContainer().getDescription());
                uiObject.setStatus(entry.getStatus().toString());
                uiObject.setStartTime(dateFormat.format(entry.getStartTime()));
                uiObject.setEndTime(entry.getEndTime() == null ? "" : dateFormat.format(entry.getEndTime()));
                //uiObject.setErrorMsg(entry.getError().getMessage());
                if (entry.getStepInstances() != null && !entry.getStepInstances().isEmpty()) {
                    List<UIObject> child = new ArrayList<>();
                    for (StepInstance stepInstance : entry.getStepInstances()) {
                        UIObject step = new UIObject();
                        uiStep = (UIStepImpl) stepInstance.getStep();
                        step.setId(uiStep.getId().toString());
                        step.setName(uiStep.getName());
                        step.setDescription(uiStep.getDescription());
                        step.setStatus(stepInstance.getStatus().toString());
                        step.setStartTime(dateFormat.format(stepInstance.getStartTime()));
                        step.setEndTime(stepInstance.getEndTime() == null ? "" : dateFormat.format(stepInstance.getEndTime()));

                        if (uiStep.getAction() != null && !uiStep.getAction().isEmpty()) {
                            List<UIObject> actionChild = new ArrayList<>();
                            for (Action uiAction : uiStep.getAction()) {
                                UIObject action = new UIObject();
                                if (uiAction.getStatus() != null) {
                                    action.setId(uiAction.getId().toString());
                                    action.setName(uiAction.getActionParam().getName());
                                    action.setDescription(uiAction.getActionParam().getFieldType() + " = "
                                            + uiAction.getActionParam().getField() + " значение - " + uiAction.getActionParam().getValue());
                                    action.setStatus(uiAction.getStatus().toString());
                                    action.setStartTime(uiAction.getStartTime() == null ? "" : dateFormat.format(uiAction.getStartTime()));
                                    action.setEndTime(uiAction.getEndTime() == null ? "" : dateFormat.format(uiAction.getEndTime()));
                                    action.setError(stepInstance.getError() == null ? null : buildUIError(stepInstance.getError()));
                                    actionChild.add(action);
                                }
                            }
                            step.setChild(actionChild);
                        }
                        child.add(step);
                    }
                    uiObject.setChild(child);
                }
                objectList.add(uiObject);
            }
        }
        return objectList;
    }

    private UIErrorObject buildUIError(Throwable error) {
        UIErrorObject uiErrorObject = null;
        if (error instanceof CommonException) {
            uiErrorObject = new UIErrorObject(error.getMessage(), ((CommonException) error).getData());
            return uiErrorObject;
        }
        return uiErrorObject;
    }

    private UIReportItem buildUIReportItem(TestCaseMonitorItem item) {
        UIReportItem uiReportItem = new UIReportItem();
        uiReportItem.setId(item.getId().toString());
        uiReportItem.setName(item.getName());
        uiReportItem.setDescription(item.getDescription());
        uiReportItem.setInitiator(item.getInitiator());
        uiReportItem.setStatus(item.getStatus().toString());
        uiReportItem.setStartTime(dateFormat.format(item.getStartTime()));
        uiReportItem.setUserFirstName(item.getUserFirstName());
        uiReportItem.setUserLastName(item.getUserLastName());
        uiReportItem.setUserEmail(item.getUserEmail());
        uiReportItem.setUserLogin(item.getUserLogin());
        if (item.getEndTime() != null) {
            uiReportItem.setEndTime(dateFormat.format(item.getEndTime()));
        }
        return uiReportItem;
    }
}

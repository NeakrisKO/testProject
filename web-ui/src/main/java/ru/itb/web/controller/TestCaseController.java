package ru.itb.web.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.itb.testautomation.core.context.intf.JSONContext;
import ru.itb.testautomation.core.dataset.intf.DataSet;
import ru.itb.testautomation.core.dataset.intf.DataSetList;
import ru.itb.testautomation.core.instance.action.intf.Action;
import ru.itb.testautomation.core.instance.step.impl.UIStepImpl;
import ru.itb.testautomation.core.instance.step.intf.Step;
import ru.itb.testautomation.core.instance.testcase.TestCaseStep;
import ru.itb.testautomation.core.instance.testcase.intf.TestCaseGroup;
import ru.itb.testautomation.core.manager.ExcelDataSetListManager;
import ru.itb.testautomation.core.instance.testcase.impl.TestCaseImpl;
import ru.itb.testautomation.core.manager.CoreObjectManager;
import ru.itb.web.model.ui.UIAction;
import ru.itb.web.model.ui.UIDataSetSimple;
import ru.itb.web.model.ui.UIObjectNode;
import ru.itb.web.model.ui.UIStep;
import ru.itb.web.model.ui.UITestCase;
import ru.itb.web.model.ui.UITestCaseGroup;
import ru.itb.web.util.UIObjectConverter;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/testcase")
public class TestCaseController {
    private static final Logger LOGGER = LogManager.getLogger(TestCaseController.class);

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<UITestCase> createTestCase(@RequestBody UITestCase uiTestCase) {
        TestCaseImpl testCase = new TestCaseImpl();
        testCase.setName(uiTestCase.getName());
        testCase.setDescription(uiTestCase.getDescription());
        testCase.setSortOrder(uiTestCase.getSortOrder());
        testCase.setDatasetName(uiTestCase.getDataset());
        Integer id = CoreObjectManager.getInstance().getManager().addTC(testCase);
        UITestCase response = UIObjectConverter.getInstance().convertTestCase(CoreObjectManager.getInstance().getManager().getTC(id, false));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "get/{id}/{lazy}", method = RequestMethod.GET)
    public @ResponseBody
    UITestCase get(@PathVariable("id") Integer testCaseID,
                   @PathVariable("lazy") Boolean lazy) {
        UITestCase response;
        response = UIObjectConverter.getInstance().convertTestCase(CoreObjectManager.getInstance().getManager().getTC(testCaseID, lazy));
        return response;
    }

    @RequestMapping(value = "addstep/{id}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void addStep(@RequestBody UIStep uiStep, @PathVariable("id") Integer tcId) {
        if (uiStep.getTestCaseId() != null) {
            TestCaseStep testCase = CoreObjectManager.getInstance().getManager().getTC(uiStep.getTestCaseId(), false);
            int size = testCase.getTestCase().getSteps().size();
            if (size > 0) {
                int lastSortOrder = testCase.getTestCase().getSteps().get(size - 1).getSortOrder();
                uiStep.setSortOrder(lastSortOrder + 1);
            } else {
                uiStep.setSortOrder(1);
            }
            Step step = UIObjectConverter.getInstance().convertUIStep(uiStep);
            testCase.getTestCase().getSteps().add(step);
            CoreObjectManager.getInstance().getManager().updateTC(testCase, tcId);
        } else LOGGER.error("TestCase id can't be null or empty");
    }

    @RequestMapping(value = "update_step", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void updateStep(@RequestBody UIStep uiStep) {
        TestCaseStep testCase = CoreObjectManager.getInstance().getManager().getTC(uiStep.getTestCaseId(), false);

        ListIterator<Step> stepIterator = testCase.getTestCase().getSteps().listIterator();
        while (stepIterator.hasNext()) {
            Step step = stepIterator.next();
            Integer stepId = (Integer) step.getId();
            if (stepId.intValue() == uiStep.getId().intValue()) {
                stepIterator.set(UIObjectConverter.getInstance().updateStep(step, uiStep));
            }
        }
        CoreObjectManager.getInstance().getManager().updateTC(testCase, null);
    }

    @RequestMapping(value = "remove/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void remove(@PathVariable("id") Integer id) {
        CoreObjectManager.getInstance().getManager().removeTC(id);
    }

    @RequestMapping(value = "remove_step/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void removeStep(@PathVariable("id") Integer id) {
        CoreObjectManager.getInstance().getManager().removeStepUI(id);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public void update(@RequestBody UITestCase uiTestCase) {
        TestCaseStep oldTestCase = CoreObjectManager.getInstance()
                .getManager().getTC(uiTestCase.getId(), false);
        oldTestCase.getTestCase().setName(uiTestCase.getName());
        oldTestCase.getTestCase().setDescription(uiTestCase.getDescription());
        oldTestCase.getTestCase().setDatasetName(uiTestCase.getDataset());
        CoreObjectManager.getInstance().getManager().updateTCSimple(oldTestCase);
    }

    @RequestMapping(value = "add_to_group/{groupId}", method = RequestMethod.POST)
    public void addTestCaseToGroup(@RequestBody UITestCase uiTestCase, @PathVariable("groupId") Integer groupId) {
        TestCaseGroup testCaseGroup = CoreObjectManager.getInstance().getManager().getTCGroup(groupId, false);
        try {
            testCaseGroup.addGroupStep(UIObjectConverter.getInstance().convertUITestCase(uiTestCase));
            CoreObjectManager.getInstance().getManager().updateTCGroup(testCaseGroup, groupId);
        } catch (Exception exc) {
            LOGGER.error("Can't add Test Case to group with id:" + groupId, exc);
        }
    }

    @RequestMapping(value = "get_group/{groupId}", method = RequestMethod.GET)
    @ResponseBody
    public UITestCaseGroup getTCGroup(@PathVariable("groupId") Integer groupId) {
        UITestCaseGroup response;
        response = UIObjectConverter.getInstance().convertTestCaseToUI(CoreObjectManager.getInstance().getManager().getTCGroup(groupId, false));
        return response;
    }

    @RequestMapping(value = "ds_list", method = RequestMethod.GET)
    @ResponseBody
    public List<UIDataSetSimple> getDSList() {
        List<UIDataSetSimple> dsList;
        Map<String, DataSetList> dsMap = ExcelDataSetListManager.getInstance().get();
        dsList = dsMap.entrySet().stream().map(this::mapDS).collect(Collectors.toList());
        return dsList;
    }

    @RequestMapping(value = "action_list/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<UIAction> getActions(@PathVariable("id") Integer stepId) {
        return UIObjectConverter.getInstance().convertActions(CoreObjectManager.getInstance().getManager().getActionList(stepId));
    }

    @RequestMapping(value = "get_step/{id}/{lazy}", method = RequestMethod.GET)
    public @ResponseBody
    UIStep getStep(@PathVariable("id") Integer stepId,
                   @PathVariable("lazy") Boolean lazy) {
        UIStep response;
        response = UIObjectConverter.getInstance().convertStep(CoreObjectManager.getInstance().getManager().getStepUI(stepId, lazy));
        return response;
    }

    @RequestMapping(value = "change_step_sort_order/{idFirst}/{soFirst}/{idSecond}/{soSecond}", method = RequestMethod.GET)
    @ResponseBody
    public void switchStep(@PathVariable("idFirst") Integer idFirst,
                           @PathVariable("soFirst") Integer soFirst,
                           @PathVariable("idSecond") Integer idSecond,
                           @PathVariable("soSecond") Integer soSecond) {
        CoreObjectManager.getInstance().getManager().changeStepUIPosition(idFirst, soFirst, idSecond, soSecond);
    }

    @RequestMapping(value = "change_action_sort_order/{idFirst}/{soFirst}/{idSecond}/{soSecond}", method = RequestMethod.GET)
    @ResponseBody
    public void switchAction(@PathVariable("idFirst") Integer idFirst,
                             @PathVariable("soFirst") Integer soFirst,
                             @PathVariable("idSecond") Integer idSecond,
                             @PathVariable("soSecond") Integer soSecond) {
        CoreObjectManager.getInstance().getManager().changeActionPosition(idFirst, soFirst, idSecond, soSecond);
    }

    @RequestMapping(value = "step_tree", method = RequestMethod.GET)
    @ResponseBody
    public List<UIObjectNode> buildTestCaseTreeProject() {
        return UIObjectConverter.getInstance().convertForStepTree(CoreObjectManager.getInstance().getManager().getProjectList(false), false);
    }

    @RequestMapping(value = "group_tree", method = RequestMethod.GET)
    @ResponseBody
    public List<UIObjectNode> buildGroupTreeProject() {
        return UIObjectConverter.getInstance().convertForGroupTree(CoreObjectManager.getInstance().getManager().getProjectList(false));
    }

    @RequestMapping(value = "action_tree", method = RequestMethod.GET)
    @ResponseBody
    public List<UIObjectNode> buildStepTreeProject() {
        return UIObjectConverter.getInstance().convertForStepTree(CoreObjectManager.getInstance().getManager().getProjectList(false), true);
    }

    @RequestMapping(value = "copystep/{uiStepId}/{testCaseId}", method = RequestMethod.GET)
    @ResponseBody
    public void copyStep(@PathVariable("uiStepId") Integer uiStepId,
                         @PathVariable("testCaseId") Integer testCaseId) {
        Step step = CoreObjectManager.getInstance().getManager().getStepUI(uiStepId, false);
        UIObjectConverter.getInstance().prepareStepForCopy(step);
        TestCaseStep testCaseStep = CoreObjectManager.getInstance().getManager().getTC(testCaseId, false);
        testCaseStep.getTestCase().addStep(step);
        CoreObjectManager.getInstance().getManager().updateTC(testCaseStep, null);
    }

    @RequestMapping(value = "copyaction/{uiActionId}/{uiStepId}", method = RequestMethod.GET)
    @ResponseBody
    public void copyAction(@PathVariable("uiActionId") Integer uiActionId,
                           @PathVariable("uiStepId") Integer uiStepId) {
        Action action = CoreObjectManager.getInstance().getManager().getAction(uiActionId, false);
        Step step = CoreObjectManager.getInstance().getManager().getStepUI(uiStepId, false);
        if (step instanceof UIStepImpl) {
            action.setId(null);
            ((UIStepImpl) step).addAction(action);
        }
        CoreObjectManager.getInstance().getManager().updateStepUI(step, null);
    }

    @RequestMapping(value = "moveaction/{uiStepId}/{actionId}", method = RequestMethod.GET)
    @ResponseBody
    public void moveAction(@PathVariable("uiStepId") Integer uiStepId,
                           @PathVariable("actionId") Integer actionId) {
        Step step = CoreObjectManager.getInstance().getManager().getStepUI(uiStepId, false);
        int actionsSize = ((UIStepImpl) step).getAction() != null ? ((UIStepImpl) step).getAction().size() : 0;
        int actionSortOrder = actionsSize + 1;
        if (actionsSize != 0) {
            actionSortOrder = ((UIStepImpl) step).getAction().get(actionsSize - 1).getSortOrder() + 1;
        }

        CoreObjectManager.getInstance().getManager().moveAction(uiStepId, actionId, actionSortOrder);
    }

    @RequestMapping(value = "movestep/{testCaseId}/{uiStepId}", method = RequestMethod.GET)
    @ResponseBody
    public void moveUIStep(@PathVariable("testCaseId") Integer testCaseId,
                           @PathVariable("uiStepId") Integer uiStepId) {
        TestCaseStep testCaseStep = CoreObjectManager.getInstance().getManager().getTC(testCaseId, false);
        int stepSize = testCaseStep.getTestCase().getSteps() != null ? testCaseStep.getTestCase().getSteps().size() : 0;
        int stepSortOrder = stepSize + 1;
        if (stepSize != 0) {
            stepSortOrder = testCaseStep.getTestCase().getSteps().get(stepSize - 1).getSortOrder() + 1;
        }
        CoreObjectManager.getInstance().getManager().moveUIStep(testCaseId, uiStepId, stepSortOrder);
    }

    @RequestMapping(value = "change_test_case_sort_order/{idFirst}/{soFirst}/{idSecond}/{soSecond}", method = RequestMethod.GET)
    @ResponseBody
    public void switchTC(@PathVariable("idFirst") Integer idFirst,
                                @PathVariable("soFirst") Integer soFirst,
                                @PathVariable("idSecond") Integer idSecond,
                                @PathVariable("soSecond") Integer soSecond) {
        CoreObjectManager.getInstance().getManager().changeTCPosition(idFirst,soFirst,idSecond,soSecond);
    }

    @RequestMapping(value = "move_testcase/{groupId}/{testCaseId}", method = RequestMethod.GET)
    @ResponseBody
    public void moveTC(@PathVariable("groupId") Integer groupId,
                              @PathVariable("testCaseId") Integer testCaseId)
    {
        TestCaseGroup testCaseGroup = CoreObjectManager.getInstance().getManager().getTCGroup(groupId, false);
        int tcSize = testCaseGroup.getSteps() != null ? testCaseGroup.getSteps().size() : 0;
        int tcSortOrder = tcSize + 1;
        if (tcSize != 0) {
            Step step = testCaseGroup.getSteps().get(tcSize - 1);
            if (step instanceof TestCaseStep) {
                tcSortOrder = ((TestCaseStep) step).getTestCase().getSortOrder() +1;
            }
        }

        CoreObjectManager.getInstance().getManager().moveTC(groupId,testCaseId,tcSortOrder);
    }

    @RequestMapping(value = "copy_testcase/{tcId}/{tcGroupId}", method = RequestMethod.GET)
    @ResponseBody
    public void copyTC(@PathVariable("tcId") Integer tcId,
                              @PathVariable("tcGroupId") Integer tcGroupId)
    {
        TestCaseStep testCaseStep = CoreObjectManager.getInstance().getManager().getTC(tcId, false);
        TestCaseGroup testCaseGroup = CoreObjectManager.getInstance().getManager().getTCGroup(tcGroupId, false);
        UIObjectConverter.getInstance().prepareTCForCopy(testCaseStep);
        testCaseGroup.addGroupStep(testCaseStep);

        CoreObjectManager.getInstance().getManager().updateTCGroup(testCaseGroup,null);
    }

    @RequestMapping(value = "ds_variable/{dsName}/{tcName}", method = RequestMethod.GET)
    @ResponseBody
    public ArrayList<String> testDSAC(@PathVariable("dsName") String dsName, @PathVariable("tcName") String tcName) {
        ArrayList<String> result = new ArrayList<>();
        DataSet ds = ExcelDataSetListManager.getInstance().get().get(dsName).getDataSet(tcName);
        if (ds != null) {
            JSONContext jsonContext = ds.read();
            Arrays.stream(jsonContext.keySet().toArray()).forEach(key -> {
                HashMap map = (HashMap) jsonContext.get(key.toString());
                Arrays.stream(map.keySet().toArray()).filter(value -> value instanceof String).filter(value -> !((String) value).isEmpty()).map(value -> key.toString() + "." + value).forEach(result::add);
            });
        }
        return result;
    }

    private UIDataSetSimple mapDS(Map.Entry<String, DataSetList> entry) {
        UIDataSetSimple uiDataSet = new UIDataSetSimple();
        uiDataSet.setName(entry.getKey());
        return uiDataSet;
    }
}
package ru.itb.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.itb.testautomation.core.instance.action.ActionParam;
import ru.itb.testautomation.core.instance.action.FieldType;
import ru.itb.testautomation.core.instance.action.intf.Action;
import ru.itb.testautomation.core.instance.step.impl.UIStepImpl;
import ru.itb.testautomation.core.instance.step.intf.Step;
import ru.itb.testautomation.core.manager.CoreObjectManager;
import ru.itb.web.model.ui.UIAction;
import ru.itb.web.model.ui.UIStep;
import ru.itb.web.util.UIObjectConverter;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/action")
public class ActionController {

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public void getActionList() {

    }

    @RequestMapping(value = "add/{id}", method = RequestMethod.POST)
    @ResponseBody
    public List<UIAction> addAction(@RequestBody UIAction uiAction, @PathVariable("id") Integer stepId) {
        Step step = CoreObjectManager.getInstance().getManager().getStepUI(stepId,false);
        if (step instanceof UIStepImpl) {
            Action action = UIObjectConverter.getInstance().convertUIAction(uiAction);
            ((UIStepImpl) step).addAction(action);
        }
        CoreObjectManager.getInstance().getManager().updateStepUI(step, null);

        UIStep response = UIObjectConverter.getInstance()
                .convertStep(CoreObjectManager.getInstance().getManager().getStepUI(stepId, false));
        return response.getActions();
    }

    @RequestMapping(value = "update", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<UIAction> update(@RequestBody UIAction uiAction) {
        Action oldAction = CoreObjectManager.getInstance().getManager().getAction(uiAction.getId(),false);
        ActionParam actionParam = oldAction.getActionParam();
        actionParam.setField(uiAction.getField());
        actionParam.setName(uiAction.getName());
        actionParam.setValue(uiAction.getVariable());
        actionParam.setFieldType(FieldType.valueOf(uiAction.getFieldType()));
        actionParam.setData(uiAction.getData());
        oldAction.setActionParam(actionParam);
        oldAction.setDelay(uiAction.getDelay());
        oldAction.setUnit(TimeUnit.valueOf(uiAction.getUnit()));

        CoreObjectManager.getInstance().getManager().updateAction(oldAction);
        UIAction response = UIObjectConverter.getInstance().convertAction(CoreObjectManager.getInstance().getManager().getAction(uiAction.getId(),false));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "remove/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void remove(@PathVariable("id") Integer id) {
        CoreObjectManager.getInstance().getManager().removeAction(id);
    }
}

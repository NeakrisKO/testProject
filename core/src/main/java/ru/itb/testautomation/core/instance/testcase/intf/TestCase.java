package ru.itb.testautomation.core.instance.testcase.intf;

import ru.itb.testautomation.core.bobject.BusinessObject;
import ru.itb.testautomation.core.dataset.intf.DataSetList;
import ru.itb.testautomation.core.instance.step.intf.Step;
import ru.itb.testautomation.core.instance.step.intf.StepContainer;

import java.util.List;
import java.util.Set;

public interface TestCase extends BusinessObject, StepContainer {
    String getDatasetName();
    void setDatasetName(String datasetName);
    Set<DataSetList> getCompatibleDataSetLists();
    void setCompatibleDataSetLists(Set<DataSetList> compatibleDataSetLists);
    void setSteps(List<Step> steps);
    void addStep(Step step);
}

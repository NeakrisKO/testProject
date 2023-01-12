package ru.itb.testautomation.core.instance.testcase;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import ru.itb.testautomation.core.dataset.intf.DataSetList;
import ru.itb.testautomation.core.instance.step.intf.Step;
import ru.itb.testautomation.core.instance.testcase.intf.TestCase;

import java.util.List;
import java.util.Set;

public abstract class AbstractTestCase implements TestCase {
    private String datasetName;
    protected List<Step> steps = Lists.newLinkedList();
    private Set<DataSetList> compatibleDataSetLists = Sets.newHashSetWithExpectedSize(10);

    @Override
    public List<Step> getSteps() {
        return this.steps;
    }

    @Override
    public String getDatasetName() {
        return this.datasetName;
    }

    @Override
    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    @Override
    public Set<DataSetList> getCompatibleDataSetLists() {
        return compatibleDataSetLists;
    }

    @Override
    public void setCompatibleDataSetLists(Set<DataSetList> compatibleDataSetLists) {
        this.compatibleDataSetLists = compatibleDataSetLists;
    }
    @Override
    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }
}

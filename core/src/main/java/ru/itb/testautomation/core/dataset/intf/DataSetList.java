package ru.itb.testautomation.core.dataset.intf;

import ru.itb.testautomation.core.common.Named;

import java.util.Set;

public interface DataSetList extends Named {
    Set<DataSet> getDataSets();
    Set<String> getVariables();
    DataSet getDataSet(String dataSetName);
}

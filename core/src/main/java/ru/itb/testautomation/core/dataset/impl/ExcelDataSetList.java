package ru.itb.testautomation.core.dataset.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import ru.itb.testautomation.core.dataset.intf.DataSetList;
import ru.itb.testautomation.core.dataset.intf.DataSet;

import java.util.Set;

public class ExcelDataSetList implements DataSetList {
    private Set<DataSet> dataSets = Sets.newHashSetWithExpectedSize(20);
    private Set<String> variables = Sets.newHashSetWithExpectedSize(200);
    private String fileName;
    private String name;

    @Override
    public Set<DataSet> getDataSets() {
        return dataSets;
    }

    @Override
    public Set<String> getVariables() {
        return variables;
    }

    @Override
    public DataSet getDataSet(String dataSetName) {
        if (!Strings.isNullOrEmpty(dataSetName)) {
            for (DataSet dataSet : dataSets) {
                if (dataSetName.equals(dataSet.getName())) {
                    return dataSet;
                }
            }
        }
        return null;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}

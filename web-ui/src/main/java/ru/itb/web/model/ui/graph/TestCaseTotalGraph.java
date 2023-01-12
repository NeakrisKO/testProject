package ru.itb.web.model.ui.graph;

import java.util.ArrayList;
import java.util.List;

public class TestCaseTotalGraph {
    private List<String> labels = new ArrayList<>();
    private List<GraphDataSet> datasets = new ArrayList<>();

    public TestCaseTotalGraph() {}

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<GraphDataSet> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<GraphDataSet> datasets) {
        this.datasets = datasets;
    }

    public void addLabel(String label) {
        labels.add(label);
    }

    public void addDatasets(GraphDataSet dataset) {
        datasets.add(dataset);
    }
}

package ru.itb.web.model.ui.graph;

import java.util.ArrayList;
import java.util.List;

public class GraphDataSet {
    private String label;
    private String backgroundColor;
    private String borderColor;
    private List<Integer> data = new ArrayList<>();

    public GraphDataSet() {}

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public List<Integer> getData() {
        return data;
    }

    public void setData(List<Integer> data) {
        this.data = data;
    }

    public void addData(Integer value) {
        data.add(value);
    }
}

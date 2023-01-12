package ru.itb.web.model.ui;

import java.util.ArrayList;
import java.util.List;

public class UIUserTestCases {
    private List<UISimpleObject> list = new ArrayList<>();

    public UIUserTestCases(){}

    public List<UISimpleObject> getList() {
        return list;
    }

    public void setList(List<UISimpleObject> list) {
        this.list = list;
    }

    public void add(UISimpleObject uiSimpleObject) {
        list.add(uiSimpleObject);
    }
}

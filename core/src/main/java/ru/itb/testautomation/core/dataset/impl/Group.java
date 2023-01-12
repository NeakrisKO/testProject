package ru.itb.testautomation.core.dataset.impl;

import ru.itb.testautomation.core.common.AbstractNamedImpl;

public class Group extends AbstractNamedImpl {
        private int startRowIndex;
        private int endRowIndex;

    public int getStartRowIndex() {
        return startRowIndex;
    }

    public void setStartRowIndex(int startRowIndex) {
        this.startRowIndex = startRowIndex;
    }

    public int getEndRowIndex() {
        return endRowIndex;
    }

    public void setEndRowIndex(int endRowIndex) {
        this.endRowIndex = endRowIndex;
    }
}

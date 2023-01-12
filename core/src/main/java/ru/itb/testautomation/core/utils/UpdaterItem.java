package ru.itb.testautomation.core.utils;

public class UpdaterItem {
    private String field;
    private Object value;

    UpdaterItem(String field, Object value) {
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }
}

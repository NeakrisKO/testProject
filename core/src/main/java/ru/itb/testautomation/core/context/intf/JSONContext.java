package ru.itb.testautomation.core.context.intf;

import org.json.simple.JSONAware;
import java.util.Map;

public interface JSONContext extends Map, JSONAware {
    Object create(Object key, boolean list);
    Object create(Object key);
    <T> T get(String key, Class<T> castTo);
    void merge(Map from);
}

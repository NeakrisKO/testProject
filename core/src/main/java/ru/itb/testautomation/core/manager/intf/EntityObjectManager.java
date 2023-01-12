package ru.itb.testautomation.core.manager.intf;

import ru.itb.testautomation.core.bobject.BusinessObject;
import java.util.List;

public interface EntityObjectManager <T extends BusinessObject> {
    Integer create(T entity);
    T get(Integer id);
    List<T> get();
    void remove(Integer id);
    Integer update(T entity, Integer parentId);
    void clear();
}

package ru.itb.testautomation.core.instance.action.intf;

import ru.itb.testautomation.core.bobject.BusinessObject;
import ru.itb.testautomation.core.common.AuditAble;
import ru.itb.testautomation.core.common.IdAble;
import ru.itb.testautomation.core.common.SortAble;
import ru.itb.testautomation.core.common.Statusable;
import ru.itb.testautomation.core.instance.action.ActionParam;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public interface Action extends BusinessObject, IdAble, AuditAble, SortAble, Statusable {
    void setActionParam(ActionParam param);
    ActionParam getActionParam();
    Integer getDelay();
    void setDelay(Integer delay);
    TimeUnit getUnit();
    void setUnit(TimeUnit unit);
    void setStartTime(Date startTime);
    Date getStartTime();
    void setEndTime(Date startTime);
    Date getEndTime();
}

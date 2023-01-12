package ru.itb.testautomation.core.instance.jira.intf;

import ru.itb.testautomation.core.bobject.BusinessObject;
import ru.itb.testautomation.core.common.AuditAble;
import ru.itb.testautomation.core.common.DescriptionAble;
import ru.itb.testautomation.core.common.IdAble;
import ru.itb.testautomation.core.common.Named;

public interface JiraProfile extends BusinessObject, IdAble, Named, DescriptionAble, AuditAble {
    String getUserName();
    void setUserName(String userName);
    String getUserPassword();
    void setUserPassword(String userPassword);
    String getProjectCode();
    void setProjectCode(String projectCode);
    String getUrl();
    void setUrl(String url);
    String getResponsibleUser();
    void setResponsibleUser(String responsibleUser);
    boolean getWriteError();
    void setWriteError(boolean writeError);
    String getWatcherUser();
    void setWatcherUser(String watcherUser);
}

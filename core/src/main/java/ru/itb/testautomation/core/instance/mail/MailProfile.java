package ru.itb.testautomation.core.instance.mail;

import ru.itb.testautomation.core.bobject.BusinessObject;
import ru.itb.testautomation.core.common.AuditAble;
import ru.itb.testautomation.core.common.DescriptionAble;
import ru.itb.testautomation.core.common.IdAble;
import ru.itb.testautomation.core.common.Named;

public interface MailProfile extends BusinessObject, IdAble, Named, DescriptionAble, AuditAble {
    String getUser();
    void setUser(String userName);
    String getPassword();
    void setPassword(String userPassword);
    String getServer();
    void setServer(String url);
}

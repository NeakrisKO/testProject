package ru.itb.testautomation.core.common;

import java.time.LocalDateTime;

public interface AuditAble {
    LocalDateTime getDateCreate();
    void setDateCreate(LocalDateTime dateTime);
    LocalDateTime getDateUpdate();
    void setDateUpdate(LocalDateTime dateTime);
    String getUserCreate();
    void setUserCreate(String userCreate);
    String getUserUpdate();
    void setUserUpdate(String userUpdate);
}

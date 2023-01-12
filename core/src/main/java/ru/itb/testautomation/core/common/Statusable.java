package ru.itb.testautomation.core.common;

import ru.itb.testautomation.core.instance.Status;

public interface Statusable {
    Status getStatus();

    void setStatus(Status status);

}

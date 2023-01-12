package ru.itb.testautomation.core.execution.intf;

import java.util.concurrent.ExecutorService;

public interface ExecutionServiceProvider {
    ExecutorService requestForBackgroundJob();
    ExecutorService requestForRegular();
}

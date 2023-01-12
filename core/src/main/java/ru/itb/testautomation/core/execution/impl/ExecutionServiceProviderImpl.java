package ru.itb.testautomation.core.execution.impl;

import ru.itb.testautomation.core.common.Config;
import ru.itb.testautomation.core.execution.intf.ExecutionServiceProvider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutionServiceProviderImpl implements ExecutionServiceProvider {
    private final ExecutorService longPool = Executors.newCachedThreadPool();
    private final ExecutorService shortPool;

    public ExecutionServiceProviderImpl() {
        int threadCount = Integer.parseInt(Config.getConfig().getString("executor.thread.pool.size", "-1"));
        if (threadCount <= 0) {
            threadCount = 20;
        }
        shortPool = Executors.newFixedThreadPool(threadCount);
    }

    @Override
    public ExecutorService requestForBackgroundJob() {
        return this.longPool;
    }

    @Override
    public ExecutorService requestForRegular() {
        return this.shortPool;
    }
}

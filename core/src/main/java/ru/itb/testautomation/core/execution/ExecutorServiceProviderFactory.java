package ru.itb.testautomation.core.execution;


import ru.itb.testautomation.core.common.inject.SpringInjection;
import ru.itb.testautomation.core.execution.intf.ExecutionServiceProvider;

public class ExecutorServiceProviderFactory {
    private static ExecutorServiceProviderFactory instance = new ExecutorServiceProviderFactory();

    public static ExecutorServiceProviderFactory getInstance() {
        return instance;
    }

    private ExecutorServiceProviderFactory() {
    }

    private ExecutionServiceProvider executionServiceProvider = SpringInjection.getInstance().getContext().getBean("executionProvider", ExecutionServiceProvider.class);

    public ExecutionServiceProvider getExecutionServiceProvider() {
        return executionServiceProvider;
    }
}

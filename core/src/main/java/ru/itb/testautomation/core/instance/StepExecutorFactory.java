package ru.itb.testautomation.core.instance;

import com.google.common.collect.Maps;
import ru.itb.testautomation.core.instance.step.StepInstance;
//import ru.itb.testautomation.core.instance.step.impl.GroupStep;
import ru.itb.testautomation.core.instance.step.impl.UIStepImpl;
import ru.itb.testautomation.core.instance.step.intf.Step;
import ru.itb.testautomation.core.instance.step.intf.StepExecutor;
//import ru.itb.testautomation.core.instance.testcase.executor.impl.TestCaseGroupStepExecutor;
import ru.itb.testautomation.core.instance.testcase.TestCaseStep;
import ru.itb.testautomation.core.instance.testcase.executor.impl.UIStepExecutor;
import ru.itb.testautomation.core.instance.testcase.executor.impl.TestCaseExecutor;

import java.util.Map;

public class StepExecutorFactory {
    private Map<Class<? extends Step>, StepExecutor> executorBindings = Maps.newHashMapWithExpectedSize(10);

    private static volatile StepExecutorFactory instance;

    public static StepExecutorFactory getInstance() {
        StepExecutorFactory localInstance = instance;
        if (localInstance == null) {
            synchronized (StepExecutorFactory.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new StepExecutorFactory();
                }
            }
        }
        return localInstance;
    }

    private StepExecutorFactory() {
        addBinding(TestCaseStep.class, new TestCaseExecutor());
        addBinding(UIStepImpl.class, new UIStepExecutor());
//        addBinding(GroupStep.class, new TestCaseGroupStepExecutor());
    }


    private void addBinding(Class<? extends Step> stepType, StepExecutor executor) {
        executorBindings.put(stepType, executor);
    }

    public StepExecutor getExecutor(StepInstance step) {
        for (Map.Entry<Class<? extends Step>, StepExecutor> entry : executorBindings.entrySet()) {
            if (entry.getKey().isAssignableFrom(step.getStep().getClass())) {
                return entry.getValue();
            }
        }
        throw new IllegalArgumentException(String.format("No executor found for step [%s], type [%s]", step.getStep(), step.getStep().getClass().getSimpleName()));
    }
}

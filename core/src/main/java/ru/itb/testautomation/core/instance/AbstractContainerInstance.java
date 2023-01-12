package ru.itb.testautomation.core.instance;

import com.google.common.collect.Lists;
import ru.itb.testautomation.core.instance.step.StepInstance;
import ru.itb.testautomation.core.instance.step.StepIterator;
import ru.itb.testautomation.core.instance.step.intf.StepContainer;

import java.util.List;

public abstract class AbstractContainerInstance extends AbstractInstance {

    private List<StepInstance> stepInstances = Lists.newArrayListWithExpectedSize(3);

    public List<StepInstance> getStepInstances() {
        return stepInstances;
    }

    public abstract StepIterator iterator();

    public abstract StepContainer getStepContainer();

    public abstract void setStepContainer(StepContainer container);

    public String toString() {
        if (getStepContainer() != null){
            return String.format("Instance: [%s]", getStepContainer().getName());
        }
        return null;
    }
}

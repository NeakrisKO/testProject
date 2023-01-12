package ru.itb.testautomation.core.instance.step;

import ru.itb.testautomation.core.instance.AbstractContainerInstance;
import ru.itb.testautomation.core.instance.AbstractInstance;
import ru.itb.testautomation.core.instance.context.InstanceContextImpl;
import ru.itb.testautomation.core.instance.step.intf.Step;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StepInstance extends AbstractInstance {

    private static final Logger LOGGER =  LogManager.getLogger(StepInstance.class);
    private Step step;
    private String imageName;

    public StepInstance() { }


    public StepInstance(Step step) {
        this.step = step;
    }

    public StepInstance(Step step, InstanceContextImpl context) {
        this.step = step;
        getContext().putAll(context);
    }

    public void setStep(Step step) {
        this.step = step;
    }

    public Step getStep() {
        return step;
    }

    @Override
    public AbstractContainerInstance getParent() {
        return (AbstractContainerInstance) super.getParent();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof StepInstance && (obj == this || ((StepInstance) obj).getParent() == getParent() && ((StepInstance) obj).getStep().equals(step));
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}

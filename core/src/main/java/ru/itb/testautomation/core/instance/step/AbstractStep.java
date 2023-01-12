package ru.itb.testautomation.core.instance.step;

import ru.itb.testautomation.core.instance.step.intf.Step;

public abstract class AbstractStep implements Step {
    public abstract Object getId();
    public abstract void setId(Object id);
    private boolean isEnable = true;
    private boolean manualStart = false;

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public boolean isManualStart() {
        return manualStart;
    }

    public void setManualStart(boolean manualStart) {
        this.manualStart = manualStart;
    }

}

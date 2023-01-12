package ru.itb.testautomation.core.instance.step;

import ru.itb.testautomation.core.instance.AbstractContainerInstance;
import ru.itb.testautomation.core.instance.step.intf.Step;

import java.util.Iterator;
import java.util.List;

public class StepIterator implements Iterator<StepInstance> {
    private Iterator<Step> iterator;
    private StepInstance current;
    private AbstractContainerInstance parent;

    public StepIterator(List<Step> steps, AbstractContainerInstance parent) {
        this.parent = parent;
        this.iterator = steps.iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public StepInstance next() {
        current = new StepInstance();
        current.setStep(iterator.next());
        current.setParent(parent);
        current.getContext().putAll(parent.getContext());
        return current;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Cannot remove steps from situation");
    }

    public StepInstance current() {
        return current;
    }
}

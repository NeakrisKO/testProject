package ru.itb.testautomation.core.instance.step.event;

import ru.itb.testautomation.core.event.Event;
import ru.itb.testautomation.core.instance.step.StepInstance;

public abstract class StepEvent extends Event {
    private StepInstance stepInstance;

    private StepEvent(StepInstance stepInstance) {
        this.stepInstance = stepInstance;
    }

    public StepInstance getStepInstance() {
        return stepInstance;
    }

    public static class Start extends StepEvent {

        public Start(StepInstance stepInstance) {
            super(stepInstance);
        }
    }

    public static class Finish extends StepEvent {

        public Finish(StepInstance stepInstance) {
            super(stepInstance);
        }
    }

    public static class Terminate extends StepEvent {

        public Terminate(StepInstance stepInstance) {
            super(stepInstance);
        }
    }

    public static class Skip extends StepEvent {

        public Skip(StepInstance stepInstance) {
            super(stepInstance);
        }
    }
}

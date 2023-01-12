package ru.itb.testautomation.core.instance.step;

public enum StepType {
    UISTEP {
        @Override
        public String toString() {
            return "UISTEP";
        }
    }, EMAILSTEP {
        @Override
        public String toString() {
            return "EMAILSTEP";
        }
    };

    @Override
    public abstract String toString();
}

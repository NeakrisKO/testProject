package ru.itb.testautomation.core.instance;

public enum Status {
    NOT_STARTED {
        @Override
        public String toString() {
            return "Not Started";
        }
    }, IN_PROGRESS {
        @Override
        public String toString() {
            return "In Progress";
        }
    }, PASSED {
        @Override
        public String toString() {
            return "Passed";
        }
    }, FAILED {
        @Override
        public String toString() {
            return "Failed";
        }
    },PAUSED {
        @Override
        public String toString() {
            return "Paused";
        }
    }, STOPPED {
        @Override
        public String toString() {
            return "Stopped";
        }
    };

    @Override
    public abstract String toString();
}

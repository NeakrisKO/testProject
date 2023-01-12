package ru.itb.testautomation.core.instance.testcase.event;

import ru.itb.testautomation.core.event.Event;
import ru.itb.testautomation.core.instance.testcase.TestCaseInstance;

public class TestCaseEvent extends Event {
    private TestCaseInstance instance;

    public TestCaseEvent(TestCaseInstance instance) {
        this.instance = instance;
    }

    public TestCaseInstance getTestCaseInstance() {
        return instance;
    }

    public static class Start extends TestCaseEvent {

        public Start(TestCaseInstance instance) {
            super(instance);
        }
    }

    public static class Skip extends TestCaseEvent {

        public Skip(TestCaseInstance instance) {
            super(instance);
        }
    }

    public static class Finish extends TestCaseEvent {

        public Finish(TestCaseInstance instance) {
            super(instance);
        }
    }

    public static class Terminate extends TestCaseEvent {

        public Terminate(TestCaseInstance instance) {
            super(instance);
        }
    }

    public static class Stopped extends TestCaseEvent {

        public Stopped(TestCaseInstance instance) {
            super(instance);
        }
    }
}

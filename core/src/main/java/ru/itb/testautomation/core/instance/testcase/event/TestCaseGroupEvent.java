package ru.itb.testautomation.core.instance.testcase.event;

import ru.itb.testautomation.core.event.Event;
import ru.itb.testautomation.core.instance.testcase.TestCaseInstance;

public abstract class TestCaseGroupEvent extends Event {
    private TestCaseInstance testCaseInstance;

    public TestCaseGroupEvent(TestCaseInstance testCaseInstance) {
        this.testCaseInstance = testCaseInstance;
    }

    public TestCaseInstance getTestCaseInstance() {
        return testCaseInstance;
    }

    public static class Start extends TestCaseGroupEvent {
        public  Start(TestCaseInstance testCaseInstance) {
            super(testCaseInstance);
        }
    }

    public static class Finish extends TestCaseGroupEvent {
        public Finish(TestCaseInstance testCaseInstance) {
            super(testCaseInstance);
        }
    }

    public static class Terminate extends TestCaseGroupEvent {
        public Terminate(TestCaseInstance testCaseInstance) {
            super(testCaseInstance);
        }
    }
}

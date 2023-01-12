package ru.itb.testautomation.core.instance.testcase.event;

import com.google.common.eventbus.Subscribe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itb.testautomation.core.context.TCContext;
import ru.itb.testautomation.core.event.EventBusProvider;
import ru.itb.testautomation.core.instance.step.StepIterator;

public class FireNextTestCaseSubscriber {
    private static final Logger LOGGER = LogManager.getLogger(FireNextTestCaseSubscriber.class);
    private final TCContext context;
    private StepIterator stepIterator;

    public FireNextTestCaseSubscriber(TCContext context,StepIterator stepIterator) {
        this.context = context;
        this.stepIterator = stepIterator;
    }

    @Subscribe
    public void handle(TestCaseEvent.Finish finishEvent) {
        if (finishEvent.getTestCaseInstance().getContext().tc().getId().toString().equals(context.getId().toString())) {
            finishEvent.stop();
            LOGGER.info("Got event TestCase finished in context . Will trigger next TestCase in group...");
            EventBusProvider.getInstance().post(new NextGroupStepEvent(context));
            EventBusProvider.getInstance().unregister(this);
        } else {
            LOGGER.debug("Ignoring event {}", finishEvent);
        }
    }
}

package ru.itb.testautomation.core.common.inject;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

public class SpringInjection {
    private static final SpringInjection INSTANCE = new SpringInjection();
    private ConfigurableApplicationContext context;

    private SpringInjection() {
        context = new GenericXmlApplicationContext("classpath:META-INF/testautomation.injection.spring.config.xml");
    }

    public static SpringInjection getInstance() {
        return INSTANCE;
    }

    public ApplicationContext getContext() {
        return context;
    }
}

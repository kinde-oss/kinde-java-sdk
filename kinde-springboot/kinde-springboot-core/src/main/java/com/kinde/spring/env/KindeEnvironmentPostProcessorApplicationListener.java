package com.kinde.spring.env;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

public class KindeEnvironmentPostProcessorApplicationListener implements SmartApplicationListener, Ordered {

    private static final Logger log = LoggerFactory.getLogger(KindeEnvironmentPostProcessorApplicationListener.class);

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationPreparedEvent) {
            ConfigurableEnvironment environment = ((ApplicationPreparedEvent) event).getApplicationContext().getEnvironment();
            // add extra validation

            if (environment.getProperty("kinde.oauth2.domain") == null) {
                log.warn("Please make sure Kinde is correctly configured.");
            }
        }
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return ApplicationPreparedEvent.class.isAssignableFrom(eventType);
    }

}

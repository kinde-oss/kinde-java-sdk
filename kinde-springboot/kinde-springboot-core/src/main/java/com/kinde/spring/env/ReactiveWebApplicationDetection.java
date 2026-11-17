package com.kinde.spring.env;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.core.env.Environment;

/**
 * Tells, during environment post-processing, whether the application is a reactive (WebFlux)
 * web application.
 */
public final class ReactiveWebApplicationDetection {

    private ReactiveWebApplicationDetection() {
    }

    /**
     * @param environment the environment being prepared
     * @param application the application being started, or null if unknown
     */
    public static boolean isReactive(Environment environment, SpringApplication application) {
        // spring.main.web-application-type is only applied to the SpringApplication after
        // environment post-processing, so read it here as well
        String configured = environment.getProperty("spring.main.web-application-type");
        if (configured != null && !configured.isBlank()) {
            return "reactive".equalsIgnoreCase(configured.trim());
        }
        return application != null && application.getWebApplicationType() == WebApplicationType.REACTIVE;
    }
}

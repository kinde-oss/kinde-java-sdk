package com.kinde.guice;

import jakarta.inject.Qualifier;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class KindeAnnotations {

    @Qualifier
    @Retention(RUNTIME)
    public @interface ClientConfigParameters {}


    @Qualifier
    @Retention(RUNTIME)
    public @interface ClientConfigDomain {}

    @Qualifier
    @Retention(RUNTIME)
    public @interface KindeToken {}

    @Qualifier
    @Retention(RUNTIME)
    public @interface KindeCode {}

    @Qualifier
    @Retention(RUNTIME)
    public @interface AuthorizationUrl {}
}

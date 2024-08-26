package com.kinde.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class KindeGuiceSingleton {

    private static KindeGuiceSingleton instance = null;

    private final Injector injector;

    private KindeGuiceSingleton(Module... modules) {
        this.injector = Guice.createInjector(modules);
    }

    public static synchronized KindeGuiceSingleton init(Module... modules) {
        instance = new KindeGuiceSingleton(modules);
        return instance;
    }

    public static synchronized void fin() {
        instance = null;
    }

    public static synchronized KindeGuiceSingleton getInstance(Module... modules) {
        if (instance == null) {
            instance = new KindeGuiceSingleton(modules);
        }
        return instance;
    }

    public Injector getInjector() {
        return this.injector;
    }
}

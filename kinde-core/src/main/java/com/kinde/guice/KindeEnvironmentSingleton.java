package com.kinde.guice;

public class KindeEnvironmentSingleton {

    public enum State {
        ACTIVE,
        TEST
    }

    private static KindeEnvironmentSingleton instance = null;

    private final State state;

    private KindeEnvironmentSingleton(State state) {
        this.state = state;
    }

    public static synchronized KindeEnvironmentSingleton getInstance() {
        if (instance == null) {
            instance = new KindeEnvironmentSingleton(State.ACTIVE);
        }
        return instance;
    }

    public static synchronized KindeEnvironmentSingleton init(State state) {
        if (instance == null) {
            instance = new KindeEnvironmentSingleton(state);
        }
        return instance;
    }

    public State getState() {
        return this.state;
    }
}

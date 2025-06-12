package com.kinde.exceptions;

public class KindeClientSessionException extends Exception {
    public KindeClientSessionException(String message) {
        super(message);
    }

    public KindeClientSessionException(String message, Throwable cause) {
        super(message, cause);
    }

    public KindeClientSessionException(Throwable cause) {
        super(cause);
    }
}

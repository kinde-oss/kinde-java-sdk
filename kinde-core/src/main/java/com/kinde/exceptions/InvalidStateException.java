package com.kinde.exceptions;

/**
 * Thrown when the {@code state} on an authorization callback is missing or does not match the
 * pending authorization request. The callback must not be processed.
 */
public class InvalidStateException extends RuntimeException {

    public InvalidStateException(String message) {
        super(message);
    }
}

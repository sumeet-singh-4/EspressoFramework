package com.espresso.framework.exceptions;

/**
 * Base exception for Espresso Framework
 * All custom exceptions should extend this class
 */
public class EspressoFrameworkException extends RuntimeException {

    public EspressoFrameworkException(String message) {
        super(message);
    }

    public EspressoFrameworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public EspressoFrameworkException(Throwable cause) {
        super(cause);
    }
}

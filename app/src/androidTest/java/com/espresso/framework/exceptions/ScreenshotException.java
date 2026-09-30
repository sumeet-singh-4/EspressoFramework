package com.espresso.framework.exceptions;

/**
 * Exception thrown when screenshot capture fails
 */
public class ScreenshotException extends EspressoFrameworkException {

    public ScreenshotException(String message) {
        super(message);
    }

    public ScreenshotException(String message, Throwable cause) {
        super(message, cause);
    }
}

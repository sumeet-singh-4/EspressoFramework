package com.espresso.framework.exceptions;

/**
 * Exception thrown when a test operation times out
 * Similar to TimeoutException in Appium
 */
public class TestTimeoutException extends EspressoFrameworkException {

    public TestTimeoutException(String message) {
        super(message);
    }

    public TestTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public TestTimeoutException(String operation, int timeoutSeconds) {
        super(String.format("Timeout after %d seconds waiting for: %s", timeoutSeconds, operation));
    }
}

package com.espresso.framework.exceptions;

/**
 * Exception thrown when reading test data fails
 * Such as JSON parsing errors, Excel reading errors, or file not found
 */
public class DataReadException extends EspressoFrameworkException {

    public DataReadException(String message) {
        super(message);
    }

    public DataReadException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor for file read errors with format
     */
    public DataReadException(String filePath, String format, Throwable cause) {
        super(String.format("Failed to read %s data from: %s", format, filePath), cause);
    }
}

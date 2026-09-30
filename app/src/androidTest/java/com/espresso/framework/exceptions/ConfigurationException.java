package com.espresso.framework.exceptions;

/**
 * Exception thrown when configuration errors occur
 * Such as missing properties, invalid values, or environment setup issues
 */
public class ConfigurationException extends EspressoFrameworkException {

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor for missing property
     */
    public ConfigurationException(String property, String environment) {
        super(String.format("Configuration property '%s' not found for environment: %s", property, environment));
    }
}

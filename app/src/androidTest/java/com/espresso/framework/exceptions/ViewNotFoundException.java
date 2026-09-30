package com.espresso.framework.exceptions;

import android.view.View;
import org.hamcrest.Matcher;

/**
 * Exception thrown when a view is not found
 * Similar to NoSuchElementException in Appium
 */
public class ViewNotFoundException extends EspressoFrameworkException {

    private final String matcherDescription;

    public ViewNotFoundException(String message) {
        super(message);
        this.matcherDescription = null;
    }

    public ViewNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.matcherDescription = null;
    }

    /**
     * Constructor with Matcher for better error context
     */
    public ViewNotFoundException(Matcher<View> viewMatcher, Throwable cause) {
        super("View not found: " + viewMatcher.toString(), cause);
        this.matcherDescription = viewMatcher.toString();
    }

    /**
     * Get the matcher description
     */
    public String getMatcherDescription() {
        return matcherDescription;
    }
}

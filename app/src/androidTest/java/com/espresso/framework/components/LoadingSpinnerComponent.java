package com.espresso.framework.components;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;

import android.view.View;

import org.hamcrest.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.Matchers.containsString;

/**
 * Reusable Loading Spinner Component
 * Handles loading indicators, progress bars, and spinners
 */
public class LoadingSpinnerComponent {
    private static final Logger logger = LoggerFactory.getLogger(LoadingSpinnerComponent.class);

    // Common loading spinner class names
    private static final String PROGRESS_BAR_CLASS = "android.widget.ProgressBar";
    private static final String LOADING_INDICATOR_CLASS = "LoadingIndicator";

    private final Matcher<View> spinnerMatcher;

    /**
     * Default constructor - uses standard Android ProgressBar
     */
    public LoadingSpinnerComponent() {
        this.spinnerMatcher = withClassName(containsString(PROGRESS_BAR_CLASS));
    }

    /**
     * Custom constructor with specific matcher
     */
    public LoadingSpinnerComponent(Matcher<View> spinnerMatcher) {
        this.spinnerMatcher = spinnerMatcher;
    }

    /**
     * Wait for loading spinner to appear
     */
    public void waitForSpinner() {
        waitForSpinner(5);
    }

    /**
     * Wait for loading spinner to appear with custom timeout
     */
    public void waitForSpinner(int timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        long timeout = timeoutSeconds * 1000L;

        while (System.currentTimeMillis() - startTime < timeout) {
            try {
                onView(spinnerMatcher).check(matches(isDisplayed()));
                logger.info("Loading spinner appeared");
                return;
            } catch (Exception e) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        logger.debug("Loading spinner did not appear within {} seconds", timeoutSeconds);
    }

    /**
     * Wait for loading spinner to disappear
     */
    public void waitForSpinnerToDisappear() {
        waitForSpinnerToDisappear(30);
    }

    /**
     * Wait for loading spinner to disappear with custom timeout
     */
    public void waitForSpinnerToDisappear(int timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        long timeout = timeoutSeconds * 1000L;

        while (System.currentTimeMillis() - startTime < timeout) {
            try {
                onView(spinnerMatcher).check(doesNotExist());
                logger.info("Loading spinner disappeared");
                return;
            } catch (Exception e) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        logger.warn("Loading spinner still visible after {} seconds", timeoutSeconds);
    }

    /**
     * Check if loading spinner is displayed
     */
    public boolean isDisplayed() {
        try {
            onView(spinnerMatcher).check(matches(isDisplayed()));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if loading is complete (spinner not displayed)
     */
    public boolean isLoadingComplete() {
        return !isDisplayed();
    }

    /**
     * Wait for loading to complete
     * This waits for the spinner to appear and then disappear
     */
    public void waitForLoadingComplete() {
        waitForLoadingComplete(30);
    }

    /**
     * Wait for loading to complete with custom timeout
     */
    public void waitForLoadingComplete(int timeoutSeconds) {
        // First check if spinner is visible
        if (isDisplayed()) {
            logger.info("Loading spinner is visible, waiting for it to disappear");
            waitForSpinnerToDisappear(timeoutSeconds);
        } else {
            // If not visible, wait a bit to see if it appears
            waitForSpinner(2);
            if (isDisplayed()) {
                waitForSpinnerToDisappear(timeoutSeconds);
            } else {
                logger.info("Loading spinner never appeared, assuming loading is complete");
            }
        }
        logger.info("Loading completed");
    }

    /**
     * Static helper - wait for default ProgressBar to disappear
     */
    public static void waitForDefaultSpinner() {
        LoadingSpinnerComponent spinner = new LoadingSpinnerComponent();
        spinner.waitForSpinnerToDisappear();
    }

    /**
     * Static helper - wait for loading to complete using default spinner
     */
    public static void waitForDefaultLoadingComplete() {
        LoadingSpinnerComponent spinner = new LoadingSpinnerComponent();
        spinner.waitForLoadingComplete();
    }

    /**
     * Wait for any loading indicators to complete
     * Checks multiple common loading indicator patterns
     */
    public static void waitForAnyLoadingComplete() {
        waitForAnyLoadingComplete(30);
    }

    /**
     * Wait for any loading indicators to complete with custom timeout
     */
    public static void waitForAnyLoadingComplete(int timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        long timeout = timeoutSeconds * 1000L;

        while (System.currentTimeMillis() - startTime < timeout) {
            boolean anyLoading = false;

            // Check for ProgressBar
            try {
                onView(withClassName(containsString(PROGRESS_BAR_CLASS)))
                    .check(matches(isDisplayed()));
                anyLoading = true;
            } catch (Exception e) {
                // Not found or not displayed
            }

            if (!anyLoading) {
                logger.info("All loading indicators completed");
                return;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        logger.warn("Some loading indicators still visible after {} seconds", timeoutSeconds);
    }
}

package com.espresso.framework.components;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;

import org.hamcrest.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reusable Alert Dialog Component
 * Handles common alert dialogs in Android apps
 */
public class AlertDialogComponent {
    private static final Logger logger = LoggerFactory.getLogger(AlertDialogComponent.class);

    // Common Android alert dialog IDs
    private static final int ALERT_TITLE_ID = android.R.id.title;
    private static final int ALERT_MESSAGE_ID = android.R.id.message;
    private static final int BUTTON_POSITIVE_ID = android.R.id.button1;
    private static final int BUTTON_NEGATIVE_ID = android.R.id.button2;
    private static final int BUTTON_NEUTRAL_ID = android.R.id.button3;

    /**
     * Get alert title text
     */
    public static String getTitle() {
        final String[] text = {""};
        try {
            onView(withId(ALERT_TITLE_ID))
                .check(matches(isDisplayed()))
                .perform(new androidx.test.espresso.ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return isDisplayed();
                    }

                    @Override
                    public String getDescription() {
                        return "Get text from alert title";
                    }

                    @Override
                    public void perform(androidx.test.espresso.UiController uiController, View view) {
                        if (view instanceof android.widget.TextView) {
                            text[0] = ((android.widget.TextView) view).getText().toString();
                        }
                    }
                });
            logger.info("Alert title: {}", text[0]);
            return text[0];
        } catch (Exception e) {
            logger.warn("Could not get alert title", e);
            return "";
        }
    }

    /**
     * Get alert message text
     */
    public static String getMessage() {
        final String[] text = {""};
        try {
            onView(withId(ALERT_MESSAGE_ID))
                .check(matches(isDisplayed()))
                .perform(new androidx.test.espresso.ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return isDisplayed();
                    }

                    @Override
                    public String getDescription() {
                        return "Get text from alert message";
                    }

                    @Override
                    public void perform(androidx.test.espresso.UiController uiController, View view) {
                        if (view instanceof android.widget.TextView) {
                            text[0] = ((android.widget.TextView) view).getText().toString();
                        }
                    }
                });
            logger.info("Alert message: {}", text[0]);
            return text[0];
        } catch (Exception e) {
            logger.warn("Could not get alert message", e);
            return "";
        }
    }

    /**
     * Click positive button (OK, Yes, Confirm, etc.)
     */
    public static void clickPositive() {
        try {
            onView(withId(BUTTON_POSITIVE_ID))
                .check(matches(isDisplayed()))
                .perform(click());
            logger.info("Clicked positive button on alert");
        } catch (Exception e) {
            logger.error("Failed to click positive button", e);
            throw e;
        }
    }

    /**
     * Click negative button (Cancel, No, Dismiss, etc.)
     */
    public static void clickNegative() {
        try {
            onView(withId(BUTTON_NEGATIVE_ID))
                .check(matches(isDisplayed()))
                .perform(click());
            logger.info("Clicked negative button on alert");
        } catch (Exception e) {
            logger.error("Failed to click negative button", e);
            throw e;
        }
    }

    /**
     * Click neutral button (Later, Maybe, etc.)
     */
    public static void clickNeutral() {
        try {
            onView(withId(BUTTON_NEUTRAL_ID))
                .check(matches(isDisplayed()))
                .perform(click());
            logger.info("Clicked neutral button on alert");
        } catch (Exception e) {
            logger.error("Failed to click neutral button", e);
            throw e;
        }
    }

    /**
     * Accept alert (click positive button)
     */
    public static void accept() {
        clickPositive();
    }

    /**
     * Dismiss alert (click negative button)
     */
    public static void dismiss() {
        clickNegative();
    }

    /**
     * Check if alert is displayed
     */
    public static boolean isDisplayed() {
        try {
            onView(withId(ALERT_TITLE_ID)).check(matches(isDisplayed()));
            return true;
        } catch (Exception e) {
            try {
                onView(withId(ALERT_MESSAGE_ID)).check(matches(isDisplayed()));
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }

    /**
     * Wait for alert to appear
     */
    public static void waitForAlert() {
        waitForAlert(10);
    }

    /**
     * Wait for alert to appear with custom timeout
     */
    public static void waitForAlert(int timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        long timeout = timeoutSeconds * 1000L;

        while (System.currentTimeMillis() - startTime < timeout) {
            if (isDisplayed()) {
                logger.info("Alert appeared");
                return;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        logger.error("Alert did not appear within {} seconds", timeoutSeconds);
        throw new RuntimeException("Alert not displayed after " + timeoutSeconds + " seconds");
    }

    /**
     * Wait for alert to disappear
     */
    public static void waitForAlertDismissal() {
        waitForAlertDismissal(10);
    }

    /**
     * Wait for alert to disappear with custom timeout
     */
    public static void waitForAlertDismissal(int timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        long timeout = timeoutSeconds * 1000L;

        while (System.currentTimeMillis() - startTime < timeout) {
            if (!isDisplayed()) {
                logger.info("Alert dismissed");
                return;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        logger.error("Alert still visible after {} seconds", timeoutSeconds);
    }

    /**
     * Click button by text
     */
    public static void clickButtonWithText(String buttonText) {
        try {
            onView(withText(buttonText))
                .check(matches(isDisplayed()))
                .perform(click());
            logger.info("Clicked button with text: {}", buttonText);
        } catch (Exception e) {
            logger.error("Failed to click button with text: {}", buttonText, e);
            throw e;
        }
    }

    /**
     * Check if positive button is displayed
     */
    public static boolean isPositiveButtonDisplayed() {
        try {
            onView(withId(BUTTON_POSITIVE_ID)).check(matches(isDisplayed()));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if negative button is displayed
     */
    public static boolean isNegativeButtonDisplayed() {
        try {
            onView(withId(BUTTON_NEGATIVE_ID)).check(matches(isDisplayed()));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

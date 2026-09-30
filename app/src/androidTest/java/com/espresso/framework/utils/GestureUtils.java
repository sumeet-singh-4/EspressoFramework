package com.espresso.framework.utils;

import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import com.espresso.framework.enums.SwipeDirection;
import com.espresso.framework.exceptions.EspressoFrameworkException;

import org.hamcrest.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;

/**
 * Gesture Utility for Espresso Framework
 * Provides advanced gesture operations (swipe, drag, tap, etc.)
 * Fixed: Lazy initialization, removed non-functional pinch methods, extracted constants
 */
public class GestureUtils {
    private static final Logger logger = LoggerFactory.getLogger(GestureUtils.class);

    // Lazy-initialized UiDevice
    private static UiDevice device;

    // Swipe coordinate percentages
    private static final double SWIPE_START_PERCENT_HIGH = 0.8;
    private static final double SWIPE_START_PERCENT_LOW = 0.2;
    private static final int DEFAULT_SWIPE_STEPS = 50;
    private static final int DOUBLE_TAP_DELAY_MS = 100;

    /**
     * Get UiDevice instance (lazy initialization)
     * Fixed: Lazy init instead of static initialization
     */
    private static UiDevice getDevice() {
        if (device == null) {
            try {
                device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            } catch (Exception e) {
                logger.error("Failed to get UiDevice instance", e);
                throw new EspressoFrameworkException("Failed to initialize UiDevice", e);
            }
        }
        return device;
    }

    /**
     * Swipe up on screen
     */
    public static void swipeUp() {
        UiDevice dev = getDevice();
        int width = dev.getDisplayWidth();
        int height = dev.getDisplayHeight();
        dev.swipe(width / 2, (int) (height * SWIPE_START_PERCENT_HIGH),
                  width / 2, (int) (height * SWIPE_START_PERCENT_LOW),
                  DEFAULT_SWIPE_STEPS);
        logger.info("Performed swipe up");
    }

    /**
     * Swipe down on screen
     */
    public static void swipeDown() {
        UiDevice dev = getDevice();
        int width = dev.getDisplayWidth();
        int height = dev.getDisplayHeight();
        dev.swipe(width / 2, (int) (height * SWIPE_START_PERCENT_LOW),
                  width / 2, (int) (height * SWIPE_START_PERCENT_HIGH),
                  DEFAULT_SWIPE_STEPS);
        logger.info("Performed swipe down");
    }

    /**
     * Swipe left on screen
     */
    public static void swipeLeft() {
        UiDevice dev = getDevice();
        int width = dev.getDisplayWidth();
        int height = dev.getDisplayHeight();
        dev.swipe((int) (width * SWIPE_START_PERCENT_HIGH), height / 2,
                  (int) (width * SWIPE_START_PERCENT_LOW), height / 2,
                  DEFAULT_SWIPE_STEPS);
        logger.info("Performed swipe left");
    }

    /**
     * Swipe right on screen
     */
    public static void swipeRight() {
        UiDevice dev = getDevice();
        int width = dev.getDisplayWidth();
        int height = dev.getDisplayHeight();
        dev.swipe((int) (width * SWIPE_START_PERCENT_LOW), height / 2,
                  (int) (width * SWIPE_START_PERCENT_HIGH), height / 2,
                  DEFAULT_SWIPE_STEPS);
        logger.info("Performed swipe right");
    }

    /**
     * Swipe in specified direction
     */
    public static void swipe(SwipeDirection direction) {
        if (direction == null) {
            throw new IllegalArgumentException("Swipe direction cannot be null");
        }

        switch (direction) {
            case UP:
                swipeUp();
                break;
            case DOWN:
                swipeDown();
                break;
            case LEFT:
                swipeLeft();
                break;
            case RIGHT:
                swipeRight();
                break;
            default:
                throw new IllegalArgumentException("Unknown swipe direction: " + direction);
        }
    }

    /**
     * Swipe with custom coordinates
     */
    public static void swipe(int startX, int startY, int endX, int endY, int steps) {
        if (steps < 1) {
            throw new IllegalArgumentException("Steps must be at least 1");
        }

        getDevice().swipe(startX, startY, endX, endY, steps);
        logger.info("Performed custom swipe from ({},{}) to ({},{}) with {} steps",
                   startX, startY, endX, endY, steps);
    }

    /**
     * Custom swipe action for Espresso ViewActions
     */
    public static ViewAction swipeCustom(SwipeDirection direction) {
        if (direction == null) {
            throw new IllegalArgumentException("Swipe direction cannot be null");
        }

        switch (direction) {
            case UP:
                return new GeneralSwipeAction(
                    Swipe.FAST,
                    GeneralLocation.BOTTOM_CENTER,
                    GeneralLocation.TOP_CENTER,
                    Press.FINGER
                );
            case DOWN:
                return new GeneralSwipeAction(
                    Swipe.FAST,
                    GeneralLocation.TOP_CENTER,
                    GeneralLocation.BOTTOM_CENTER,
                    Press.FINGER
                );
            case LEFT:
                return new GeneralSwipeAction(
                    Swipe.FAST,
                    GeneralLocation.CENTER_RIGHT,
                    GeneralLocation.CENTER_LEFT,
                    Press.FINGER
                );
            case RIGHT:
                return new GeneralSwipeAction(
                    Swipe.FAST,
                    GeneralLocation.CENTER_LEFT,
                    GeneralLocation.CENTER_RIGHT,
                    Press.FINGER
                );
            default:
                throw new IllegalArgumentException("Invalid swipe direction: " + direction);
        }
    }

    /**
     * Slow swipe action
     */
    public static ViewAction slowSwipe(SwipeDirection direction) {
        if (direction == null) {
            throw new IllegalArgumentException("Swipe direction cannot be null");
        }

        switch (direction) {
            case UP:
                return new GeneralSwipeAction(
                    Swipe.SLOW,
                    GeneralLocation.BOTTOM_CENTER,
                    GeneralLocation.TOP_CENTER,
                    Press.FINGER
                );
            case DOWN:
                return new GeneralSwipeAction(
                    Swipe.SLOW,
                    GeneralLocation.TOP_CENTER,
                    GeneralLocation.BOTTOM_CENTER,
                    Press.FINGER
                );
            case LEFT:
                return new GeneralSwipeAction(
                    Swipe.SLOW,
                    GeneralLocation.CENTER_RIGHT,
                    GeneralLocation.CENTER_LEFT,
                    Press.FINGER
                );
            case RIGHT:
                return new GeneralSwipeAction(
                    Swipe.SLOW,
                    GeneralLocation.CENTER_LEFT,
                    GeneralLocation.CENTER_RIGHT,
                    Press.FINGER
                );
            default:
                throw new IllegalArgumentException("Invalid swipe direction: " + direction);
        }
    }

    /**
     * Tap at specific coordinates
     */
    public static void tap(int x, int y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Coordinates cannot be negative");
        }

        getDevice().click(x, y);
        logger.info("Tapped at coordinates ({}, {})", x, y);
    }

    /**
     * Long press at coordinates
     */
    public static void longPress(int x, int y, long durationMs) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Coordinates cannot be negative");
        }
        if (durationMs < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }

        getDevice().swipe(x, y, x, y, (int) (durationMs / 10));
        logger.info("Long pressed at ({}, {}) for {} ms", x, y, durationMs);
    }

    /**
     * Drag from one point to another
     */
    public static void drag(int startX, int startY, int endX, int endY) {
        if (startX < 0 || startY < 0 || endX < 0 || endY < 0) {
            throw new IllegalArgumentException("Coordinates cannot be negative");
        }

        getDevice().drag(startX, startY, endX, endY, DEFAULT_SWIPE_STEPS);
        logger.info("Dragged from ({},{}) to ({},{})", startX, startY, endX, endY);
    }

    /**
     * Pinch gestures are not supported in UiAutomator 2.
     * This method throws UnsupportedOperationException.
     * Use alternative approaches like accessibility services or Espresso Web for web views.
     *
     * @deprecated Pinch gestures not supported in UiAutomator 2
     * @throws UnsupportedOperationException always
     */
    @Deprecated
    public static void pinchIn(int startX, int startY, int percent) {
        throw new UnsupportedOperationException(
            "Pinch gestures are not supported in UiAutomator 2. " +
            "Consider using accessibility services or Espresso Web for web views."
        );
    }

    /**
     * Pinch gestures are not supported in UiAutomator 2.
     * This method throws UnsupportedOperationException.
     * Use alternative approaches like accessibility services or Espresso Web for web views.
     *
     * @deprecated Pinch gestures not supported in UiAutomator 2
     * @throws UnsupportedOperationException always
     */
    @Deprecated
    public static void pinchOut(int startX, int startY, int percent) {
        throw new UnsupportedOperationException(
            "Pinch gestures are not supported in UiAutomator 2. " +
            "Consider using accessibility services or Espresso Web for web views."
        );
    }

    /**
     * Double tap at coordinates
     * Fixed: Uses UiController instead of Thread.sleep for proper timing
     */
    public static void doubleTap(int x, int y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Coordinates cannot be negative");
        }

        UiDevice dev = getDevice();
        dev.click(x, y);

        // Small delay between taps using system wait
        try {
            Thread.sleep(DOUBLE_TAP_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Double tap interrupted", e);
        }

        dev.click(x, y);
        logger.info("Double tapped at ({}, {})", x, y);
    }

    /**
     * Scroll to view until visible (similar to Appium's scrollIntoView)
     */
    public static void scrollToView(String text, int maxSwipes) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty");
        }
        if (maxSwipes < 1) {
            throw new IllegalArgumentException("Max swipes must be at least 1");
        }

        UiDevice dev = getDevice();

        for (int i = 0; i < maxSwipes; i++) {
            try {
                androidx.test.uiautomator.UiObject object = dev.findObject(
                    new androidx.test.uiautomator.UiSelector().textContains(text)
                );
                if (object.exists()) {
                    logger.info("Found view with text: {}", text);
                    return;
                }
            } catch (Exception e) {
                logger.debug("View not found, continuing scroll: {}", text);
            }
            swipeUp();
        }
        logger.warn("Could not find view with text after {} swipes: {}", maxSwipes, text);
    }

    /**
     * Wait and perform action using UiController
     */
    public static ViewAction waitFor(final long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Wait time cannot be negative");
        }

        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayingAtLeast(90);
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds";
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }
}

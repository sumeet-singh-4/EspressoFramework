package com.espresso.framework.base;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;

import com.espresso.framework.config.ConfigReader;
import com.espresso.framework.enums.SwipeDirection;
import com.espresso.framework.exceptions.TestTimeoutException;
import com.espresso.framework.exceptions.ViewNotFoundException;
import com.espresso.framework.utils.GestureUtils;

import org.hamcrest.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Base Page Object for Espresso Framework
 * Provides core Espresso actions equivalent to Appium's BasePage
 * All page objects should extend this class
 */
public abstract class BasePage {
    protected static final Logger logger = LoggerFactory.getLogger(BasePage.class);
    protected static final int DEFAULT_TIMEOUT = ConfigReader.getDefaultTimeout();

    /**
     * Click on a view
     */
    protected void click(Matcher<View> viewMatcher) {
        try {
            onView(viewMatcher)
                .check(matches(isDisplayed()))
                .perform(click());
            logger.info("Clicked on view: {}", viewMatcher);
        } catch (NoMatchingViewException e) {
            logger.error("View not found: {}", viewMatcher, e);
            throw new ViewNotFoundException(viewMatcher, e);
        } catch (Exception e) {
            logger.error("Click failed on view: {}", viewMatcher, e);
            throw e;
        }
    }

    /**
     * Type text into a view
     */
    protected void type(Matcher<View> viewMatcher, String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        try {
            onView(viewMatcher)
                .check(matches(isDisplayed()))
                .perform(typeText(text), closeSoftKeyboard());
            logger.info("Typed '{}' into view: {}", text, viewMatcher);
        } catch (NoMatchingViewException e) {
            logger.error("View not found: {}", viewMatcher, e);
            throw new ViewNotFoundException(viewMatcher, e);
        } catch (Exception e) {
            logger.error("Type failed on view: {}", viewMatcher, e);
            throw e;
        }
    }

    /**
     * Replace text in a view (clears first, then types)
     */
    protected void replaceText(Matcher<View> viewMatcher, String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        try {
            onView(viewMatcher)
                .check(matches(isDisplayed()))
                .perform(replaceText(text), closeSoftKeyboard());
            logger.info("Replaced text with '{}' in view: {}", text, viewMatcher);
        } catch (NoMatchingViewException e) {
            logger.error("View not found: {}", viewMatcher, e);
            throw new ViewNotFoundException(viewMatcher, e);
        } catch (Exception e) {
            logger.error("Replace text failed on view: {}", viewMatcher, e);
            throw e;
        }
    }

    /**
     * Get text from a view
     */
    protected String getText(Matcher<View> viewMatcher) {
        final String[] text = {""};
        try {
            onView(viewMatcher)
                .check(matches(isDisplayed()))
                .perform(new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return isEnabled();
                    }

                    @Override
                    public String getDescription() {
                        return "Get text from view";
                    }

                    @Override
                    public void perform(UiController uiController, View view) {
                        if (view instanceof android.widget.TextView) {
                            CharSequence charSeq = ((android.widget.TextView) view).getText();
                            text[0] = charSeq != null ? charSeq.toString() : "";
                        }
                    }
                });
            logger.info("Retrieved text '{}' from view: {}", text[0], viewMatcher);
            return text[0];
        } catch (NoMatchingViewException e) {
            logger.error("View not found: {}", viewMatcher, e);
            throw new ViewNotFoundException(viewMatcher, e);
        } catch (Exception e) {
            logger.error("Get text failed on view: {}", viewMatcher, e);
            throw e;
        }
    }

    /**
     * Verify if view is displayed
     */
    protected boolean isDisplayed(Matcher<View> viewMatcher) {
        try {
            onView(viewMatcher).check(matches(isDisplayed()));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verify text matches expected value
     */
    protected void verifyText(Matcher<View> viewMatcher, String expectedText) {
        try {
            onView(viewMatcher)
                .check(matches(withText(expectedText)));
            logger.info("Verified text '{}' on view: {}", expectedText, viewMatcher);
        } catch (Exception e) {
            logger.error("Text verification failed for view: {}", viewMatcher, e);
            throw e;
        }
    }

    /**
     * Verify view is displayed
     */
    protected void verifyDisplayed(Matcher<View> viewMatcher) {
        try {
            onView(viewMatcher)
                .check(matches(isDisplayed()));
            logger.info("Verified view is displayed: {}", viewMatcher);
        } catch (Exception e) {
            logger.error("View display verification failed: {}", viewMatcher, e);
            throw e;
        }
    }

    /**
     * Verify view does not exist
     */
    protected void verifyNotExists(Matcher<View> viewMatcher) {
        try {
            onView(viewMatcher)
                .check(doesNotExist());
            logger.info("Verified view does not exist: {}", viewMatcher);
        } catch (Exception e) {
            logger.error("View non-existence verification failed: {}", viewMatcher, e);
            throw e;
        }
    }

    /**
     * Wait for view to be displayed
     */
    protected void waitForView(Matcher<View> viewMatcher) {
        waitForView(viewMatcher, DEFAULT_TIMEOUT);
    }

    /**
     * Wait for view to be displayed with custom timeout
     * Uses Espresso's built-in waiting mechanism with UiController
     */
    protected void waitForView(Matcher<View> viewMatcher, int timeoutSeconds) {
        long endTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(timeoutSeconds);

        while (System.currentTimeMillis() < endTime) {
            try {
                onView(viewMatcher).check(matches(isDisplayed()));
                logger.info("View became visible: {}", viewMatcher);
                return;
            } catch (NoMatchingViewException | AssertionError e) {
                // View not found yet, continue waiting
                // Use UiController loopMainThreadUntilIdle instead of Thread.sleep
                onView(androidx.test.espresso.matcher.ViewMatchers.isRoot())
                    .perform(new ViewAction() {
                        @Override
                        public Matcher<View> getConstraints() {
                            return androidx.test.espresso.matcher.ViewMatchers.isRoot();
                        }

                        @Override
                        public String getDescription() {
                            return "Wait for view to appear";
                        }

                        @Override
                        public void perform(UiController uiController, View view) {
                            uiController.loopMainThreadForAtLeast(500);
                        }
                    });
            }
        }

        logger.error("Timeout waiting for view: {}", viewMatcher);
        throw new TestTimeoutException(timeoutSeconds, "View not displayed: " + viewMatcher);
    }

    /**
     * Wait for view to disappear
     */
    protected void waitForViewToDisappear(Matcher<View> viewMatcher) {
        waitForViewToDisappear(viewMatcher, DEFAULT_TIMEOUT);
    }

    /**
     * Wait for view to disappear with custom timeout
     * Uses Espresso's built-in waiting mechanism with UiController
     */
    protected void waitForViewToDisappear(Matcher<View> viewMatcher, int timeoutSeconds) {
        long endTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(timeoutSeconds);

        while (System.currentTimeMillis() < endTime) {
            try {
                onView(viewMatcher).check(doesNotExist());
                logger.info("View disappeared: {}", viewMatcher);
                return;
            } catch (AssertionError e) {
                // View still exists, continue waiting
                onView(androidx.test.espresso.matcher.ViewMatchers.isRoot())
                    .perform(new ViewAction() {
                        @Override
                        public Matcher<View> getConstraints() {
                            return androidx.test.espresso.matcher.ViewMatchers.isRoot();
                        }

                        @Override
                        public String getDescription() {
                            return "Wait for view to disappear";
                        }

                        @Override
                        public void perform(UiController uiController, View view) {
                            uiController.loopMainThreadForAtLeast(500);
                        }
                    });
            }
        }

        logger.error("Timeout waiting for view to disappear: {}", viewMatcher);
        throw new TestTimeoutException(timeoutSeconds, "View still visible: " + viewMatcher);
    }

    /**
     * Scroll to view
     */
    protected void scrollToView(Matcher<View> viewMatcher) {
        try {
            onView(viewMatcher).perform(scrollTo());
            logger.info("Scrolled to view: {}", viewMatcher);
        } catch (Exception e) {
            logger.warn("Could not scroll to view: {}", viewMatcher, e);
        }
    }

    /**
     * Swipe in specified direction
     */
    protected void swipe(SwipeDirection direction) {
        switch (direction) {
            case UP:
                GestureUtils.swipeUp();
                break;
            case DOWN:
                GestureUtils.swipeDown();
                break;
            case LEFT:
                GestureUtils.swipeLeft();
                break;
            case RIGHT:
                GestureUtils.swipeRight();
                break;
        }
        logger.info("Performed swipe: {}", direction);
    }

    /**
     * Swipe on specific view
     */
    protected void swipeOnView(Matcher<View> viewMatcher, SwipeDirection direction) {
        try {
            ViewInteraction viewInteraction = onView(viewMatcher);
            switch (direction) {
                case UP:
                    viewInteraction.perform(swipeUp());
                    break;
                case DOWN:
                    viewInteraction.perform(swipeDown());
                    break;
                case LEFT:
                    viewInteraction.perform(swipeLeft());
                    break;
                case RIGHT:
                    viewInteraction.perform(swipeRight());
                    break;
            }
            logger.info("Performed swipe {} on view: {}", direction, viewMatcher);
        } catch (Exception e) {
            logger.error("Swipe failed on view: {}", viewMatcher, e);
            throw e;
        }
    }

    /**
     * Long press on view
     */
    protected void longPress(Matcher<View> viewMatcher) {
        try {
            onView(viewMatcher)
                .check(matches(isDisplayed()))
                .perform(longClick());
            logger.info("Long pressed on view: {}", viewMatcher);
        } catch (NoMatchingViewException e) {
            logger.error("View not found: {}", viewMatcher, e);
            throw new ViewNotFoundException(viewMatcher, e);
        } catch (Exception e) {
            logger.error("Long press failed on view: {}", viewMatcher, e);
            throw e;
        }
    }

    /**
     * Close soft keyboard
     */
    protected void closeKeyboard() {
        try {
            androidx.test.espresso.Espresso.closeSoftKeyboard();
            logger.info("Closed soft keyboard");
        } catch (Exception e) {
            logger.warn("Could not close keyboard: {}", e.getMessage());
        }
    }

    /**
     * Wait for specified seconds
     * WARNING: Avoid using this in tests. Use IdlingResources or waitForView instead.
     * This method is deprecated and should only be used for debugging.
     */
    @Deprecated
    protected void waitForSeconds(int seconds) {
        logger.warn("Using Thread.sleep() - consider using IdlingResource or waitForView() instead");
        onView(androidx.test.espresso.matcher.ViewMatchers.isRoot())
            .perform(new ViewAction() {
                @Override
                public Matcher<View> getConstraints() {
                    return androidx.test.espresso.matcher.ViewMatchers.isRoot();
                }

                @Override
                public String getDescription() {
                    return "Wait for " + seconds + " seconds";
                }

                @Override
                public void perform(UiController uiController, View view) {
                    uiController.loopMainThreadForAtLeast(seconds * 1000L);
                }
            });
        logger.debug("Waited for {} seconds", seconds);
    }

    /**
     * Wait for specified duration
     * WARNING: Avoid using this in tests. Use IdlingResources or waitForView instead.
     * This method is deprecated and should only be used for debugging.
     */
    @Deprecated
    protected void waitFor(Duration duration) {
        logger.warn("Using explicit wait - consider using IdlingResource or waitForView() instead");
        onView(androidx.test.espresso.matcher.ViewMatchers.isRoot())
            .perform(new ViewAction() {
                @Override
                public Matcher<View> getConstraints() {
                    return androidx.test.espresso.matcher.ViewMatchers.isRoot();
                }

                @Override
                public String getDescription() {
                    return "Wait for " + duration.toMillis() + " ms";
                }

                @Override
                public void perform(UiController uiController, View view) {
                    uiController.loopMainThreadForAtLeast(duration.toMillis());
                }
            });
        logger.debug("Waited for {} ms", duration.toMillis());
    }
}

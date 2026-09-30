package com.espresso.framework.base;

import android.view.View;

import com.espresso.framework.enums.SwipeDirection;

import org.hamcrest.Matcher;

import java.time.Duration;

/**
 * Fluent Page Object - Supports method chaining for better test readability
 * Example: new LoginPage().typeUsername("user").typePassword("pass").clickLogin();
 *
 * This class extends BasePage and adds fluent interfaces that return 'this' for chaining
 */
public abstract class FluentPage extends BasePage {

    /**
     * Fluent click - returns self for chaining
     */
    protected <T extends FluentPage> T clickFluent(Matcher<View> viewMatcher) {
        click(viewMatcher);
        return self();
    }

    /**
     * Fluent type - returns self for chaining
     */
    protected <T extends FluentPage> T typeFluent(Matcher<View> viewMatcher, String text) {
        type(viewMatcher, text);
        return self();
    }

    /**
     * Fluent replace text - returns self for chaining
     */
    protected <T extends FluentPage> T replaceTextFluent(Matcher<View> viewMatcher, String text) {
        replaceText(viewMatcher, text);
        return self();
    }

    /**
     * Fluent wait for view - returns self for chaining
     */
    protected <T extends FluentPage> T waitForViewFluent(Matcher<View> viewMatcher) {
        waitForView(viewMatcher);
        return self();
    }

    /**
     * Fluent wait for view with timeout - returns self for chaining
     */
    protected <T extends FluentPage> T waitForViewFluent(Matcher<View> viewMatcher, int timeoutSeconds) {
        waitForView(viewMatcher, timeoutSeconds);
        return self();
    }

    /**
     * Fluent wait for view to disappear - returns self for chaining
     */
    protected <T extends FluentPage> T waitForViewToDisappearFluent(Matcher<View> viewMatcher) {
        waitForViewToDisappear(viewMatcher);
        return self();
    }

    /**
     * Fluent scroll to view - returns self for chaining
     */
    protected <T extends FluentPage> T scrollToViewFluent(Matcher<View> viewMatcher) {
        scrollToView(viewMatcher);
        return self();
    }

    /**
     * Fluent swipe - returns self for chaining
     */
    protected <T extends FluentPage> T swipeFluent(SwipeDirection direction) {
        swipe(direction);
        return self();
    }

    /**
     * Fluent swipe on view - returns self for chaining
     */
    protected <T extends FluentPage> T swipeOnViewFluent(Matcher<View> viewMatcher, SwipeDirection direction) {
        swipeOnView(viewMatcher, direction);
        return self();
    }

    /**
     * Fluent long press - returns self for chaining
     */
    protected <T extends FluentPage> T longPressFluent(Matcher<View> viewMatcher) {
        longPress(viewMatcher);
        return self();
    }

    /**
     * Fluent close keyboard - returns self for chaining
     */
    protected <T extends FluentPage> T closeKeyboardFluent() {
        closeKeyboard();
        return self();
    }

    /**
     * Fluent wait for seconds - returns self for chaining
     */
    protected <T extends FluentPage> T waitForSecondsFluent(int seconds) {
        waitForSeconds(seconds);
        return self();
    }

    /**
     * Fluent wait for duration - returns self for chaining
     */
    protected <T extends FluentPage> T waitForFluent(Duration duration) {
        waitFor(duration);
        return self();
    }

    /**
     * Verify element is displayed - returns self for chaining
     */
    protected <T extends FluentPage> T verifyDisplayedFluent(Matcher<View> viewMatcher) {
        verifyDisplayed(viewMatcher);
        logger.info("Verified view is displayed (fluent)");
        return self();
    }

    /**
     * Verify text equals expected value - returns self for chaining
     */
    protected <T extends FluentPage> T verifyTextFluent(Matcher<View> viewMatcher, String expectedText) {
        verifyText(viewMatcher, expectedText);
        logger.info("Verified text '{}' (fluent)", expectedText);
        return self();
    }

    /**
     * Verify view does not exist - returns self for chaining
     */
    protected <T extends FluentPage> T verifyNotExistsFluent(Matcher<View> viewMatcher) {
        verifyNotExists(viewMatcher);
        logger.info("Verified view does not exist (fluent)");
        return self();
    }

    /**
     * Returns this instance for method chaining
     */
    @SuppressWarnings("unchecked")
    private <T extends FluentPage> T self() {
        return (T) this;
    }

    /**
     * Navigate to another page/screen
     * Useful for fluent navigation: loginPage.login().navigateTo(HomePage.class)
     */
    protected <T extends FluentPage> T navigateTo(Class<T> pageClass) {
        try {
            T page = pageClass.getDeclaredConstructor().newInstance();
            logger.info("Navigated to page: {}", pageClass.getSimpleName());
            return page;
        } catch (Exception e) {
            logger.error("Failed to navigate to page: {}", pageClass.getName(), e);
            throw new RuntimeException("Navigation failed to: " + pageClass.getName(), e);
        }
    }

    /**
     * Execute a custom action and return self for chaining
     */
    protected <T extends FluentPage> T doAction(Runnable action) {
        action.run();
        return self();
    }

    /**
     * Conditional action - execute only if condition is true
     */
    protected <T extends FluentPage> T doIf(boolean condition, Runnable action) {
        if (condition) {
            action.run();
        }
        return self();
    }
}

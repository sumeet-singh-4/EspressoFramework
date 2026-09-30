package com.espresso.framework.pages;

import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;

import com.espresso.framework.base.FluentPage;

import org.hamcrest.Matcher;

import io.qameta.allure.Step;

/**
 * Login Page Object
 * Example page object demonstrating fluent API and Allure integration
 */
public class LoginPage extends FluentPage {

    // Element locators (update these IDs based on your actual app)
    private final Matcher<View> usernameField = withContentDescription("Username input field");
    private final Matcher<View> passwordField = withContentDescription("Password input field");
    private final Matcher<View> loginButton = withContentDescription("Login button");
    private final Matcher<View> errorMessage = withId(android.R.id.message);
    private final Matcher<View> successMessage = withText("You are logged in!");

    // Alternative locators by text (use these if content description is not available)
    private final Matcher<View> usernameFieldByText = withText("Username");
    private final Matcher<View> passwordFieldByText = withText("Password");
    private final Matcher<View> loginButtonByText = withText("LOGIN");

    /**
     * Check if login page is displayed
     */
    @Step("Verify login page is displayed")
    public boolean isDisplayed() {
        try {
            waitForView(loginButton, 10);
            return true;
        } catch (Exception e) {
            logger.error("Login page is not displayed", e);
            return false;
        }
    }

    /**
     * Type username
     */
    @Step("Enter username: {username}")
    public LoginPage typeUsername(String username) {
        logger.info("Entering username: {}", username);
        replaceTextFluent(usernameField, username);
        return this;
    }

    /**
     * Type password
     */
    @Step("Enter password")
    public LoginPage typePassword(String password) {
        logger.info("Entering password");
        replaceTextFluent(passwordField, password);
        return this;
    }

    /**
     * Click login button
     */
    @Step("Click login button")
    public LoginPage clickLogin() {
        logger.info("Clicking login button");
        clickFluent(loginButton);
        return this;
    }

    /**
     * Close keyboard after entering credentials
     */
    @Step("Close keyboard")
    public LoginPage closeKeyboard() {
        closeKeyboardFluent();
        return this;
    }

    /**
     * Complete login flow
     */
    @Step("Login with username: {username}")
    public LoginPage login(String username, String password) {
        logger.info("Performing login with username: {}", username);
        return typeUsername(username)
            .typePassword(password)
            .closeKeyboard()
            .clickLogin();
    }

    /**
     * Verify error message is displayed
     */
    @Step("Verify error message is displayed")
    public LoginPage verifyErrorDisplayed() {
        logger.info("Verifying error message is displayed");
        waitForView(errorMessage, 5);
        verifyDisplayedFluent(errorMessage);
        return this;
    }

    /**
     * Verify success message is displayed
     */
    @Step("Verify success message is displayed")
    public LoginPage verifySuccessDisplayed() {
        logger.info("Verifying success message is displayed");
        waitForView(successMessage, 10);
        verifyDisplayedFluent(successMessage);
        return this;
    }

    /**
     * Get error message text
     */
    @Step("Get error message text")
    public String getErrorMessage() {
        try {
            waitForView(errorMessage, 5);
            String message = getText(errorMessage);
            logger.info("Error message: {}", message);
            return message;
        } catch (Exception e) {
            logger.error("Could not get error message", e);
            return "";
        }
    }

    /**
     * Verify login page elements are visible
     */
    @Step("Verify all login page elements are visible")
    public LoginPage verifyAllElementsVisible() {
        logger.info("Verifying all login page elements are visible");
        verifyDisplayedFluent(usernameField);
        verifyDisplayedFluent(passwordField);
        verifyDisplayedFluent(loginButton);
        return this;
    }

    /**
     * Clear username field
     */
    @Step("Clear username field")
    public LoginPage clearUsername() {
        logger.info("Clearing username field");
        replaceTextFluent(usernameField, "");
        return this;
    }

    /**
     * Clear password field
     */
    @Step("Clear password field")
    public LoginPage clearPassword() {
        logger.info("Clearing password field");
        replaceTextFluent(passwordField, "");
        return this;
    }

    /**
     * Wait for login page to load
     */
    @Step("Wait for login page to load")
    public LoginPage waitForPageLoad() {
        logger.info("Waiting for login page to load");
        waitForView(loginButton, 15);
        return this;
    }

    /**
     * Verify login button is enabled
     */
    @Step("Verify login button is enabled")
    public LoginPage verifyLoginButtonEnabled() {
        logger.info("Verifying login button is enabled");
        verifyDisplayedFluent(loginButton);
        return this;
    }
}

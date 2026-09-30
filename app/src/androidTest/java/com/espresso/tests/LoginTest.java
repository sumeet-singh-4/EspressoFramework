package com.espresso.tests;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.espresso.framework.MainActivity;
import com.espresso.framework.base.BaseTest;
import com.espresso.framework.components.LoadingSpinnerComponent;
import com.espresso.framework.pages.LoginPage;
import com.espresso.framework.utils.JsonReader;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

/**
 * Login Test Examples
 * Demonstrates Espresso testing with Allure annotations and data-driven approach
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
@Epic("Authentication")
@Feature("Login")
public class LoginTest extends BaseTest {

    private LoginPage loginPage;
    private Map<String, Object> testData;

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
        new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setupTest() {
        loginPage = new LoginPage();

        // Load test data from JSON
        testData = JsonReader.readJson("testdata/testdata.json");

        // Validate test data loaded successfully
        if (testData == null || testData.isEmpty()) {
            throw new IllegalStateException("Failed to load test data from testdata.json");
        }
    }

    @Test
    @Story("Valid Login")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test successful login with valid credentials")
    public void testValidLogin() {
        logStep("Starting valid login test");

        // Get valid user credentials from test data
        Map<String, String> validUser = (Map<String, String>) testData.get("validUser");
        String username = validUser.get("username");
        String password = validUser.get("password");

        loginPage
            .waitForPageLoad()
            .verifyAllElementsVisible()
            .login(username, password);

        // Wait for loading to complete
        LoadingSpinnerComponent.waitForDefaultLoadingComplete();

        // Verify successful login
        loginPage.verifySuccessDisplayed();

        logStep("Valid login test completed successfully");
    }

    @Test
    @Story("Invalid Login")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test login failure with invalid credentials")
    public void testInvalidLogin() {
        logStep("Starting invalid login test");

        // Get invalid user credentials from test data
        Map<String, String> invalidUser = (Map<String, String>) testData.get("invalidUser");
        String username = invalidUser.get("username");
        String password = invalidUser.get("password");

        loginPage
            .waitForPageLoad()
            .login(username, password);

        // Verify error message is displayed
        loginPage.verifyErrorDisplayed();

        String errorMessage = loginPage.getErrorMessage();
        logInfo("Error message displayed: " + errorMessage);

        logStep("Invalid login test completed successfully");
    }

    @Test
    @Story("Empty Credentials")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test login with empty credentials")
    public void testEmptyCredentials() {
        logStep("Starting empty credentials test");

        // Get empty credentials from test data
        Map<String, String> emptyCredentials = (Map<String, String>) testData.get("emptyCredentials");
        String username = emptyCredentials.get("username");
        String password = emptyCredentials.get("password");

        loginPage
            .waitForPageLoad()
            .typeUsername(username)
            .typePassword(password)
            .closeKeyboard()
            .clickLogin();

        // Verify error message or that login button is disabled
        loginPage.verifyErrorDisplayed();

        logStep("Empty credentials test completed successfully");
    }

    @Test
    @Story("Login Page Elements")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify all login page elements are visible")
    public void testLoginPageElementsVisible() {
        logStep("Starting login page elements visibility test");

        loginPage
            .waitForPageLoad()
            .verifyAllElementsVisible()
            .verifyLoginButtonEnabled();

        logStep("Login page elements visibility test completed successfully");
    }

    @Test
    @Story("Clear Fields")
    @Severity(SeverityLevel.TRIVIAL)
    @Description("Test clearing input fields")
    public void testClearFields() {
        logStep("Starting clear fields test");

        Map<String, String> validUser = (Map<String, String>) testData.get("validUser");
        String username = validUser.get("username");
        String password = validUser.get("password");

        loginPage
            .waitForPageLoad()
            .typeUsername(username)
            .typePassword(password)
            .clearUsername()
            .clearPassword()
            .verifyAllElementsVisible();

        logStep("Clear fields test completed successfully");
    }

    @Test
    @Story("Login Flow")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test complete login flow with fluent API")
    public void testLoginFlowWithFluentAPI() {
        logStep("Starting login flow test with fluent API");

        Map<String, String> validUser = (Map<String, String>) testData.get("validUser");

        // Demonstrate fluent API method chaining
        loginPage
            .waitForPageLoad()
            .verifyAllElementsVisible()
            .typeUsername(validUser.get("username"))
            .typePassword(validUser.get("password"))
            .closeKeyboard()
            .clickLogin();

        // Wait for loading to complete
        LoadingSpinnerComponent.waitForDefaultLoadingComplete();
        logInfo("Loading completed");

        // Verify success
        loginPage.verifySuccessDisplayed();

        logStep("Login flow test with fluent API completed successfully");
    }
}

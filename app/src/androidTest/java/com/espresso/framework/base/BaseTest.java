package com.espresso.framework.base;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.espresso.framework.config.ConfigReader;
import com.espresso.framework.listeners.AllureListener;
import com.espresso.framework.listeners.TestLifecycleListener;
import com.espresso.framework.rules.RetryRule;
import com.espresso.framework.utils.IdlingResourceManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.RuleChain;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base Test Class for Espresso Framework
 * All test classes should extend this class
 * Provides setup/teardown, screenshot capture, and test lifecycle management
 */
public abstract class BaseTest {
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    /**
     * JUnit Rule to capture test name
     */
    @Rule
    public TestName testName = new TestName();

    /**
     * Combined rule chain: Retry -> Lifecycle -> Allure
     * Order matters: retry wraps everything, allure is innermost
     */
    @Rule
    public TestRule ruleChain = RuleChain
        .outerRule(new RetryRule(ConfigReader.getRetryCount()))
        .around(new TestLifecycleListener())
        .around(new AllureListener());

    /**
     * Setup method - runs before each test
     */
    @Before
    public void setUp() {
        logger.info("========================================");
        logger.info("Starting test: {}", testName.getMethodName());
        logger.info("Environment: {}", ConfigReader.getCurrentEnvironment());
        logger.info("========================================");

        // Disable animations if configured
        if (ConfigReader.disableAnimations()) {
            disableAnimations();
        }

        // Custom setup for subclasses
        onSetUp();
    }

    /**
     * Teardown method - runs after each test
     */
    @After
    public void tearDown() {
        logger.info("========================================");
        logger.info("Completed test: {}", testName.getMethodName());
        logger.info("========================================");

        // Clean up IdlingResources
        try {
            IdlingResourceManager.unregisterAll();
            logger.debug("Cleaned up IdlingResources");
        } catch (Exception e) {
            logger.warn("Failed to clean up IdlingResources: {}", e.getMessage());
        }

        // Custom teardown for subclasses
        onTearDown();
    }

    /**
     * Override this method in subclasses for custom setup logic
     */
    protected void onSetUp() {
        // Override in subclasses if needed
    }

    /**
     * Override this method in subclasses for custom teardown logic
     */
    protected void onTearDown() {
        // Override in subclasses if needed
    }

    /**
     * Disable animations for faster and more reliable testing
     */
    private void disableAnimations() {
        try {
            // Disable animations via shell commands
            executeShellCommand("settings put global window_animation_scale 0");
            executeShellCommand("settings put global transition_animation_scale 0");
            executeShellCommand("settings put global animator_duration_scale 0");
            logger.info("Animations disabled");
        } catch (Exception e) {
            logger.warn("Failed to disable animations: {}", e.getMessage());
        }
    }

    /**
     * Enable animations (restore default)
     */
    protected void enableAnimations() {
        try {
            executeShellCommand("settings put global window_animation_scale 1");
            executeShellCommand("settings put global transition_animation_scale 1");
            executeShellCommand("settings put global animator_duration_scale 1");
            logger.info("Animations enabled");
        } catch (Exception e) {
            logger.warn("Failed to enable animations: {}", e.getMessage());
        }
    }

    /**
     * Execute shell command
     */
    private void executeShellCommand(String command) {
        try {
            androidx.test.platform.app.InstrumentationRegistry
                .getInstrumentation()
                .getUiAutomation()
                .executeShellCommand(command);
        } catch (Exception e) {
            logger.error("Failed to execute shell command: {}", command, e);
        }
    }

    /**
     * Get current test name
     */
    protected String getCurrentTestName() {
        return testName.getMethodName();
    }

    /**
     * Wait for specified seconds (helper method)
     * WARNING: Avoid using this in production tests. Use IdlingResources instead.
     * This should only be used for debugging or as a last resort.
     */
    @Deprecated
    protected void waitForSeconds(int seconds) {
        logger.warn("Using Thread.sleep() - consider using IdlingResource instead");
        try {
            Thread.sleep(seconds * 1000L);
            logger.debug("Waited for {} seconds", seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Wait interrupted", e);
        }
    }

    /**
     * Log test step
     */
    protected void logStep(String stepDescription) {
        logger.info("TEST STEP: {}", stepDescription);
    }

    /**
     * Log test info
     */
    protected void logInfo(String message) {
        logger.info(message);
    }

    /**
     * Log test warning
     */
    protected void logWarning(String message) {
        logger.warn(message);
    }

    /**
     * Log test error
     */
    protected void logError(String message, Throwable throwable) {
        logger.error(message, throwable);
    }
}

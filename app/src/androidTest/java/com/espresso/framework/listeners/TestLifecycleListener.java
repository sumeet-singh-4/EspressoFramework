package com.espresso.framework.listeners;

import com.espresso.framework.config.ConfigReader;
import com.espresso.framework.utils.ScreenshotUtils;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test Lifecycle Listener (JUnit Rule)
 * Handles test lifecycle events: screenshot on failure, logging, etc.
 */
public class TestLifecycleListener extends TestWatcher {
    private static final Logger logger = LoggerFactory.getLogger(TestLifecycleListener.class);

    @Override
    protected void starting(Description description) {
        logger.info("========================================");
        logger.info("STARTING TEST: {}", description.getMethodName());
        logger.info("Test Class: {}", description.getClassName());
        logger.info("========================================");
    }

    @Override
    protected void succeeded(Description description) {
        logger.info("========================================");
        logger.info("TEST PASSED: {}", description.getMethodName());
        logger.info("========================================");

        // Capture screenshot on pass if configured
        if (ConfigReader.captureScreenshotOnPass()) {
            ScreenshotUtils.captureScreenshotOnPass(description.getMethodName());
        }
    }

    @Override
    protected void failed(Throwable e, Description description) {
        logger.error("========================================");
        logger.error("TEST FAILED: {}", description.getMethodName());
        logger.error("Failure Reason: {}", e.getMessage());
        logger.error("========================================", e);

        // Capture screenshot on failure
        if (ConfigReader.captureScreenshotOnFailure()) {
            ScreenshotUtils.captureScreenshotOnFailure(description.getMethodName());
        }
    }

    @Override
    protected void skipped(org.junit.AssumptionViolatedException e, Description description) {
        logger.warn("========================================");
        logger.warn("TEST SKIPPED: {}", description.getMethodName());
        logger.warn("Skip Reason: {}", e.getMessage());
        logger.warn("========================================");
    }

    @Override
    protected void finished(Description description) {
        logger.info("========================================");
        logger.info("FINISHED TEST: {}", description.getMethodName());
        logger.info("========================================");
    }
}

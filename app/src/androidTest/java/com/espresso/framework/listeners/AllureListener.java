package com.espresso.framework.listeners;

import com.espresso.framework.config.ConfigReader;
import com.espresso.framework.utils.ScreenshotUtils;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Allure;

/**
 * Allure Listener for Espresso Framework
 * Integrates with Allure reporting for enhanced test reports
 */
public class AllureListener extends TestWatcher {
    private static final Logger logger = LoggerFactory.getLogger(AllureListener.class);

    @Override
    protected void starting(Description description) {
        logger.info("Allure: Starting test - {}", description.getMethodName());

        // Add test information to Allure
        Allure.parameter("Test Method", description.getMethodName());
        Allure.parameter("Test Class", description.getClassName());
        Allure.parameter("Environment", ConfigReader.getCurrentEnvironment());
    }

    @Override
    protected void succeeded(Description description) {
        logger.info("Allure: Test passed - {}", description.getMethodName());

        // Attach screenshot on pass if configured
        if (ConfigReader.captureScreenshotOnPass()) {
            byte[] screenshot = ScreenshotUtils.captureScreenshot(description.getMethodName() + "_PASSED");
            if (screenshot != null && screenshot.length > 0) {
                Allure.addAttachment("Test Passed Screenshot", "image/png",
                    new java.io.ByteArrayInputStream(screenshot), ".png");
            }
        }
    }

    @Override
    protected void failed(Throwable e, Description description) {
        logger.error("Allure: Test failed - {}", description.getMethodName(), e);

        // Attach failure information
        Allure.addAttachment("Failure Message", e.getMessage());
        Allure.addAttachment("Stack Trace", getStackTrace(e));

        // Attach screenshot on failure
        if (ConfigReader.captureScreenshotOnFailure()) {
            byte[] screenshot = ScreenshotUtils.captureScreenshot(description.getMethodName() + "_FAILED");
            if (screenshot != null && screenshot.length > 0) {
                Allure.addAttachment("Failure Screenshot", "image/png",
                    new java.io.ByteArrayInputStream(screenshot), ".png");
            }
        }
    }

    @Override
    protected void skipped(org.junit.AssumptionViolatedException e, Description description) {
        logger.warn("Allure: Test skipped - {}", description.getMethodName());
        Allure.addAttachment("Skip Reason", e.getMessage());
    }

    @Override
    protected void finished(Description description) {
        logger.info("Allure: Finished test - {}", description.getMethodName());
    }

    /**
     * Get stack trace as string
     */
    private String getStackTrace(Throwable throwable) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}

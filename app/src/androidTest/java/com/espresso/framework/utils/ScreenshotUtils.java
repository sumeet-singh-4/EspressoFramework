package com.espresso.framework.utils;

import android.graphics.Bitmap;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.screenshot.Screenshot;

import com.espresso.framework.config.ConfigReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;

/**
 * Screenshot Utility for Espresso Framework
 * Captures screenshots and integrates with Allure reporting
 */
public class ScreenshotUtils {
    private static final Logger logger = LoggerFactory.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_DIR = "screenshots/";

    static {
        File directory = new File(getScreenshotDirectory());
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Capture screenshot and attach to Allure report
     */
    @Attachment(value = "Screenshot: {testName}", type = "image/png")
    public static byte[] captureScreenshot(String testName) {
        try {
            Bitmap bitmap = Screenshot.capture().getBitmap();
            byte[] screenshotBytes = convertBitmapToByteArray(bitmap);

            // Save to file
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            String fileName = testName + "_" + timestamp + ".png";
            File screenshotFile = new File(getScreenshotDirectory() + fileName);

            try (FileOutputStream fos = new FileOutputStream(screenshotFile)) {
                fos.write(screenshotBytes);
                logger.info("Screenshot saved: {}", screenshotFile.getAbsolutePath());
            }

            // Attach to Allure
            attachToAllure(testName, screenshotBytes);

            return screenshotBytes;
        } catch (Exception e) {
            logger.error("Failed to capture screenshot for test: {}", testName, e);
            return new byte[0];
        }
    }

    /**
     * Capture screenshot on test failure
     */
    public static byte[] captureScreenshotOnFailure(String testName) {
        if (ConfigReader.captureScreenshotOnFailure()) {
            logger.info("Capturing screenshot on failure for test: {}", testName);
            return captureScreenshot(testName + "_FAILED");
        }
        return new byte[0];
    }

    /**
     * Capture screenshot on test pass
     */
    public static byte[] captureScreenshotOnPass(String testName) {
        if (ConfigReader.captureScreenshotOnPass()) {
            logger.info("Capturing screenshot on pass for test: {}", testName);
            return captureScreenshot(testName + "_PASSED");
        }
        return new byte[0];
    }

    /**
     * Capture screenshot with custom name
     */
    public static byte[] captureScreenshotWithName(String screenshotName) {
        return captureScreenshot(screenshotName);
    }

    /**
     * Convert Bitmap to byte array
     */
    private static byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * Attach screenshot to Allure report
     */
    private static void attachToAllure(String name, byte[] screenshot) {
        try {
            Allure.addAttachment(name, "image/png", new java.io.ByteArrayInputStream(screenshot), ".png");
            logger.debug("Screenshot attached to Allure: {}", name);
        } catch (Exception e) {
            logger.warn("Failed to attach screenshot to Allure: {}", e.getMessage());
        }
    }

    /**
     * Get screenshot directory path
     */
    private static String getScreenshotDirectory() {
        String dir = ConfigReader.getProperty("screenshot.directory", SCREENSHOT_DIR);
        File directory = new File(InstrumentationRegistry.getInstrumentation()
            .getTargetContext().getFilesDir(), dir);
        return directory.getAbsolutePath() + "/";
    }

    /**
     * Capture screenshot as Base64 (for web integrations)
     */
    public static String captureScreenshotAsBase64() {
        try {
            Bitmap bitmap = Screenshot.capture().getBitmap();
            byte[] screenshotBytes = convertBitmapToByteArray(bitmap);
            return android.util.Base64.encodeToString(screenshotBytes, android.util.Base64.DEFAULT);
        } catch (Exception e) {
            logger.error("Failed to capture screenshot as Base64", e);
            return "";
        }
    }

    /**
     * Clear all screenshots from directory
     */
    public static void clearScreenshots() {
        File directory = new File(getScreenshotDirectory());
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.delete()) {
                        logger.debug("Deleted screenshot: {}", file.getName());
                    }
                }
            }
            logger.info("Cleared all screenshots from directory");
        }
    }
}

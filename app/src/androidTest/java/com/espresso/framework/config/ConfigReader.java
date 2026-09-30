package com.espresso.framework.config;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration Reader for Espresso Framework
 * Supports multiple environments: dev, qa, sbx, prod
 * Auto-detects environment from System Properties, Environment Variables, or Gradle properties
 */
public class ConfigReader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);
    private static Properties properties;
    private static final String CONFIG_DIR = "config/";
    private static final String DEFAULT_CONFIG = "config.properties";
    private static String currentEnvironment;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        properties = new Properties();

        // Determine environment from multiple sources (priority order)
        currentEnvironment = determineEnvironment();

        // Build config file name
        String configFileName = currentEnvironment.isEmpty()
            ? DEFAULT_CONFIG
            : "config-" + currentEnvironment + ".properties";

        String configFilePath = CONFIG_DIR + configFileName;

        try {
            Context context = InstrumentationRegistry.getInstrumentation().getContext();
            InputStream input = context.getAssets().open(configFilePath);
            properties.load(input);
            logger.info("====================================");
            logger.info("Environment: {}", currentEnvironment.isEmpty() ? "default" : currentEnvironment.toUpperCase());
            logger.info("Configuration loaded from: {}", configFilePath);
            logger.info("====================================");
            input.close();
        } catch (IOException e) {
            logger.warn("Failed to load environment-specific config: {}", configFilePath);
            logger.info("Falling back to default config: {}", DEFAULT_CONFIG);

            // Fallback to default config
            try {
                Context context = InstrumentationRegistry.getInstrumentation().getContext();
                InputStream input = context.getAssets().open(CONFIG_DIR + DEFAULT_CONFIG);
                properties.load(input);
                logger.info("Default configuration loaded successfully");
                input.close();
            } catch (IOException ex) {
                logger.error("Failed to load default configuration file", ex);
                throw new RuntimeException("Configuration file not found: " + CONFIG_DIR + DEFAULT_CONFIG);
            }
        }
    }

    /**
     * Determines the environment from multiple sources in priority order:
     * 1. System property: -Denv=qa
     * 2. Environment variable: ENV=qa
     * 3. Gradle property: env from gradle.properties
     * 4. Default: empty string (uses config.properties)
     */
    private static String determineEnvironment() {
        // Priority 1: System property
        String env = System.getProperty("env");
        if (env != null && !env.isEmpty()) {
            logger.info("Environment determined from System Property 'env': {}", env);
            return env.toLowerCase();
        }

        // Priority 2: Environment variable
        env = System.getenv("ENV");
        if (env != null && !env.isEmpty()) {
            logger.info("Environment determined from Environment Variable 'ENV': {}", env);
            return env.toLowerCase();
        }

        // Priority 3: Check for Gradle injected property
        env = System.getProperty("gradle.env");
        if (env != null && !env.isEmpty()) {
            logger.info("Environment determined from Gradle property 'gradle.env': {}", env);
            return env.toLowerCase();
        }

        logger.info("No environment specified, using default configuration");
        return "";
    }

    /**
     * Get the current environment name
     */
    public static String getCurrentEnvironment() {
        return currentEnvironment.isEmpty() ? "default" : currentEnvironment;
    }

    /**
     * Reload configuration (useful for switching environments during runtime)
     */
    public static void reloadConfiguration() {
        logger.info("Reloading configuration...");
        loadProperties();
    }

    public static String getProperty(String key) {
        String value = System.getProperty(key);
        if (value == null) {
            value = properties.getProperty(key);
        }
        if (value == null) {
            logger.warn("Property '{}' not found in configuration", key);
        }
        return value;
    }

    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    public static int getPropertyAsInt(String key) {
        return Integer.parseInt(getProperty(key));
    }

    public static int getPropertyAsInt(String key, int defaultValue) {
        String value = getProperty(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    public static boolean getPropertyAsBoolean(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }

    public static boolean getPropertyAsBoolean(String key, boolean defaultValue) {
        String value = getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public static long getPropertyAsLong(String key) {
        return Long.parseLong(getProperty(key));
    }

    public static long getPropertyAsLong(String key, long defaultValue) {
        String value = getProperty(key);
        return value != null ? Long.parseLong(value) : defaultValue;
    }

    // Framework-specific getters
    public static int getDefaultTimeout() {
        return getPropertyAsInt("test.timeout.default", 10);
    }

    public static int getExplicitWait() {
        return getPropertyAsInt("test.timeout.explicit", 20);
    }

    public static int getImplicitWait() {
        return getPropertyAsInt("test.timeout.implicit", 5);
    }

    public static boolean captureScreenshotOnFailure() {
        return getPropertyAsBoolean("screenshot.onFailure", true);
    }

    public static boolean captureScreenshotOnPass() {
        return getPropertyAsBoolean("screenshot.onPass", false);
    }

    public static boolean recordVideo() {
        return getPropertyAsBoolean("video.record", false);
    }

    public static int getRetryCount() {
        return getPropertyAsInt("test.retry.count", 2);
    }

    public static long getRetryDelay() {
        return getPropertyAsLong("test.retry.delay", 1000);
    }

    // App-specific getters
    public static String getAppPackage() {
        return getProperty("app.package");
    }

    public static String getAppActivity() {
        return getProperty("app.activity");
    }

    // Environment-specific getters
    public static String getAppBaseUrl() {
        return getProperty("app.baseUrl");
    }

    public static String getApiBaseUrl() {
        return getProperty("api.baseUrl");
    }

    public static String getDeeplinkScheme() {
        return getProperty("deeplink.scheme");
    }

    public static String getTestUsername() {
        return getProperty("test.username");
    }

    public static String getTestPassword() {
        return getProperty("test.password");
    }

    // Animation settings
    public static boolean disableAnimations() {
        return getPropertyAsBoolean("animations.disable", true);
    }

    // Idling resource settings
    public static long getIdlingResourceTimeout() {
        return getPropertyAsLong("idling.timeout", 30000);
    }

    // Logging
    public static String getLogLevel() {
        return getProperty("log.level", "INFO");
    }
}

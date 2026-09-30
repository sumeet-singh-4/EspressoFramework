package com.espresso.framework.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Smart Retry Utility - Provides intelligent retry logic for flaky operations
 * Replicates functionality from Appium framework
 */
public class RetryUtils {
    private static final Logger logger = LoggerFactory.getLogger(RetryUtils.class);

    private RetryUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Retry an operation with default settings (3 attempts, 2 second delay)
     */
    public static <T> T retry(Supplier<T> operation) {
        return retry(operation, 3, 2000);
    }

    /**
     * Retry an operation with custom attempts and delay
     *
     * @param operation  The operation to retry
     * @param maxRetries Maximum number of retry attempts
     * @param delayMs    Delay between retries in milliseconds
     */
    public static <T> T retry(Supplier<T> operation, int maxRetries, long delayMs) {
        int attempt = 1;
        Exception lastException = null;

        while (attempt <= maxRetries) {
            try {
                logger.debug("Attempt {}/{}", attempt, maxRetries);
                return operation.get();
            } catch (Exception e) {
                lastException = e;
                logger.warn("Attempt {}/{} failed: {}", attempt, maxRetries, e.getMessage());

                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                }
                attempt++;
            }
        }

        logger.error("All {} attempts failed", maxRetries);
        throw new RuntimeException("Operation failed after " + maxRetries + " attempts", lastException);
    }

    /**
     * Retry an operation that doesn't return a value
     */
    public static void retryVoid(Runnable operation) {
        retryVoid(operation, 3, 2000);
    }

    /**
     * Retry a void operation with custom attempts and delay
     */
    public static void retryVoid(Runnable operation, int maxRetries, long delayMs) {
        retry(() -> {
            operation.run();
            return null;
        }, maxRetries, delayMs);
    }

    /**
     * Retry with exponential backoff
     * Delay increases exponentially: 1s, 2s, 4s, 8s, etc.
     */
    public static <T> T retryWithExponentialBackoff(Supplier<T> operation, int maxRetries) {
        int attempt = 1;
        long delayMs = 1000;
        Exception lastException = null;

        while (attempt <= maxRetries) {
            try {
                logger.debug("Attempt {}/{} (delay: {}ms)", attempt, maxRetries, delayMs);
                return operation.get();
            } catch (Exception e) {
                lastException = e;
                logger.warn("Attempt {}/{} failed: {}", attempt, maxRetries, e.getMessage());

                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(delayMs);
                        delayMs *= 2; // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                }
                attempt++;
            }
        }

        logger.error("All {} attempts failed with exponential backoff", maxRetries);
        throw new RuntimeException("Operation failed after " + maxRetries + " attempts", lastException);
    }

    /**
     * Retry only for specific exception types
     */
    @SafeVarargs
    public static <T> T retryOnException(Supplier<T> operation, int maxRetries, long delayMs,
                                         Class<? extends Exception>... retryableExceptions) {
        int attempt = 1;
        Exception lastException = null;

        while (attempt <= maxRetries) {
            try {
                logger.debug("Attempt {}/{}", attempt, maxRetries);
                return operation.get();
            } catch (Exception e) {
                lastException = e;

                // Check if exception is retryable
                boolean shouldRetry = false;
                for (Class<? extends Exception> retryableException : retryableExceptions) {
                    if (retryableException.isInstance(e)) {
                        shouldRetry = true;
                        break;
                    }
                }

                if (!shouldRetry) {
                    logger.error("Non-retryable exception encountered: {}", e.getClass().getName());
                    throw new RuntimeException("Non-retryable exception", e);
                }

                logger.warn("Attempt {}/{} failed with retryable exception: {}",
                        attempt, maxRetries, e.getMessage());

                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                }
                attempt++;
            }
        }

        logger.error("All {} attempts failed", maxRetries);
        throw new RuntimeException("Operation failed after " + maxRetries + " attempts", lastException);
    }

    /**
     * Retry until a condition is met or max attempts reached
     */
    public static <T> T retryUntil(Supplier<T> operation, Predicate<T> condition,
                                    int maxRetries, long delayMs) {
        int attempt = 1;

        while (attempt <= maxRetries) {
            try {
                logger.debug("Attempt {}/{}", attempt, maxRetries);
                T result = operation.get();

                if (condition.test(result)) {
                    logger.info("Condition met on attempt {}", attempt);
                    return result;
                }

                logger.warn("Condition not met on attempt {}/{}", attempt, maxRetries);

                if (attempt < maxRetries) {
                    Thread.sleep(delayMs);
                }
                attempt++;
            } catch (Exception e) {
                logger.warn("Attempt {}/{} failed: {}", attempt, maxRetries, e.getMessage());
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                }
                attempt++;
            }
        }

        throw new RuntimeException("Condition not met after " + maxRetries + " attempts");
    }

    /**
     * Retry with custom delay function
     */
    public static <T> T retryWithCustomDelay(Supplier<T> operation, int maxRetries,
                                              java.util.function.IntFunction<Long> delayFunction) {
        int attempt = 1;
        Exception lastException = null;

        while (attempt <= maxRetries) {
            try {
                logger.debug("Attempt {}/{}", attempt, maxRetries);
                return operation.get();
            } catch (Exception e) {
                lastException = e;
                logger.warn("Attempt {}/{} failed: {}", attempt, maxRetries, e.getMessage());

                if (attempt < maxRetries) {
                    long delayMs = delayFunction.apply(attempt);
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                }
                attempt++;
            }
        }

        logger.error("All {} attempts failed", maxRetries);
        throw new RuntimeException("Operation failed after " + maxRetries + " attempts", lastException);
    }
}

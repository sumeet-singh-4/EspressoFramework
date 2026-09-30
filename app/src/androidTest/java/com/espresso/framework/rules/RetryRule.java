package com.espresso.framework.rules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit Rule for retrying failed tests
 * Integrates retry logic at the test level
 */
public class RetryRule implements TestRule {
    private static final Logger logger = LoggerFactory.getLogger(RetryRule.class);

    private final int retryCount;

    public RetryRule(int retryCount) {
        this.retryCount = retryCount;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Throwable caughtThrowable = null;

                for (int i = 0; i <= retryCount; i++) {
                    try {
                        if (i > 0) {
                            logger.info("Retrying test '{}' - Attempt {}/{}",
                                description.getDisplayName(), i + 1, retryCount + 1);
                        }
                        base.evaluate();
                        return; // Test passed
                    } catch (Throwable t) {
                        caughtThrowable = t;
                        logger.warn("Test '{}' failed on attempt {}/{}: {}",
                            description.getDisplayName(), i + 1, retryCount + 1, t.getMessage());

                        if (i < retryCount) {
                            // Wait before retry
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                }

                logger.error("Test '{}' failed after {} attempts",
                    description.getDisplayName(), retryCount + 1);
                throw caughtThrowable;
            }
        };
    }
}

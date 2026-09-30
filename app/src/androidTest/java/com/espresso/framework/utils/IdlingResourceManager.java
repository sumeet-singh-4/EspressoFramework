package com.espresso.framework.utils;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;

import com.espresso.framework.exceptions.EspressoFrameworkException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * IdlingResource Manager for Espresso Framework
 * Manages custom IdlingResources for async operations
 * Thread-safe implementation using ConcurrentHashMap
 */
public class IdlingResourceManager {
    private static final Logger logger = LoggerFactory.getLogger(IdlingResourceManager.class);
    // Changed to ConcurrentHashMap for thread safety
    private static final Map<String, IdlingResource> idlingResources = new ConcurrentHashMap<>();

    /**
     * Register an IdlingResource
     * Thread-safe implementation
     */
    public static void register(String name, IdlingResource idlingResource) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Resource name cannot be null or empty");
        }
        if (idlingResource == null) {
            throw new IllegalArgumentException("IdlingResource cannot be null");
        }

        IdlingResource existing = idlingResources.putIfAbsent(name, idlingResource);
        if (existing == null) {
            // Successfully registered new resource
            IdlingRegistry.getInstance().register(idlingResource);
            logger.info("Registered IdlingResource: {}", name);
        } else {
            logger.warn("IdlingResource '{}' is already registered", name);
        }
    }

    /**
     * Unregister an IdlingResource
     */
    public static void unregister(String name) {
        IdlingResource resource = idlingResources.get(name);
        if (resource != null) {
            IdlingRegistry.getInstance().unregister(resource);
            idlingResources.remove(name);
            logger.info("Unregistered IdlingResource: {}", name);
        } else {
            logger.warn("IdlingResource '{}' not found", name);
        }
    }

    /**
     * Unregister all IdlingResources
     * Thread-safe implementation
     */
    public static void unregisterAll() {
        if (idlingResources.isEmpty()) {
            logger.debug("No IdlingResources to unregister");
            return;
        }

        // Create a copy of keys to avoid ConcurrentModificationException
        for (String name : idlingResources.keySet()) {
            unregister(name);
        }
        logger.info("Unregistered all IdlingResources");
    }

    /**
     * Check if IdlingResource is registered
     */
    public static boolean isRegistered(String name) {
        return idlingResources.containsKey(name);
    }

    /**
     * Simple CountingIdlingResource implementation
     */
    public static class SimpleCountingIdlingResource implements IdlingResource {
        private final String name;
        private volatile int counter = 0;
        private volatile ResourceCallback resourceCallback;

        public SimpleCountingIdlingResource(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean isIdleNow() {
            boolean idle = counter == 0;
            if (idle && resourceCallback != null) {
                resourceCallback.onTransitionToIdle();
            }
            return idle;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback callback) {
            this.resourceCallback = callback;
        }

        public void increment() {
            counter++;
            logger.debug("{}: Counter incremented to {}", name, counter);
        }

        public void decrement() {
            if (counter > 0) {
                counter--;
                logger.debug("{}: Counter decremented to {}", name, counter);
                if (counter == 0 && resourceCallback != null) {
                    resourceCallback.onTransitionToIdle();
                }
            }
        }

        public void reset() {
            counter = 0;
            logger.debug("{}: Counter reset", name);
        }

        public int getCount() {
            return counter;
        }
    }

    /**
     * Time-based IdlingResource
     * Waits for a specific duration before becoming idle
     */
    public static class TimeBasedIdlingResource implements IdlingResource {
        private final String name;
        private final long durationMs;
        private final long startTime;
        private volatile ResourceCallback resourceCallback;

        public TimeBasedIdlingResource(String name, long duration, TimeUnit timeUnit) {
            this.name = name;
            this.durationMs = timeUnit.toMillis(duration);
            this.startTime = System.currentTimeMillis();
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean isIdleNow() {
            long elapsed = System.currentTimeMillis() - startTime;
            boolean idle = elapsed >= durationMs;

            if (idle && resourceCallback != null) {
                resourceCallback.onTransitionToIdle();
            }

            return idle;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback callback) {
            this.resourceCallback = callback;
            // Check if already idle
            if (isIdleNow()) {
                callback.onTransitionToIdle();
            }
        }
    }

    /**
     * Condition-based IdlingResource
     * Waits until a condition is met
     */
    public static class ConditionIdlingResource implements IdlingResource {
        private final String name;
        private final java.util.function.Supplier<Boolean> condition;
        private volatile ResourceCallback resourceCallback;

        public ConditionIdlingResource(String name, java.util.function.Supplier<Boolean> condition) {
            this.name = name;
            this.condition = condition;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean isIdleNow() {
            boolean idle = condition.get();

            if (idle && resourceCallback != null) {
                resourceCallback.onTransitionToIdle();
            }

            return idle;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback callback) {
            this.resourceCallback = callback;
            // Check if already idle
            if (isIdleNow()) {
                callback.onTransitionToIdle();
            }
        }
    }

    /**
     * Create and register a time-based IdlingResource
     */
    public static void waitFor(String name, long duration, TimeUnit timeUnit) {
        TimeBasedIdlingResource resource = new TimeBasedIdlingResource(name, duration, timeUnit);
        register(name, resource);
    }

    /**
     * Create and register a condition-based IdlingResource
     */
    public static void waitUntil(String name, java.util.function.Supplier<Boolean> condition) {
        ConditionIdlingResource resource = new ConditionIdlingResource(name, condition);
        register(name, resource);
    }

    /**
     * Create and register a counting IdlingResource
     */
    public static SimpleCountingIdlingResource createCountingResource(String name) {
        SimpleCountingIdlingResource resource = new SimpleCountingIdlingResource(name);
        register(name, resource);
        return resource;
    }
}

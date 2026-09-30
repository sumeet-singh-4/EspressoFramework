package com.espresso.framework.utils;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.espresso.framework.exceptions.DataReadException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * JSON Reader Utility using Jackson
 * Reads JSON files from assets folder
 * Fixed: Resource leaks, proper exception handling, input validation
 */
public class JsonReader {
    private static final Logger logger = LoggerFactory.getLogger(JsonReader.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Read JSON file and return as Map
     * Fixed: Uses try-with-resources, throws exception on failure
     */
    public static Map<String, Object> readJson(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        try (InputStream inputStream = context.getAssets().open(filePath)) {
            JsonNode rootNode = objectMapper.readTree(inputStream);
            Map<String, Object> result = objectMapper.convertValue(rootNode, Map.class);
            logger.info("Successfully read JSON file: {}", filePath);
            return result;
        } catch (IOException e) {
            logger.error("Failed to read JSON file: {}", filePath, e);
            throw new DataReadException(filePath, "JSON", e);
        }
    }

    /**
     * Read JSON file and convert to specific class type
     * Fixed: Uses try-with-resources, throws exception on failure
     */
    public static <T> T readJson(String filePath, Class<T> valueType) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        if (valueType == null) {
            throw new IllegalArgumentException("Value type cannot be null");
        }

        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        try (InputStream inputStream = context.getAssets().open(filePath)) {
            T result = objectMapper.readValue(inputStream, valueType);
            logger.info("Successfully read JSON file and converted to {}: {}", valueType.getSimpleName(), filePath);
            return result;
        } catch (IOException e) {
            logger.error("Failed to read JSON file: {}", filePath, e);
            throw new DataReadException(filePath, "JSON", e);
        }
    }

    /**
     * Get specific value from JSON file by key
     */
    public static String getJsonValue(String filePath, String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }

        Map<String, Object> jsonMap = readJson(filePath);
        Object value = jsonMap.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * Get nested value from JSON using dot notation
     * Example: "user.address.city"
     * Fixed: Uses try-with-resources, better error handling
     */
    public static Object getNestedValue(String filePath, String keyPath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        if (keyPath == null || keyPath.isEmpty()) {
            throw new IllegalArgumentException("Key path cannot be null or empty");
        }

        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        try (InputStream inputStream = context.getAssets().open(filePath)) {
            JsonNode rootNode = objectMapper.readTree(inputStream);

            String[] keys = keyPath.split("\\.");
            JsonNode currentNode = rootNode;

            for (String key : keys) {
                if (currentNode.has(key)) {
                    currentNode = currentNode.get(key);
                } else {
                    logger.warn("Key '{}' not found in JSON path: {}", key, keyPath);
                    return null;
                }
            }

            // Return appropriate type
            if (currentNode.isTextual()) {
                return currentNode.asText();
            } else if (currentNode.isInt()) {
                return currentNode.asInt();
            } else if (currentNode.isLong()) {
                return currentNode.asLong();
            } else if (currentNode.isDouble()) {
                return currentNode.asDouble();
            } else if (currentNode.isBoolean()) {
                return currentNode.asBoolean();
            } else if (currentNode.isNull()) {
                return null;
            } else {
                return currentNode.toString();
            }
        } catch (IOException e) {
            logger.error("Failed to read nested value from JSON: {}", filePath, e);
            throw new DataReadException(filePath, "JSON", e);
        }
    }

    /**
     * Read JSON array from file
     * Fixed: Uses try-with-resources, throws exception on failure
     */
    public static <T> T[] readJsonArray(String filePath, Class<T[]> valueType) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        if (valueType == null) {
            throw new IllegalArgumentException("Value type cannot be null");
        }

        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        try (InputStream inputStream = context.getAssets().open(filePath)) {
            T[] result = objectMapper.readValue(inputStream, valueType);
            logger.info("Successfully read JSON array from file: {}", filePath);
            return result;
        } catch (IOException e) {
            logger.error("Failed to read JSON array from file: {}", filePath, e);
            throw new DataReadException(filePath, "JSON array", e);
        }
    }
}

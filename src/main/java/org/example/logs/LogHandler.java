package org.example.logs;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.enums.LoggerType;
import org.example.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class handles logging functionality for different log types, such as INFO, ERROR, and WARNING. It provides
 * methods to log messages based on the log type, store them in an internal map, and retrieve them as JSON or plain
 * text. It uses the SLF4J logger for output and supports logging the messages for different log levels.
 */
public class LogHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogHandler.class);
    private static final Map<LoggerType, List<LogData>> loggerMap = new HashMap<>();

    private LogHandler() {}

    /**
     * Handles the logging of a message based on its type (INFO, ERROR, or WARNING).
     * This method logs the message using the appropriate SLF4J logger method and stores the log in the internal map.
     *
     * @param logger The SLF4J logger to be used for logging.
     * @param type   The type of log (INFO, ERROR, or WARNING).
     * @param message The message to be logged.
     */
    public static void handleLog(Logger logger, LoggerType type, String message) {
        synchronized (loggerMap) {
            switch (type) {
                case INFO -> logger.info("[" + type.name() + "] " + message);
                case ERROR -> logger.error("[" + type.name() + "] " + message);
                case WARNING -> logger.warn("[" + type.name() + "] " + message);
            }
            addLog(type, new LogData(message, logger.getName()));
        }
    }

    /**
     * Adds the log message to the internal map, categorized by its type. If there are no existing logs for the
     * specified type, it creates a new list to store the logs.
     *
     * @param type The type of log.
     * @param message The log data to be added.
     */
    private static void addLog(LoggerType type, LogData message) {
        List<LogData> loggMessages = loggerMap.get(type);
        if (loggMessages == null) {
            loggMessages = new ArrayList<>();
            loggerMap.put(type, loggMessages);
        }
        loggMessages.add(message);
    }

    /**
     * Retrieves logs of a specific type and returns them as a JSON string. If the logs cannot be converted to JSON, it
     * attempts to return the logs in a plain text format.
     *
     * @param type The type of logs to be retrieved (e.g., INFO, ERROR, WARNING).
     * @return A JSON string representation of the logs, or a message indicating no logs of that type exist.
     */
    public static String getLogsByType(LoggerType type) {
        synchronized (loggerMap) {
            List<LogData> logList = loggerMap.get(type);
            if (logList != null) {
                try {
                    return JsonUtil.toJsonString(logList);
                } catch (JsonProcessingException e) {
                    handleLog(LOGGER, LoggerType.ERROR, "Failed to format logs as json string: " + e.getMessage());
                    return getListAsString(type, logList);
                }
            }
        }
        return "No '" + type.name() + "' logs registered.";
    }

    /**
     * Retrieves all logs from the internal map and returns them as a JSON string. If the logs cannot be converted to
     * JSON, it attempts to return the logs in a plain text format.
     *
     * @return A JSON string representation of all logs, or a message indicating no logs have been created.
     */
    public static String getAllLogs() {
        synchronized (loggerMap) {
            try {
                return (!loggerMap.isEmpty()) ? JsonUtil.toJsonString(loggerMap) : "No logs created.";
            } catch (JsonProcessingException e) {
                handleLog(LOGGER, LoggerType.ERROR, "Failed to format logs as json string: " + e.getMessage());
                return getMapAsString();
            }
        }
    }

    /**
     * Converts a list of log data to a plain text string representation. The logs are formatted in a human-readable
     * way, including the log type and message.
     *
     * @param type The type of log.
     * @param list The list of log data to be converted.
     * @return A string representation of the logs.
     */
    private static String getListAsString(LoggerType type, List<LogData> list) {
        StringBuilder sb = new StringBuilder(type.name());
        sb.append(": ").append("\n").append("[ ");
        for (LogData data : list) {
            sb.append(data).append(", ");
        }
        sb.setLength(sb.length() - 2);
        return sb.append(" ]").toString();
    }

    /**
     * Converts all logs in the internal map to a plain text string representation. The logs are grouped by their type
     * and formatted in a human-readable way.
     *
     * @return A string representation of all logs.
     */
    private static String getMapAsString() {
        if (!loggerMap.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (LoggerType type : loggerMap.keySet()) {
                List<LogData> dataList = loggerMap.get(type);
                sb.append(getListAsString(type, dataList));
            }
            return sb.toString();
        }
        return "No logs created.";
    }
}

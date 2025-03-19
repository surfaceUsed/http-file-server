package org.example.enums;

import java.util.Map;

/**
 * Enum representing different types of log messages that the server can handle. This includes informational messages,
 * errors, and warnings. The enum also provides a mapping of command-line flags to corresponding log types.
 */
public enum LoggerType {

    INFO, ERROR, WARNING, INVALID;

    private static final Map<String, LoggerType> loggerCommand = Map.of(
            "--info", INFO,
            "--error", ERROR,
            "--warn", WARNING);

    /**
     * Retrieves the corresponding LoggerType based on the given command string.
     * If the provided command does not match any predefined log type, it defaults to INVALID.
     *
     * @param command the log command string (e.g., "--info", "--error", "--warn")
     * @return the corresponding LoggerType (INFO, ERROR, WARNING, or INVALID if not found)
     */
    public static LoggerType getType(String command) {
        LoggerType type = loggerCommand.get(command);
        return (type != null) ? type : INVALID;
    }
}

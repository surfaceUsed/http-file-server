package org.example.logs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This class represents a log entry with a message, source class, and timestamp. It is used to store information about
 * a log event, including the message, the source class that generated the log, and the timestamp when the log was
 * created. The timestamp is formatted according to the defined pattern.
 */
public class LogData {

    private static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm";

    private final String message;
    private final String sourceClass;
    private final String timeStamp;

    /**
     * Constructs a new LogData object with the given message and source class. The timestamp is automatically set to
     * the current date and time when the log is created.
     *
     * @param message     The log message.
     * @param sourceClass The class where the log is generated.
     */
    public LogData(String message, String sourceClass) {
        this.message = message;
        this.sourceClass = sourceClass;
        this.timeStamp = formatTime(LocalDateTime.now());
    }

    /**
     * Formats the given LocalDateTime object into a string representation. The timestamp is formatted using the defined
     * date-time format pattern "dd.MM.yyyy HH:mm".
     *
     * @param time The LocalDateTime to format.
     * @return A string representation of the formatted time.
     */
    private String formatTime(LocalDateTime time) {
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        return (time.format(pattern));
    }

    public String getSourceClass() {
        return sourceClass;
    }

    public String getMessage() {
        return message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    @Override
    public String toString() {
        return "{\n" +
                "\t\"message\": \"" + this.message + "\",\n" +
                "\t\"className\": \"" + this.sourceClass + "\",\n" +
                "\t\"timeStamp\": \"" + this.timeStamp + "\"\n" +
                "}";
    }
}

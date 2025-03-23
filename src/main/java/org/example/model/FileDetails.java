package org.example.model;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents detailed metadata for a file in the system. This class stores various attributes of a file, such as:
 *
 *     1. A unique file ID.
 *     2. The file name.
 *     3. The file type (determined by its extension).
 *     4. The file size (formatted in KB and bytes).
 *     5. The timestamp of when the file was created.
 *     6. The timestamp of the last update.
 *
 * The class provides methods to retrieve, update, and compare file details. It also includes utility functions to
 * determine file types and format timestamps.
 *
 * Note: If the file type cannot be determined, it is labeled as <NULL>. Directories are identified with the <DIR>
 * label.
 */
public class FileDetails implements Comparable<FileDetails> {

    private static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm";
    private static final char FILE_SEPARATOR = '.';
    private static final String UNDEFINED_FILE_TYPE = "<NULL>";
    private static final String DIRECTORY_TYPE = "<DIR>";

    private long fileId;
    private String fileName;
    private String fileType;
    private String fileSize;
    private final String timeCreated;
    private String timeUpdated;

    /**
     * Default constructor.
     */
    public FileDetails() {
        this.timeCreated = formatTime(LocalDateTime.now());
        this.timeUpdated = formatTime(LocalDateTime.now());
    }

    /**
     * Constructs a FileDetails object from a given File and assigns it an ID.
     *
     * @param file   The file to retrieve metadata from.
     * @param fileId A unique identifier assigned to the file.
     */
    public FileDetails(File file, long fileId) {
        this.fileName = file.getName();
        this.fileId = fileId;
        this.fileType = getFileType(file);
        this.fileSize = convertToKB(file.length());
        this.timeCreated = formatTime(LocalDateTime.now());
        this.timeUpdated = formatTime(LocalDateTime.now());
    }

    /**
     * Converts file size from bytes to a formatted string in KB and bytes.
     *
     * @param size The file size in bytes.
     * @return A formatted string representation of the file size.
     */
    private String convertToKB(long size) {
        return (int) (size / 1024) + " kb (" + size + " bytes)";
    }

    /**
     * Determines the file type based on its extension. Returns <NULL> if the type cannot be determined and <DIR> if the
     * file is a directory.
     *
     * @param file The file to check.
     * @return A string representing the file type.
     */
    private static String getFileType(File file) {
        if (!file.isDirectory()) {
            String fileName = file.getName();
            StringBuilder sb = new StringBuilder();
            char character = ' ';
            int start = fileName.length() - 1;
            while (character != FILE_SEPARATOR) {
                sb.append(fileName.charAt(start));
                character = fileName.charAt(--start);
                if (start == 0 && character != FILE_SEPARATOR) {
                    return UNDEFINED_FILE_TYPE;
                }
            }
            return "<" + sb.reverse().toString().toUpperCase() + ">";
        }
        return DIRECTORY_TYPE;
    }

    /**
     * Checks whether two files have the same file type.
     *
     * @param fileName Name of the first file.
     * @param compName Name of the second file.
     * @return true if both files share the same type, otherwise false.
     */
    public static boolean isEqualFileType(String fileName, String compName) {
        File one = new File(fileName);
        File two = new File(compName);
        return getFileType(one).equals(getFileType(two));
    }

    /**
     * Gets the unique file ID.
     *
     * @return The file ID.
     */
    public long getFileId() {
        return fileId;
    }

    /**
     * Gets the file name.
     *
     * @return The file name.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Gets the file type.
     *
     * @return The file type.
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * Gets the file size in a formatted string.
     *
     * @return The file size.
     */
    public String getFileSize() {
        return fileSize;
    }

    /**
     * Gets the timestamp of when the file was created.
     *
     * @return The creation timestamp.
     */
    public String getTimeCreated() {
        return timeCreated;
    }

    /**
     * Gets the timestamp of the last update made to the file.
     *
     * @return The last updated timestamp.
     */
    public String getTimeUpdated() {
        return timeUpdated;
    }

    /**
     * Updates the file size.
     *
     * @param size The new file size in bytes.
     */
    public void updateFileSize(long size) {
        this.fileSize = convertToKB(size);
    }

    /**
     * Updates the file name and refreshes the last updated timestamp.
     *
     * @param fileName The new file name.
     */
    public void updateFileName(String fileName) {
        this.fileName = fileName;
        updateStatus();
    }

    /**
     * Updates the last modified timestamp to the current time.
     */
    public void updateStatus() {
        this.timeUpdated = formatTime(LocalDateTime.now());
    }

    /**
     * Formats a LocalDateTime object into a string.
     *
     * @param time The time to format.
     * @return A formatted string representing the time.
     */
    private String formatTime(LocalDateTime time) {
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        return time.format(pattern);
    }

    /**
     * Checks whether the file name or file ID contains the given query.
     *
     * @param query The keyword to search for.
     * @return true if the keyword is found, otherwise false.
     */
    public boolean contains(String query) {
        return (this.fileName.contains(query) || query.contains(String.valueOf(this.fileId)));
    }

    /**
     * Compares this file with another based on their IDs.
     *
     * @param fileDetails The file to compare against.
     * @return A negative integer, zero, or a positive integer as this file ID is less than, equal to,
     *         or greater than the specified file ID.
     */
    @Override
    public int compareTo(FileDetails fileDetails) {
        return Long.compare(this.fileId, fileDetails.getFileId());
    }

    /**
     * Checks whether this file is equal to another object.
     *
     * @param obj The object to compare.
     * @return true if both objects have the same file ID, otherwise false.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        FileDetails fileDetails = (FileDetails) obj;
        return this.fileId == fileDetails.getFileId();
    }

    /**
     * Returns a string representation of the file metadata in JSON format.
     */
    @Override
    public String toString() {
        return "{\n" +
                "\t\"fileId\": " + this.fileId + ",\n" +
                "\t\"fileName\": \"" + this.fileName + "\",\n" +
                "\t\"fileType\": \"" + this.fileType + "\",\n" +
                "\t\"fileSize\": \"" + this.fileSize + "\",\n" +
                "\t\"timeCreated\": \"" + this.timeCreated + "\",\n" +
                "\t\"timeUpdated\": \"" + this.timeUpdated + "\"\n" +
                "}";
    }
}

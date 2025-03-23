package org.example.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility class for file operations such as reading, writing, and converting files. Provides methods to read a file as
 * a string or byte array, and write data to files.
 */
public final class FileUtil {

    private static final int BUFFER_SIZE = 8192; // 8192 KB

    private FileUtil() {}

    /**
     * Reads the content of a file and returns it as a String.
     *
     * @param path The file path as a string.
     * @return The content of the file as a string.
     * @throws IOException If an I/O error occurs or if the file is not found.
     */
    public static String getFileAsString(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + path);
        }
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }

    /**
     * Reads the content of a file and returns it as a byte array.
     *
     * @param file The file to be read.
     * @return A byte array containing the file's contents.
     * @throws IOException If an I/O error occurs or if the file is not found.
     */
    public static byte[] getFileAsBytes(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
        }

        try (BufferedInputStream fromFile = new BufferedInputStream(new FileInputStream(file));
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            byte[] bytesFromFile = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = fromFile.read(bytesFromFile)) != -1) {
                out.write(bytesFromFile, 0, bytesRead);
            }

            return out.toByteArray();
        }
    }

    /**
     * Writes a text string to a file.
     *
     * @param path The file path where the JSON should be written.
     * @param text The JSON content to write.
     * @throws IOException If an I/O error occurs during writing.
     */
    public static void writeTextToFile(String path, String text) throws IOException {
        Files.write(Paths.get(path), text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Writes a byte array to a specified file.
     *
     * @param path The file object where the data should be written.
     * @param fileInBytes The byte array containing file data.
     * @throws IOException If an I/O error occurs during writing.
     */
    public static void writeBytesToFile(File path, byte[] fileInBytes) throws IOException {
        Files.write(path.toPath(), fileInBytes);
    }
}

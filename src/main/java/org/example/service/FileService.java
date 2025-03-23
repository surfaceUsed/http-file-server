package org.example.service;

import org.example.error.FileSystemException;
import org.example.model.FileDetails;
import org.example.model.Identifier;

import java.io.IOException;
import java.util.List;

/**
 * Defines the contract for file storage operations. This interface provides methods for managing files, including
 * adding, retrieving, updating, deleting, and listing file details.
 */
public interface FileService {

    /**
     * Adds a new file to the file system.
     *
     * @param fileName the name of the file to add.
     * @param byteFile the byte array representing the file content.
     * @return the unique identifier of the newly added file.
     * @throws FileSystemException if an error occurs while adding the file.
     */
    long add(String fileName, byte[] byteFile) throws FileSystemException;

    /**
     * Retrieves the content of a file based on its identifier.
     *
     * @param identifier the unique identifier of the file.
     * @return the file content as a byte array.
     * @throws FileSystemException if the file cannot be found or accessed.
     */
    byte[] retrieve(Identifier identifier) throws FileSystemException;

    /**
     * Searches for a file using its identifier and retrieves its metadata.
     *
     * @param identifier the unique identifier of the file.
     * @return a FileDetails object containing file metadata.
     * @throws FileSystemException if the file cannot be found.
     */
    FileDetails search(Identifier identifier) throws FileSystemException;

    /**
     * Lists all files that match a given search query.
     *
     * @param query the search query used to filter files.
     * @return a list of FileDetails objects that match the query.
     */
    List<FileDetails> list(String query);

    /**
     * Deletes a file from the file system.
     *
     * @param identifier the unique identifier of the file to delete.
     * @throws FileSystemException if an error occurs while deleting the file.
     */
    void delete(Identifier identifier) throws FileSystemException;

    /**
     * Updates file metadata, such as renaming a file.
     *
     * @param identifier  the unique identifier of the file.
     * @param updateValue the new value to update (e.g., new file name).
     * @throws FileSystemException if an error occurs while updating the file.
     */
    void update(Identifier identifier, String updateValue) throws FileSystemException;

    /**
     * Overrides the content of an existing file with new data.
     *
     * @param identifier the unique identifier of the file.
     * @param byteFile   the new file content as a byte array.
     * @throws FileSystemException if an error occurs during the override operation.
     */
    void override(Identifier identifier, byte[] byteFile) throws FileSystemException;

    /**
     * Closes the file system and releases any resources.
     *
     * @throws IOException if an error occurs while saving or closing the file system.
     */
    void close() throws IOException;
}

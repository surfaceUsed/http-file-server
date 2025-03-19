package org.example.service;

import org.example.config.ConfigurationManager;
import org.example.config.FileSystem;
import org.example.error.FileSystemException;
import org.example.model.FileDetails;
import org.example.model.Identifier;
import java.io.IOException;
import java.util.List;

/**
 * Singleton service class that provides file storage operations for a server-based cloud storage system. This class
 * acts as a bridge between the client requests and the underlying FileSystem implementation.
 */
public class ServerCloudStorage implements FileService {

    private static FileService INSTANCE;
    private final FileSystem fileSystem;

    /**
     * Private constructor to enforce singleton pattern. Initializes the file system instance from the configuration
     * manager.
     */
    private ServerCloudStorage() {
        this.fileSystem = ConfigurationManager.getInstance().getFileSystem();
    }

    /**
     * Returns the singleton instance of ServerCloudStorage.
     *
     * @return the singleton instance of FileService.
     */
    public static FileService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ServerCloudStorage();
        }
        return INSTANCE;
    }

    /**
     * Adds a new file to the file system.
     *
     * @param fileName the name of the file to add.
     * @param byteFile the byte array representing the file contents.
     * @return the generated file ID.
     * @throws FileSystemException if an error occurs while adding the file.
     */
    @Override
    public long add(String fileName, byte[] byteFile) throws FileSystemException {
        return this.fileSystem.addFile(fileName, byteFile);
    }

    /**
     * Retrieves a file from the file system.
     *
     * @param identifier the unique identifier (ID or name) of the file.
     * @return the file contents as a byte array.
     * @throws FileSystemException if the file cannot be found or accessed.
     */
    @Override
    public byte[] retrieve(Identifier identifier) throws FileSystemException {
       return this.fileSystem.getFile(identifier);

    }

    /**
     * Searches for a file in the file system using an identifier.
     *
     * @param identifier the unique identifier (ID or name) of the file.
     * @return the metadata of the located file.
     * @throws FileSystemException if the file cannot be found.
     */
    @Override
    public FileDetails search(Identifier identifier) throws FileSystemException {
        return this.fileSystem.viewFile(identifier);
    }

    /**
     * Lists all files that match a search query.
     *
     * @param query the search query used to filter file names.
     * @return a list of file details matching the query.
     */
    @Override
    public List<FileDetails> list(String query) {
        return this.fileSystem.listFiles(query);
    }

    /**
     * Deletes a file from the file system.
     *
     * @param identifier the unique identifier (ID or name) of the file to delete.
     * @throws FileSystemException if an error occurs while deleting the file.
     */
    @Override
    public void delete(Identifier identifier) throws FileSystemException {
        this.fileSystem.delete(identifier);
    }

    /**
     * Updates file metadata (such as file name).
     *
     * @param identifier   the unique identifier of the file to update.
     * @param updateValue  the new value to update.
     * @throws FileSystemException if an error occurs while updating the file.
     */
    @Override
    public void update(Identifier identifier, String updateValue) throws FileSystemException {
        this.fileSystem.update(identifier, updateValue);
    }

    /**
     * Replaces an existing file's contents with new data.
     *
     * @param identifier the unique identifier (ID or name) of the file.
     * @param byteFile   the new file content as a byte array.
     * @throws FileSystemException if an error occurs during the override operation.
     */
    @Override
    public void override(Identifier identifier, byte[] byteFile) throws FileSystemException {
        this.fileSystem.override(identifier, byteFile);
    }

    /**
     * Saves the current state of the file system to persistent storage.
     *
     * @throws IOException if an error occurs while saving the file system.
     */
    @Override
    public void close() throws IOException {
        this.fileSystem.closeFileSystem();
    }
}

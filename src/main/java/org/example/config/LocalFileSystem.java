package org.example.config;

import org.example.enums.HttpResponseStatus;
import org.example.enums.LoggerType;
import org.example.error.FileRollbackException;
import org.example.error.FileSystemException;
import org.example.error.ServerConfigurationException;
import org.example.logs.LogHandler;
import org.example.model.FileDetails;
import org.example.model.Identifier;
import org.example.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LocalFileSystem implements FileSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileSystem.class);
    private static final String LIST_ALL = "all";

    private final String fileSystemUrl;
    private final FileMetadataTracker dataTracker;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Private constructor to initialize the file system with a given path and metadata tracker.
     *
     * @param fileSystemUrl The path to the file system.
     * @param dataTracker   The metadata tracker for managing file details.
     */
    private LocalFileSystem(String fileSystemUrl, FileMetadataTracker dataTracker) {
        this.fileSystemUrl = fileSystemUrl;
        this.dataTracker = dataTracker;
    }

    /**
     * Loads the file system by ensuring the specified directory exists or creating it if necessary.
     *
     * @param path The file system directory path.
     * @param dataTracker The metadata tracker instance.
     * @return A new instance of FileSystem.
     * @throws ServerConfigurationException If the directory cannot be created or is not valid.
     */
    static FileSystem loadFileSystem(String path, FileMetadataTracker dataTracker) {
        File file = new File(path);
        boolean isDirectoryCreated;
        if (!file.exists()) {
            isDirectoryCreated = file.mkdirs();
            if (!isDirectoryCreated) {
                throw new ServerConfigurationException("Failed to implement server directory.");
            }
        } else if (!file.isDirectory()) {
            throw new ServerConfigurationException("Loaded path to file directory is not valid.");
        }
        return new LocalFileSystem(path, dataTracker);
    }

    /**
     * POST
     *
     * Adds a new file to the file system and updates the metadata tracker.
     * If an error occurs during file writing, a rollback is performed.
     *
     * @param fileName The name of the file to be added.
     * @param fileAsBytes The file content as a byte array.
     * @return The unique ID of the newly added file.
     * @throws FileSystemException If an error occurs while adding the file.
     */
    @Override
    public long addFile(String fileName, byte[] fileAsBytes) throws FileSystemException {
        this.lock.writeLock().lock();
        try {
            File file = createNewFileInFileSystem(fileName);
            try {
                FileUtil.writeBytesToFile(file, fileAsBytes);
                return this.dataTracker.addNewFile(file);
            } catch (IOException e) {
                try {
                    fileCreationRollback(file);
                } catch (FileRollbackException ex) {
                    LogHandler.handleLog(LOGGER, LoggerType.ERROR, ex.getMessage());
                    LogHandler.handleLog(LOGGER, LoggerType.WARNING, "' needs to be deleted manually from " +
                            "file system as soon as possible to prevent further complications: " + file.getPath());
                }
                throw new FileSystemException("Error creating new file on the server: " + e.getMessage(),
                        HttpResponseStatus.SERVER_ERROR_INTERNAL_SERVER_ERROR, e);
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Rolls back file creation in case of an error by deleting the empty file.
     *
     * @param file The file to be deleted.
     * @throws FileRollbackException If the file cannot be deleted.
     */
    private void fileCreationRollback(File file) throws FileRollbackException {
        if (file.exists() && !file.delete()) {
            throw new FileRollbackException("Critical error; could not remove the empty file object '" +
                    file.getName() + "' after failing to add a new file in the file system");
        }
    }

    /**
     * Retrieves a file's content based on its identifier (ID or name).nThe method checks if the identifier is numeric
     * or text and fetches the file accordingly.
     *
     * @param identifier The identifier of the file (either numeric or text).
     * @return A byte array containing the content of the requested file.
     * @throws FileSystemException If the file cannot be found or there is an error retrieving it.
     */
    @Override
    public byte[] getFile(Identifier identifier) throws FileSystemException {
        this.lock.readLock().lock();
        try {
            String fileName;
            if (identifier.getIdentifierDataType().isNumeric()) {
                fileName = this.dataTracker.searchFileById(identifier.getNumericIdentifier()).getFileName();
            } else if (identifier.getIdentifierDataType().isText()) {
                fileName = identifier.getStringIdentifier();
            } else {
                throw new FileSystemException("Invalid file identifier",
                        HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);
            }
            return getFileByName(fileName);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    /**
     * GET
     *
     * Retrieves a file's content as a byte array by searching with its name.
     *
     * @param fileName The name of the file.
     * @return The file content as a byte array.
     * @throws FileSystemException If the file does not exist or cannot be read.
     */
    private byte[] getFileByName(String fileName) throws FileSystemException {
        if (fileName == null || fileName.isEmpty()) {
            throw new FileSystemException("File name is null or empty.",
                    HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);
        }
        try {
            File file = createFilePath(fileName);
            if (file.exists()) {
                byte[] byteFile = FileUtil.getFileAsBytes(file);
                if (byteFile.length == 0) {
                    throw new FileSystemException("The file is empty or cannot be read.",
                            HttpResponseStatus.SERVER_ERROR_INTERNAL_SERVER_ERROR);
                }
                return byteFile;
            } else {
                throw new FileSystemException("File does not exist on the server.",
                        HttpResponseStatus.CLIENT_ERROR_NOT_FOUND);
            }
        } catch (IOException e) {
            throw new FileSystemException("Error reading file: " + e.getMessage(),
                    HttpResponseStatus.SERVER_ERROR_INTERNAL_SERVER_ERROR, e);
        }
    }

    /**
     * Retrieves the metadata details of a file. The method fetches file information such as name, size, creation date,
     * etc., based on the file identifier.
     *
     * @param identifier The identifier of the file (either numeric or text).
     * @return A `FileDetails` object containing the metadata of the file.
     * @throws FileSystemException If there is an error retrieving the file details.
     */
    @Override
    public FileDetails viewFile(Identifier identifier) throws FileSystemException {
        this.lock.readLock().lock();
        try {
            return getFileDetails(identifier);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    /**
     * Lists files based on a search query. If the query is equal to `LIST_ALL`, all files are listed. Otherwise, the
     * files matching the query are returned. The list is sorted before returning the results.
     *
     * @param query The query string used to filter the files. If it equals `LIST_ALL`, all files are listed.
     * @return A list of `FileDetails` objects that match the search query.
     */
    @Override
    public List<FileDetails> listFiles(String query) {
        this.lock.readLock().lock();
        try {
            List<FileDetails> matchingFiles = (query.equals(LIST_ALL)) ?
                    listAll(this.dataTracker.getData()) :
                    listByKeyword(this.dataTracker.getData(), query);
            Collections.sort(matchingFiles);
            return matchingFiles;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    /**
     * Retrieves a list of all file details stored in the file system.
     *
     * @param files a map where each entry consists of a file ID as the key
     *              and the corresponding FileDetails object as the value.
     * @return a list containing the details of all files in the system.
     */
    private List<FileDetails> listAll(Map<Long, FileDetails> files) {
        List<FileDetails> fileDetails = new ArrayList<>();
        for (long fileId : files.keySet()) {
            fileDetails.add(files.get(fileId));
        }
        return fileDetails;
    }

    /**
     * Searches for files whose details contain a specified keyword.
     *
     * @param files   a map where each entry consists of a file ID as the key
     *                and the corresponding FileDetails object as the value.
     * @param query the search term used to filter the files.
     * @return a list of FileDetails objects that match the given keyword.
     */
    private List<FileDetails> listByKeyword(Map<Long, FileDetails> files, String query) {
        List<FileDetails> fileDetails = new ArrayList<>();
        for (Long fileId : files.keySet()) {
            FileDetails details = files.get(fileId);
            if (details.contains(query)) {
                fileDetails.add(details);
            }
        }
        return fileDetails;
    }

    /**
     * Overrides the content of an existing file. The method replaces the current content of the file with the provided
     * new content.
     *
     * @param identifier The identifier of the file to be overridden.
     * @param overrideData The new content to replace the existing file's content.
     * @throws FileSystemException If there is an error overriding the file content.
     */
    @Override
    public void override(Identifier identifier, byte[] overrideData) throws FileSystemException {
        this.lock.writeLock().lock();
        try {
            FileDetails fileDetails = getFileDetails(identifier);
            overrideFileData(fileDetails, overrideData);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Helper method for overriding a file's content using its metadata.
     * Ensures the file exists before replacing its content.
     *
     * @param fileDetails The metadata of the file.
     * @param overrideData The new file content.
     * @throws FileSystemException If the file does not exist or cannot be overridden.
     */
    private void overrideFileData(FileDetails fileDetails, byte[] overrideData) throws FileSystemException {
        File file = findFile(fileDetails.getFileName());
        try {
            FileUtil.writeBytesToFile(file, overrideData);
            fileDetails.updateFileSize(overrideData.length);
            fileDetails.updateStatus();
        } catch (IOException e) {
            throw new FileSystemException("An unexpected error occurred while attempting to override file",
                    HttpResponseStatus.SERVER_ERROR_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates the file name of an existing file.
     *
     * @param identifier The identifier of the file to be updated.
     * @param updateData The new metadata or name to update the file with.
     * @throws FileSystemException If there is an error updating the file.
     */
    @Override
    public void update(Identifier identifier, String updateData) throws FileSystemException {
        this.lock.writeLock().lock();
        try {
            FileDetails fileDetails = getFileDetails(identifier);
            updateFileName(fileDetails, updateData);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Helper method for updating a file's name.
     * Renames a file in the file system if the new name is not already in use, and the metadata is also updated.
     *
     * @param fileDetails     The file details of the existing file.
     * @param updatedData The new name for the file.
     * @throws FileSystemException If the file does not exist or if the new name is already in use.
     */
    private void updateFileName(FileDetails fileDetails, String updatedData) throws FileSystemException {
        File fileToUpdate = findFile(fileDetails.getFileName());
        File updatedFile = getFileIfNotExist(updatedData);
        if (fileToUpdate.renameTo(updatedFile)) {
            this.dataTracker.updateFile(fileDetails, updatedData);
        }
    }

    /**
     * Deletes a file from the system. The method removes the file from the file system and deletes its metadata.
     *
     * @param identifier The identifier of the file to be deleted.
     * @throws FileSystemException If there is an error deleting the file.
     */
    @Override
    public void delete(Identifier identifier) throws FileSystemException {
        this.lock.writeLock().lock();
        try {
            FileDetails fileDetails = getFileDetails(identifier);
            deleteFile(fileDetails);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Helper method for deleting a file.
     * Deletes a file from the file system and removes its entry from the tracker.
     *
     * @param fileDetails The file details of the file to delete.
     * @throws FileSystemException If the file does not exist or cannot be deleted.
     */
    private void deleteFile(FileDetails fileDetails) throws FileSystemException {
        File fileToDelete = findFile(fileDetails.getFileName());
        if (fileToDelete.delete()) {
            this.dataTracker.delete(fileDetails);
        }
    }

    /**
     * Retrieves the metadata details of a file based on its identifier type. The method fetches file details like name,
     * size, creation date, etc., using the file's ID or name.
     *
     * @param identifier The identifier (ID or name) of the file.
     * @return A `FileDetails` object containing the file's metadata.
     * @throws FileSystemException If the identifier is invalid or if the file cannot be found.
     */
    private FileDetails getFileDetails(Identifier identifier) throws FileSystemException {
        FileDetails fileDetails;
        if (identifier.getIdentifierDataType().isNumeric()) {
            fileDetails = this.dataTracker.searchFileById(identifier.getNumericIdentifier());
        } else if (identifier.getIdentifierDataType().isText()) {
            fileDetails = this.dataTracker.searchFileByName(identifier.getStringIdentifier());
        } else {
            throw new FileSystemException("Invalid file identifier",
                    HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);
        }
        return fileDetails;
    }

    /**
     * Saves the current state of the file system.
     *
     * @throws IOException If an error occurs while saving the data.
     */
    @Override
    public void closeFileSystem() throws IOException {
        synchronized (this.dataTracker) {
            this.dataTracker.saveDataToFile();
        }
    }

    /**
     * Constructs a File object representing the given filename within the file system directory.
     *
     * @param fileName The name of the file.
     * @return A File object representing the file path.
     */
    private File createFilePath(String fileName) {
        return new File(this.fileSystemUrl, fileName);
    }

    /**
     * Creates a new file in the file system.
     *
     * @param fileName The name of the new file.
     * @return A File object representing the newly created file.
     * @throws FileSystemException If the file already exists or cannot be created.
     */
    private File createNewFileInFileSystem(String fileName) throws FileSystemException {
        File file = createFilePath(fileName);
        try {
            if (file.createNewFile()) {
                return file;
            } else {
                throw new FileSystemException("Failed to execute, because file with name '" + fileName + "' is already " +
                        "in directory.", HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);
            }
        } catch (IOException e) {
            throw new FileSystemException("Failed to create new file with file name \"" + fileName + "\".",
                    HttpResponseStatus.SERVER_ERROR_INTERNAL_SERVER_ERROR, e);
        }
    }

    /**
     * Returns a File object if the file does not already exist.
     *
     * @param fileName The name of the file to check.
     * @return A File object representing the file path.
     * @throws FileSystemException If the file already exists.
     */
    private File getFileIfNotExist(String fileName) throws FileSystemException {
        File file = createFilePath(fileName);
        if (file.exists()) {
            throw new FileSystemException("File with name '" + fileName + "' already exist on the server.",
                    HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);
        }
        return file;
    }

    /**
     * Retrieves an existing file from the file system.
     *
     * @param fileName The name of the file to retrieve.
     * @return A File object representing the existing file.
     * @throws FileSystemException If the file does not exist.
     */
    private File findFile(String fileName) throws FileSystemException {
        File retreiveFile = createFilePath(fileName);
        if (retreiveFile.exists()) {
            return retreiveFile;
        } else {
            throw new FileSystemException("File with name '" + fileName + "' does not exist on the server.",
                    HttpResponseStatus.CLIENT_ERROR_NOT_FOUND);
        }
    }
}

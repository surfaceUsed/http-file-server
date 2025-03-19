package org.example.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.enums.HttpResponseStatus;
import org.example.error.FileSystemException;
import org.example.error.ServerConfigurationException;
import org.example.model.FileDetails;
import org.example.util.FileUtil;
import org.example.util.JsonUtil;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The FileMetadataTracker class tracks all the metadata for the files that are added to the file system.
 * It stores the file metadata in a Map, where each file's metadata is associated with a unique ID.
 *
 * The object attributes 'currentId' and 'data' are both deserialized field values from the file
 * 'resources/files_metadata.json'.
 *
 * The ID is incremented every time a new file is added. The Map holds the data corresponding to the file ID,
 * and the ID is never decremented when a file is removed.
 */
class FileMetadataTracker {

    private final AtomicLong currentId;
    private final Map<Long, FileDetails> data;

    /**
     * Uses the last created file id as a starting point for when a new file is added.
     * "data" holds all the metadata entries, and uses the object for every client requests made to the file system.
     */
    private FileMetadataTracker(long currentId, Map<Long, FileDetails> data) {
        this.currentId = new AtomicLong(currentId);
        this.data = data;
    }

    /**
     * Loads the FileMetadataTracker from a JSON file.
     * This method reads the file's metadata and current ID from the specified path and creates a new instance of
     * FileMetadataTracker.
     *
     * @param path The path to the JSON file containing the file metadata and current ID.
     * @param idField The field name that stores the current file ID in the JSON data.
     * @param dataField The field name that stores the file metadata in the JSON data.
     * @return A new instance of FileMetadataTracker initialized with the data from the specified file.
     * @throws ServerConfigurationException If there is an error processing the JSON file.
     */
    static FileMetadataTracker loadDataTracker(String path, String idField, String dataField) {
        try {
            JsonDataLoader jsonLoader = new JsonDataLoader(path);
            long currentId = jsonLoader.getFieldNode(idField).asLong();
            Map<Long, FileDetails> dataMap = JsonUtil.parseJsonToMap(jsonLoader.getFieldNode(dataField),
                    new TypeReference<Map<Long, FileDetails>>() {});
            return new FileMetadataTracker(currentId, dataMap);
        } catch (JsonProcessingException e) {
            throw new ServerConfigurationException("Failed to format data as JSON format: " + e.getMessage());
        }
    }

    /**
     * Returns the Map containing all the metadata.
     */
    Map<Long, FileDetails> getData() {
        return this.data;
    }

    /**
     * Returns the current file ID.
     */
    long getCurrentId() {
        return this.currentId.get();
    }

    /**
     * Adds a new file to the tracker.
     * It increments the current ID and associates the new file with the new ID.
     *
     * @param file The file to add.
     * @return The newly assigned ID for the added file.
     */
    long addNewFile(File file) {
        long newId = this.currentId.incrementAndGet();
        this.data.put(newId, new FileDetails(file, newId));
        return newId;
    }

    /**
     * Updates the metadata of an existing file in the tracker.
     * The current file's metadata is updated with the new information provided.
     *
     * @param currentDetails The current file details to update.
     * @param updateValue The new value to update the file's name with.
     */
    void updateFile(FileDetails currentDetails, String updateValue) {
        currentDetails.updateFileName(updateValue);
        this.data.put(currentDetails.getFileId(), currentDetails);
    }

    /**
     * Deletes a file's metadata from the tracker.
     *
     * @param fileDetails The details of the file to delete.
     */
    void delete(FileDetails fileDetails) {
        this.data.remove(fileDetails.getFileId());
    }

    /**
     * Searches for a file by its name.
     * If the file with the given name is found, it returns its metadata.
     *
     * @param fileName The name of the file to search for.
     * @return The file metadata associated with the file name.
     * @throws FileSystemException If the file with the given name does not exist.
     */
    FileDetails searchFileByName(String fileName) throws FileSystemException {
        for (Map.Entry<Long, FileDetails> fileEntry : this.data.entrySet()) {
            FileDetails value = fileEntry.getValue();
            if (value.getFileName().equals(fileName)) {
                return value;
            }
        }
        throw new FileSystemException("File with name '" + fileName + "' does not exist in file system",
                HttpResponseStatus.CLIENT_ERROR_NOT_FOUND);
    }

    /**
     * Searches for a file by its ID.
     * If the file with the given ID is found, it returns its metadata.
     *
     * @param fileId The ID of the file to search for.
     * @return The file metadata associated with the file ID.
     * @throws FileSystemException If the file with the given ID does not exist.
     */
    FileDetails searchFileById(Long fileId) throws FileSystemException {
        FileDetails fileDetails = this.data.get(fileId);
        if (fileDetails == null) {
            throw new FileSystemException("File with id #" + fileId + " does not exist in the file system",
                    HttpResponseStatus.CLIENT_ERROR_NOT_FOUND);
        }
        return fileDetails;
    }

    /**
     * Saves the file metadata to a JSON file.
     * The current file ID and all file metadata are serialized and written to the specified file path.
     *
     * @throws IOException If there is an issue saving the data to the file.
     */
    void saveDataToFile() throws IOException {
        ServerProperties properties = ConfigurationManager.getInstance().getServerProperties();
        String idField = properties.getMetadataCurrentIdFieldKey(); // Name of json field 'currentId'.
        String dataField = properties.getMetadataDataFieldKey(); // Name of json field 'data'.
        String savePath = properties.getFileMetadataPath(); // Path to save file location.

        ObjectNode node = JsonUtil.getObjectMapper().createObjectNode();
        node.put(idField, this.currentId.get());
        JsonNode dataNode = JsonUtil.getAsJsonNode(this.data);
        node.set(dataField, dataNode);

        FileUtil.writeTextToFile(savePath, JsonUtil.toJsonString(node));
    }
}

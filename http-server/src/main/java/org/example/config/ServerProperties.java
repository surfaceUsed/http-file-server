package org.example.config;

/**
 * The ServerProperties class is responsible for loading and providing access to various configuration properties of the
 * server, such as HTTP version, server name, host, port, file paths, and metadata keys. It uses the Singleton design
 * pattern to ensure that only one instance of the class is created and used across the application.
 *
 * These properties are loaded from the 'application.properties' file and are accessed through various getter methods.
 * The class also includes specific properties for the file system and metadata, which are essential for the server's
 * operation.
 */
public class ServerProperties {

    /**
     * Keys for the specific server properties. These keys represent configuration settings such as HTTP version,
     * server name, host, port, and paths to metadata, file system, and URL structures.
     */
    private static final String HTTP_VERSION = "sv.version"; // Http version.
    private static final String SERVER_NAME = "sv.name"; // Server name.
    private static final String HOST = "sv.host"; // Server network host (localhost).
    private static final String PORT = "sv.port"; // Server port number.

    private static final String METADATA = "path.files.metadata"; // url path to metadata file "filesMetadata.json".
    private static final String FILE_SYSTEM = "path.files.system"; // url path to the server file system.
    private static final String URL_STRUCTURES = "path.files.structure.urls"; // url path to the valid url patterns.

    private static final String METADATA_ID_FIELD_KEY = "metadata.field.id"; // Key-value "currentId" from the file "filesMetadata.json". Used for accessing the last created file id, when starting the server.
    private static final String METADATA_DATA_FIELD_KEY = "metadata.field.data"; // Key-value "data" from the file "filesMetadata.json". Used for accessing the metadata of the files in the file system.

    private final String httpVersion;
    private final String serverName;
    private final String host;
    private final int port;
    private final String metadataPath;
    private final String fileSystemPath;
    private final String urlStructuresPath;
    private final String metadataCurrentIdFieldKey;
    private final String metadataDataFieldKey;

    /**
     * Eager initialization. Creates a singleton instance of ServerProperties by retrieving all the server properties
     * and storing them as separate properties.
     */
    private static final ServerProperties INSTANCE = new ServerProperties(
            PropertiesLoader.getProperty(HTTP_VERSION),
            PropertiesLoader.getProperty(SERVER_NAME),
            PropertiesLoader.getProperty(HOST),
            PropertiesLoader.getProperty(PORT),
            PropertiesLoader.getProperty(METADATA),
            PropertiesLoader.getProperty(FILE_SYSTEM),
            PropertiesLoader.getProperty(URL_STRUCTURES),
            PropertiesLoader.getProperty(METADATA_ID_FIELD_KEY),
            PropertiesLoader.getProperty(METADATA_DATA_FIELD_KEY));

    /**
     * Private constructor to initialize the server properties object with the necessary values, parsed from the
     * configuration file.
     *
     * @param httpVersion the HTTP version.
     * @param serverName the name of the server.
     * @param host the network host address.
     * @param port the port number the server listens on.
     * @param metadataPath the URL path to the metadata file.
     * @param fileSystemPath the URL path to the file system.
     * @param urlStructuresPath the URL path to the file structure.
     * @param currentId the key used to access the current file ID in the metadata file.
     * @param fileData the key used to access file metadata in the metadata file.
     */
    private ServerProperties(String httpVersion, String serverName, String host, String port, String metadataPath,
                             String fileSystemPath, String urlStructuresPath, String currentId, String fileData) {
        this.httpVersion = httpVersion;
        this.serverName = serverName;
        this.host = host;
        this.port = Integer.parseInt(port);
        this.metadataPath = metadataPath;
        this.fileSystemPath = fileSystemPath;
        this.urlStructuresPath = urlStructuresPath;
        this.metadataCurrentIdFieldKey = currentId;
        this.metadataDataFieldKey = fileData;
    }

    /**
     * Retrieves a Singleton instance of ServerProperties.
     */
    static ServerProperties getInstance() {
        return INSTANCE;
    }

    /**
     *
     * Returns the http version this server is using.
     */
    public String getHttpVersion() {
        return httpVersion;
    }

    /**
     *
     * Returns the name of the server.
     */
    public String getServerName() {
        return this.serverName;
    }

    /**
     *
     * Returns the name of the host the server is running on.
     */
    public String getHost() {
        return host;
    }

    /**
     *
     * Returns the port number of the server.
     */
    public int getPort() {
        return port;
    }

    /**
     *
     * Returns the URL of the server file system 'files/'.
     */
    public String getFileSystemPath() {
        return fileSystemPath;
    }

    /**
     *
     * Returns the url of the file "files_metadata.json".
     */
    public String getFileMetadataPath() {
        return metadataPath;
    }

    /**
     *
     * Returns the path to the url structures file 'url_structures.json'.
     */
    public String getUrlStructuresPath() {
        return urlStructuresPath;
    }

    /**
     *
     * Returns the field value for the key 'currentId' in the file 'files_metadata.json'.
     */
    public String getMetadataCurrentIdFieldKey() {
        return metadataCurrentIdFieldKey;
    }

    /**
     *
     * Returns the field value for the key 'data' in file 'files_metadata.json'.
     */
    public String getMetadataDataFieldKey() {
        return metadataDataFieldKey;
    }
}

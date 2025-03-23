# Server file system

The `resources/files/` directory serves as the servers file system, simulating a cloud-based storage environment. When a file is uploaded, the server assigns it a unique ID and stores its metadata in `files_metadata.json`.
To ensure data consistency, metadata is only serialized when the server is properly shut down. If the server is closed improperly (e.g., forcefully terminated), metadata may not be saved, resulting in inconsistencies between stored files and their records.

**Loading file system and metadata storage**

When the server starts, the file system path is loaded into memory, serving as the base directory for all file operations. This ensures that all file-related requests are directed to a predefined location. 
The metadata storage file, `files_metadata.json`, is loaded into memory as a `FileMetadataTracker` instance. This object provides access to file-related metadata, and also the ability to add, update and remove data. 
Any updates made to the local file system, such as adding, modifying, or deleting files, are immediately reflected in the `FileMetadataTracker` instance.
Upon server shutdown, the metadata stored in `FileMetadataTracker` is serialized back into `files_metadata.json`, ensuring that all changes are preserved for future sessions.
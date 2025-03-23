# Server configuration files

When the server starts, it loads several configuration files that define its behavior, structure, and file handling rules.

1. `application.properties`: defines key configuration settings that control the servers behavior, network details, and file system paths. 

    **Server settings**

    Properties that define basic server metadata and network configurations:
    - `sv.version=HTTP/1.1` -> Specifies the supported HTTP version.
    - `sv.name=Custom Java Server` -> Defines the server name.
    - `sv.host=localhost` -> Sets the hostname or IP address where the server runs.
    - `sv.port=8080` -> Specifies the port number the server listens on for incoming connections.

    **File system paths**
    
    Properties that define the loacation for storing files, metadata and predefined URL structures:
    - `path.files.system=src/main/resources/files` -> Specifies the root directory where uploaded files are stored.
    - `path.files.metadata=src/main/resources/files_metadata.json` -> Stores metadata about uploaded files.
    - `path.files.structure.urls=src/main/resources/url_structures.json` -> Defines URL mappings or structures used by the server.

    **Metadata access keys**
    
    Properties that define specific field keys for access to JSON data:
    - `metadata.field.id=currentId` -> Represents the field name that holds the current file ID in the metadata JSON.
    - `metadata.field.data=data` -> Defines the field name where file-related data is stored in the metadata JSON.


2. `files_metadata.json`: A file used to keep track of stored files and their metadata. It acts as a lightweigth database for managing file details.

    **Structure breakdown**
    
   Each file entry is stored as an object under its corresponding file ID:

    ```json
        {
          "currentId" : 2,
          "data" : {
              "1" : {
              "fileId" : 1,
              "fileName" : "text-one.txt",
              "fileType" : "<TXT>",
              "fileSize" : "0 kb (26 bytes)",
              "timeCreated" : "20.03.2025 08:23",
              "timeUpdated" : "20.03.2025 08:23"
              },
              "2" : {
              "fileId" : 2,
              "fileName" : "text-two.txt",
              "fileType" : "<TXT>",
              "fileSize" : "0 kb (26 bytes)",
              "timeCreated" : "20.03.2025 09:20",
              "timeUpdated" : "20.03.2025 09:20"
              }
           }
        }
    ```
    The JSON structure consists of two root fields:
    - `currentId` -> Keeps track of the latest assigned file id. When a new file is uploaded, this value is incremented, and given to that file. This helps prevent ID collisions and ensures unique file identifiers.
    - `data` -> Keeps mapping of file IDs to their metadata. Each file is stored under a unique ID as a key-value pair. 
    - Read more about how the server manages data [here](https://github.com/surfaceUsed/http-file-server/blob/main/Documents/File-system.md).


3. `url_structures.json`: The file defines the supported API endpoint structures for handling file-related requests in the server. It maps server endpoints to supported HTTP methods (`GET`, `PUT`, `POST`, `DELETE`) and their corresponding URL patterns. This enables the server to correctly process and route incoming client requests. 
     
    **Structure breakdown**
    
    The JSON file contains a JSON object with the key `/files`, which represents the primary endpoint that manages the file system. Each HTTP method has an array of generic URL patterns that the server accepts. This allows the server to handle operations based on file name, ID, query parameters, and actions.

    Read more about the URLs function and structure [here](https://github.com/surfaceUsed/http-file-server/blob/main/Documents/Urls-explained.md).
     
    **URL explained**
    
    - Each URL pattern follows a generic structure, incorporating placeholders enclosed in curly brackets (`{}`) that represents dynamic
    values provided by the client.
    - Read more about the URL dynamic pattern matching [here](https://github.com/surfaceUsed/http-file-server/blob/main/Documents/Dynamic-url-pattern-matching.md).
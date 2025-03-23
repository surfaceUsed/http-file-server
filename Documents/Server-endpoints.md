# Server endpoints

The server currently exposes a single endpoint: `/files`. This endpoint serves as the entry point for interacting with 
the file system and handles all file management operations. 

The `/files` endpoint is tied to a root directory that facilitates the file operations. It maps specific HTTP requests 
to corresponding file actions, allowing clients to interact with the server's file system. This functionality is managed 
through a set of HTTP methods (`GET`, `POST`, `PUT`, `DELETE`), each of which performs a different operation on the files stored on the server.

**How It Works**

The `UrlRootDirectory` enum defines the servers root directories, and each root directory is associated with a set of 
handlers for different HTTP methods.

Each HTTP method is mapped to a specific `Command` that is responsible for executing the corresponding file operation:

- `GET`: Fetches file data.
- `POST`: Uploads a new file to the server.
- `PUT`: Updates an existing file.
- `DELETE`: Removes a file from the server.

These handlers are executed through the `FileService`, which is responsible for interacting with the file system, making 
the server capable of handling file operations efficiently.

**Future Updates**

In the future, additional endpoints may be added to support other functionalities or service areas. The current setup 
allows easy extensibility, enabling more root directories and associated file operations to be incorporated seamlessly.

**Key Points**

- The server exposes a single endpoint: `/files` for all file-related operations.
- It supports `GET`, `POST`, `PUT`, and `DELETE` HTTP methods for managing files.
- The functionality is controlled by the `UrlRootDirectory` enum and executed through corresponding `Command` handlers.
- The file operations are managed by the `FileService` class, which acts as the intermediary between the server and the file system.
- Future updates may introduce more endpoints to expand the servers capabilities.
- This structure ensures that the server can efficiently manage files while keeping the endpoint logic organized and easy to maintain.
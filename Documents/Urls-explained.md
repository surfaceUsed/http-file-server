# URLs explained

The URLs in this server are designed to define a specific path and optional query parameters to determine the action to be performed on the
servers file system. The server supports several actions like file retrieval, metadata view, file upload, file overriding, and deletion.

**File retrieval**

Retrieve a file by name:
```bash
/files/name/file.txt?action=download
```

- This request retrieves the file identified by `file.txt` from the `/files` endpoint.
- The `action=download` query parameter signals that the server should serve the file for download.

Retrieve a file by ID:
```bash
/files/id/1?action=download
```

- This request retrieves the file with ID `1` from the `/files` endpoint.
- Similar to the name-based retrieval, the `action=download` query indicates the file should be served for download.

**View file metadata**

View metadata by file name:
```bash
/files/name/file.txt?action=view
```

- This request retrieves the metadata of the file `file.txt` located at the `/files` endpoint.
- The metadata include details such as file name, id, size, type, creation date, or last modified timestamp.

View metadata by file ID:
```bash
/files/id/1?action=view
```

- This request retrieves the metadata of the file with ID `1` from the `/files` endpoint.

Search for files and retrieve metadata based on a query:
```bash
/files/query/query-string?action=view
```

- This request retrieves a list of file metadata that matches the `query-string`.
- File queries are matching based on file name and IDs.

**File Upload**

```bash
/files/upload
```

- This request allows clients to upload a new file to the server.
- The file data should be contained within the HTTP request body. The request should specify the files content type and the file itself.

**Override a file**

Override a file by name:
```bash
/files/name/file.txt?action=override
```

- This request overrides the file identified by `file.txt` in the `/files` endpoint.
- The new file data should be contained within the request body.

Override a file by ID:
```bash
/files/id/1?action=override
```

- This request overrides the file with ID `1` in the `/files` endpoint.
- The file's new content is sent as part of the HTTP request body

**Update file name**

Update the file name by name:
```bash
/files/name/file.txt?action=update-name&value=new-name.txt
```

- This request updates the name of the file `file.txt` to `new-name.txt` in the `/files` endpoint.
- The `value` query parameter specifies the new name for the file.

Update the file name by ID:
```bash
/files/id/1?action=update-name&value=new-name.txt
```

- This request updates the name of the file with ID `1` to `new-name.txt` in the `/files` endpoint.
- Similar to the name-based update, the new name is passed through the value query parameter.

**Delete file**

Delete a file by name:
```bash
/files/name/file.txt
```

- This request deletes the file `file.txt` from the `/files` endpoint.
- No query parameters are needed for deletion. Simply use the name of the file to identify it.

Delete a file by ID:
```bash
/files/id/1
```

- This request deletes the file with ID `1` from the `/files` endpoint.
- Similar to the name-based deletion, the file is identified using its ID.

**General Notes**

- Query Parameters: The `action` query parameter determines the operation (e.g., `download`, `view`, `override`, etc.) that will be
performed on the file.

- File Identification: Files can be identified by either their name or their ID. Both options are available depending on the use case.

- Upload and Override: For uploading or overriding a file, the file content is included in the body of the request (often as multipart data
for files).
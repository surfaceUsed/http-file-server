# Handling `GET` requests

A `GET` request can serve two primary functions: downloading a file or viewing file metadata.

**Downloading a file**

The `action` parameter of the URL query needs to be set to `"download"`.

To download a file from the server, the client can specify the search by either its name or ID:

Searching by file name.
```bash
GET /files/name/file.txt?action=download HTTP/1.1
Host: www.localhost:8080
Accept: */*
Connection: close
```

Searching by file ID.
```bash
GET /files/id/1?action=download HTTP/1.1
Host: www.localhost:8080
Accept: */*
Connection: close
```
In both cases, the client is requesting a specific file from the `files/` endpoint.

A successfull response would look like this.
```bash
HTTP/1.1 200 OK
Server: Custom Java Server
Connection: close
Content-Type: application/octet-stream
Content-Length: 1024
   
<file data>
```

**Important response settings**

- The server always sets the `Content-Type` to `application/octet-stream` when returning a file.
- The `Content-Length` header specifies the size of the file in bytes.
- The response body contains the file data itself.


**View file metadata** 

The `action` parameter of the URL query needs to be set to `"view"`. 

This request retrieves metadata for a file, such as its ID, name, type, size, and timestamps. The client can search for the file by its name, ID or by a search query that returns a list of file data that matches the search query:

Searching by file name.
```bash
GET /files/name/file.txt?action=view HTTP/1.1
Host: www.localhost:8080
Accept: */*
Connection: close
```

Searching by ID.
```bash
GET /files/id/1?action=view HTTP/1.1
Host: www.localhost:8080
Accept: */*
Connection: close
```

A successful response will look like this.
```bash
HTTP/1.1 200 OK
Server: Custom Java Server
Connection: close
Content-Type: application/json
Content-Length: 190
   
[
    {
        "fileId": 1,
        "fileName": "file.txt",
        "fileType": "<TXT>",
        "fileSize": "0 kb (26 bytes)",
        "timeCreated": "20.03.2025 08:23",
        "timeUpdated": "20.03.2025 08:23"
    }
]
```

If the client wants to search for a set of matching files, then they can do that by searching for a search query that matches the files by name and/or ID, and returns all matching file metadata.
 ```bash
GET /files/query/f?action=view HTTP/1.1
Host: www.localhost:8080
Accept: */*
Connection: close
```
   
Successful response.
```bash
HTTP/1.1 200 OK
Server: Custom Java Server
Connection: close
Content-Type: application/json
Content-Length: 384
   
[
    {
        "fileId": 1,
        "fileName": "file.txt",
        "fileType": "<TXT>",
        "fileSize": "0 kb (26 bytes)",
        "timeCreated": "20.03.2025 08:23",
        "timeUpdated": "20.03.2025 08:23"
    },
    {
        "fileId": 2,
        "fileName": "fileTwo.txt",
        "fileType": "<TXT>",
        "fileSize": "0 kb (26 bytes)",
        "timeCreated": "20.03.2025 09:20",
        "timeUpdated": "20.03.2025 09:20"
    }
]
```

The server can respond either by `application/json` or `text/plain`, depending on the `Accept` header of the request. If it's set to default (`*/*`), the response will be sent in JSON format.

---
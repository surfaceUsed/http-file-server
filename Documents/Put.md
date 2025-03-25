# Handling `PUT` requests

The `PUT` request serves two primary functions: overriding an existing file or changing the name of a file in the file system.

**Override a file**

The `action` parameter of the URL query needs to be set to `"override"`. 

To override a file in the server, the client can specify the search by either its name or ID:

Search by file name.
```bash
PUT /files/name/file.txt?action=override HTTP/1.1
Host: www.localhost:8080
Accept: */*
Connection: close
Content-Type: application/octet-stream
Content-length: 19

<file data>
```

Search by file ID.
```bash
PUT /files/id/1?action=override HTTP/1.1
Host: www.localhost:8080
Accept: */*
Connection: close
Content-Type: application/octet-stream
Content-length: 19

<file data>
```

Success response.
```bash
HTTP/1.1 200 OK
Server: Custom Java Server
Connection: close
Content-Type: application/json
Content-Length: 110

{
    "status": 200,
    "message": "Override successful",
    "info": "The file 'file.txt' was overridden"
}
```

The override file has to be of the same data type as the previous one, and only updates the content of the file.

**Important Request Headers**

For the server to handle the request properly, ensure the following request headers are set correctly:

- `Content-Type`: Must be set to `application/octet-stream` to indicate binary data.
- `Content-Length`: The size of the file in bytes.


**Update file name**

The `action` parameter of the URL query needs to be set to `"update-name"`, and the `value` parameter should contain the new file name.

To update a file name, the client can specify the file by either its name or ID:

Search by file name.
```bash 
PUT /files/name/file.txt?action=update-name&value=new-name.txt HTTP/1.1
Host: www.localhost:8080
Accept: */*
Connection: close
```

Search by file ID.
```bash 
PUT /files/id/1?action=update-name&value=new-name.txt HTTP/1.1
Host: www.localhost:8080
Accept: */*
Connection: close
```

Success response.
```bash
HTTP/1.1 200 OK
Server: Custom Java Server
Connection: close
Content-Type: application/json
Content-Length: 109

{
    "status": 200,
    "message": "File updated successfully",
    "info": "New file name: new-name.txt"
}
```

The new file name has to have the same file extension as the previous file:
   - If the previous file name was `file.txt` and the new name is `new-name` it needs to be followed by the `.txt` extension. 

The server can respond either by `application/json` or `text/plain`, depending on the `Accept` header of the request. If it's set to default (`*/*`), the response will be sent in JSON format.

---
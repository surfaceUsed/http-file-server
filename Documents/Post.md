# Handling `POST` requests

A `POST` request attempts to uploads a file to the server. 

Uploading a file to the server.
```bash
POST /files/upload HTTP/1.1
Host: www.localhost:8080
Accept: */*
Connection: close
Content-Disposition: attachment; filename="text.txt"
Content-Type: application/octet-stream
Content-length: 26

<file data>
```

Success response:
```bash
HTTP/1.1 201 Created
Server: Custom Java Server
Connection: close
Content-Type: application/json
Content-Length: 128

{
    "status": 201,
    "message": "File saved on the server",
    "info": "'text.txt' was given a unique identifier #3"
}
```

The server can respond either by `application/json` or `text/plain`, depending on the `Accept` header of the request. If it's set to default (`*/*`), the response will be sent in JSON format.

**Important Request Headers**

For the server to read the file content, the following request headers must be set correctly:

- `Content-Type`: Must be set to `application/octet-stream` to indicate binary data.
- `Content-Disposition`: Specifies the file attachment and filename.
- `Content-Length`: The size of the file in bytes.

---
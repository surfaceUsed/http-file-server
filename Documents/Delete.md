# Handling `DELETE` requests

The `DELETE` request attempts to remove a file from the server file system. The client can request to remove the file either by its name or ID:

Search file by name.
```bash
DELETE /files/name/text.txt
Host: www.localhost:8080
Accept: */*
Connection: close
```

Search by ID.
```bash
PUT /files/id/1
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
Content-Length: 91

{
    "status": 200,
    "message": "The file was deleted successfully from the server."
}
```

The server can respond either by `application/json` or `text/plain`, depending on the `Accept` header of the request. If it's set to default (`*/*`), the response will be sent in JSON format.

--- 

# Http requests and responses

**Request parsing and handling**

The server handles HTTP requests by parsing the incoming data to determine the requested action. The parsing ensures the server identifies the correct operation, validates the request, and processes the relevant data. The process is broken down into the following steps:

1. Reading and parsing the HTTP request: 
   - The server reads the raw HTTP request and breaks it into three key components: **request line**, **headers** and **request body**.
   - These components are parsed from the input stream and stored in an `HttpRequest` object for easy access and further processing.
   - The request is validated at each stage to ensure proper format, and errors are flagged if any component deviates from the expected structure.

2. Request components:
   - The **request line** contains the HTTP method, requested URL and the HTTP version used to make the request.
   - The **headers** provide additional metadata about the request, and is used to interpret the request. 
   - The **request body** contains specific data, such as a file, which is sent by the client. This component is processed only when the `Content-Length` header is present, as it indicates the size of the body content to be read.

   Request format:
   ```plaintext
    method request-target http-version CRLF
    header-field CRLF
    CRLF
    message-body
   ```
   
3. Routing the request:
   - Once the necessary data has been parsed and validated, the server routs the request to the appropriate handler class based on the HTTP method (e.g view, download, override). 
   - The server uses the data in the `HttpRequest` object to perform the corresponding operation (e.g., retrieving a file, uploading a file, or modifying a file).

4. Error handling:
   - If any issue is encountered during the parsing of the request or performing the request operation, the server generates an appropriate error response and sends it back to the client. 

**Response parsing and handling**

Responses are generated only after the request has been fully parsed and processed, or an error has occured.

1. Creating a response:
   - The response reflects the result of the request.
   - The resulting response is broken down into three components: **status line**, **headers** and **response body**.

2. Response components:
   - The **status line** contains the http-version, status code and a status message, and indicates the result of the request.
   - The **headers** provide metadata about the response. 
   - The **response body** contains the actual content returned by the server. This can include file data or a message with a detailed explanation of the result.

   Response format:
   ```plaintext
    http-version status-code status-message CRLF
    header-field CRLF
    CRLF
    message-body
   ```

3. Sending the response:
   - Once the response is constructed, it is sent back to the client as a byte stream.
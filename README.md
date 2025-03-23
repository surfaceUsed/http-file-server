# Http file server

### Project Description

This project started as an exploration into HTTP and HTTP protocols, which I initially found challenging
to understand. My goal was to create a project where I could work hands-on with HTTP, so I could gain a
deeper understanding of how communication over the internet works. Along the way, I also wanted to
incorporate configuration files and simulate real-world server behavior.

This project is a mock file server that simulates the behavior of a REST-based cloud file server. It
handles HTTP requests like `GET`, `PUT`, `POST`, and `DELETE` and performs operations such as uploading, retrieving,
updating, and deleting files from the server.

The server works using Java sockets to handle client-server communication, which is a great way to simulate
low-level networking and HTTP handling without relying on high-level frameworks like Spring. The project also
includes configuration files to manage server settings such as port, file paths, and other server behaviors.

The goal of this project is to replicate the functionality of a cloud file server, while also providing a
clear understanding of how HTTP requests are processed, how data is transferred, and how to handle file
operations in a server environment.

---

### Features

- **Socket Communication:** Uses Java's Socket API to handle client-server communication, simulating the basics of an HTTP server.

- **Thread safety:** Ensures safe concurrent access to shared resources using synchronization, preventing race conditions and data inconsistencies.

- **File Management:** The server stores files in a local directory and handles metadata associated with each file.

- **Configuration Management:** Server settings like port number, file paths, and directory management are stored in 
configuration files for easy adjustments.

- **Http request parsing and response creation**: The server parses incoming HTTP requests, processes it, and generates 
a corresponding HTTP response with the relevant status, headers, and body content.

- **Dynamic URL pattern matching:** The server uses dynamic URL pattern matching to handle various HTTP requests efficiently.

- **Servlet routing:** Utilizes `HttpServlet` instance to route requests to specific handlers based on their URL endpoint 
(the root directory path) and HTTP method.

- **HTTP Methods:** The server supports basic HTTP methods for file management:
    - **GET:** Retrieve data from the server.
    - **POST:** Upload a file to the server.
    - **PUT:** Update an existing file on the server.
    - **DELETE:** Delete a file from the server.

- **Logging:** Includes logging for server actions such as connections, file operations, and errors, providing insights 
into the server's behavior.

- **Error handling:** All errors are logged and documented. If an issue occurs while processing a request, the server 
generates an appropriate response and sends it back to the client, ensuring proper error communication.

---

### Dependencies

The project includes the following dependencies:

**Jackson Core and Databind**

- `jackson-core` (version 2.17.2): Provides the foundational features for JSON parsing and generating in Java. It enables 
the ability to read and write JSON data in various formats (e.g., reading a JSON string into Java objects and vice versa).

- `jackson-databind` (version 2.17.2): Works alongside Jackson Core to handle the data binding, i.e., converting between 
Java objects and JSON. This dependency is essential for serialization and deserialization operations in the project.

**Logging**

- `slf4j-api` (version 2.0.16): The Simple Logging Facade for Java (SLF4J) is a common logging abstraction. This library 
allows various logging frameworks to be used interchangeably in the project, providing a consistent logging interface.

- `logback-classic` (version 1.5.17): Logback is the logging framework used in this project. It integrates with SLF4J to 
provide logging functionality. This dependency ensures logging of various events such as request processing, errors, and 
other relevant system activities.

These dependencies enable JSON processing and robust logging for the server, which are fundamental for handling HTTP 
requests and managing server activities effectively.

---

### Server behaviour and rules

**General Overview**

The server is a socket-based application that supports multiple clients simultaneously. It implements the `HTTP/1.1` protocol, 
allowing clients to send requests and receive responses. It runs on a predefined host and port `localhost:8080` (this can
be changed by updating the settings in `application.properties`).
Currently, the server is configured to handle requests directed to the `/files` endpoint, which serves as the entry point 
for interacting with the file system. This endpoint allows for operations like uploading, retrieving, and deleting files and metadata.

Each HTTP method follows a distinct set of rules, supporting specific request content types and predefined response formats. 
If an unsupported content type is used, the server returns an error response in JSON format (`application/json`) as a default response type.

When the server starts, it loads a set of configuration files, which contain essential settings for the application. The 
data from these files is then loaded into memory and used to define global server properties, which are accessible through 
the `ConfigurationManager`. If this process fails, the server cannot start.

Each incoming client connection is handled as an individual session, ensuring that requests are processed separately. The 
session remains active until:

- The client explicitly sets the `Connection` header to `close`.
- An exception occurs during the client session, and closes the `Socket`.

A `HttpServlet` is used to route the request to it's correct handler class, making sure that specific requests are handled 
by specific handlers.

After processing the request, a response is created reflecting the result of the request.

**Server administration**

The server is controlled using a command window (GUI). The server administrator starts and manages the server from here. 
Below is a list of administrator commands:
- `.start` – Starts the server.
- `.restart` – Gracefully shuts down the server, closes all active connections, and restarts it.
- `.shutdown` – Properly shuts down the server and ensures all resources are released.
- `.status` – Displays the server status, including:
  - Host
  - Port number
  - Number of connected clients
  - Number of active threads
- `.connections` – Lists all active client IP addresses.
- `.log` – Displays logs generated by the server. The administrator can choose a specific log type:
  - `--info` – Shows informational logs.
  - `--error` – Shows error logs.
  - `--warn` – Shows warning logs.
  - If no type is specified, all logs will be displayed.
- `.clear` – Clears the command window.
- `.help` – Displays a list of all administrator commands.
- `.end` – Ends the application.
  - The server must be shut down before the application can exit.
  - Saves all relevant data to files.
  - If `--save` is specified (`.end --save`), all logs will be saved to the default file `logs.txt`.

--- 

### Sections

- [Project structure]()
- [Configuration files]()
- [Server file system]()
- [Request and response handling]()
- [URL structure]()
- [Dynamic URL pattern matching]()
- [Server endpoints]()
- [Servlet routing]()
- Http methods: [GET](), [PUT](), [POST](), [DELETE]()
- [Installing the application]()
- [Running the application]()

---
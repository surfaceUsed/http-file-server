# Project structure

```plaintext
http-file-server/
│
└── src/main/java/org/example/
|   ├── command/
|   │   ├── files/
|   │   │   ├── handlers/
|   │   │   |   ├── BaseHandler.java
|   │   │   |   ├── DeleteHandler.java
|   │   │   |   ├── DownloadHandler.java
|   │   │   |   ├── OverrideHandler.java
|   │   │   |   ├── UpdateHandler.java
|   │   │   |   ├── UploadHandler.java
|   │   │   |   └── ViewHandler.java
|   |   |   |
|   │   │   ├── DELETE.java
|   │   │   ├── GET.java
|   │   │   ├── POST.java
|   │   │   └── PUT.java
|   |   |
|   │   ├── Command.java
|   │   ├── ContentTypeValidator.java
|   │   ├── RequestHandlerInitializer.java
|   │
|   ├── config/
|   │   ├── ConfigurationManager.java
|   |   ├── FileMetadataTracker.java
|   │   ├── FileSystem.java
|   │   ├── JsonDataLoader.java
|   │   ├── LocalFileSystem.java
|   │   ├── PropertiesLoader.java
|   │   └── ServerProperties.java
|   │
|   ├── core/
|   │   ├── Server.java
|   │   ├── ServerAdministrator.java
|   │   └── Session.java
|   │
|   ├── enums/
|   │   ├── AdministratorCommands.java
|   │   ├── ConnectionStatus.java
|   │   ├── ContentType.java
|   │   ├── DataType.java
|   │   ├── Header.java
|   │   ├── HttpMethod.java
|   │   ├── HttpResponseStatus.java
|   │   ├── LoggerType.java
|   │   ├── RequestHandlerAction.java
|   │   ├── UrlParameters.java
|   │   └── UrlRootDirectory.java
|   │
|   ├── error/
|   │   ├── ErrorMessageHandler.java
|   │   ├── FileRollbackException.java
|   │   ├── FileSystemException.java
|   │   ├── HttpRequestParserException.java
|   │   ├── HttpRequestURLException.java
|   │   ├── HttpResponseParserException.java
|   │   └── ServerConfigurationException.java
|   │
|   ├── gui/
|   │   └── CommandWindow.java
|   │
|   ├── http/
|   │   ├── request/
|   │   │   ├── HttpRequest.java
|   │   │   └── HttpRequestParser.java
|   |   |
|   │   ├── response/
|   │       ├── HttpResponse.java
|   │       ├── HttpResponseInitializer.java
|   │       └── ResponseHandler.java
|   │
|   ├── logs/
|   │   ├── LogData.java
|   │   └── LogHandler.java
|   │
|   ├── model/
|   │   ├── FileDetails.java
|   │   ├── FileIdentifier.java
|   │   ├── HttpMessage.java
|   │   ├── Identifier.java
|   │   ├── UrlRouter.java
|   │   └── UrlStructureMatcher.java
|   │
|   ├── Service/
|   │   ├── FileService.java
|   │   └── ServerCloudStorage.java
|   │
|   ├── Servlet/
|   │   ├── FileServlet.java
|   │   ├── HttpServlet.java
|   │   └── ServletDispatcher.java
|   │
|   ├── util/
|   │   ├── FileUtil.java
|   │   └── Jsonutil.java
|   │
|   ├── ServerApplicationRunner.java
|   │
|   └── resources/
|       ├── files/
|       │   ├── file-one.txt
|       │   └── file-two.txt
|       |      
|       ├── application.properties
|       ├── files_metadata.json
|       └── url_structures.json
│
└── pom.xml
```

**`command/`**
- This package handles the core logic for processing various HTTP requests and their corresponding actions. 
- It contains HTTP request handlers for file operations, and `Command` classes for different HTTP methods, encapsulating the logic for how each method is processed.

**`config/`**
- Contains configuration management classes that handles server settings, file system, setup, and property loading. 
- These classes ensure that the server can be configured properly through external properties and settings files. 

**`core/`**
- This package holds the core server functionality, including classes that contol the server operations, manage server sessions, and perform administartive tasks (the controller). 
- It's the backbone of the server application, and oversees how it starts, runs and manages user sessions. 

**`enums/`**
- Contains all the enumeration types used throughout the application. 
- They provide constant values that represent key concepts like HTTP methods, response statuses, connection statuses etc.
- They help standardize the logic in the system.

**`error/`**
- Manages custom exceptions and error-handling logic. 
- It contains classes that define specific errors that may arise during the server's operation, like issues with HTTP requests or problems with the file system.

**`gui/`**
- The package contains the application GUI that the server administartor uses to interact with the server. 
- The GUI acts as a command window where the administartor can issue commands, monitor server activities and status. 

**`http/`**
- Contains classes that handle HTTP request and responses. 
- `request/` -> deals with parsing and processing incoming HTTP requests.
- `response/` -> manages the creation and sending og HTTP responses back to the client. 

**`logs/`**
- Manages server logging and logging-related functionality. 
- This package ensures that important server activities, errors, and requests are logged for auditing or debugging purposes.

**`model/`**
- Defines the data model used within the application. 
- These models represents various entities like files, identifiers and URL routing structures.
- They enable the server to manage and process data efficiently. 

**`service/`**
- The package contains the applications business logic, and handles specific tasks within the server.
- The classes are responsible for performing operations on files, and storage within the server file system. 

**`servlet/`**
- The package implements server-like functionality to handle HTTP requests. 
- The classes provides ways to retrieve the correct serlvet instance for the requests, process it, and generate an appropriate
HTTP response. 

**`util/`**
- Contains utility classes that provide helper functions throughout the application. 

**`ServerApplicationRunner.java`**
- The entry point of the application, initializing and starting the server. 

**`resources/`**
- This directory contains static resources used by the server, such as configuration files, metadata, and the server "cloud storage" 
(`files/`), which acts as the applications file system. 
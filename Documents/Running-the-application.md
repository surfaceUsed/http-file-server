# Running the application

**Software requirements**

1. Java 20.

2. [Apache Maven](https://maven.apache.org/).

3. Software to test the application:
   - [cURL](https://curl.se/)
   - [Postman](https://www.postman.com/downloads/)

**Installation**

1. Clone the repository.
```bash
git clone https://github.com/surfaceUsed/http-file-server.git
```

2. Navigate the project directory.
```bash
cd path/to/http-file-server
```

3. Compile the application.
```bash
mvn clean compile
```

4. Package the application
```bash
mvn package
```

5. Run the application
```bash
java -jar target/http-file-server-1.0.jar
```
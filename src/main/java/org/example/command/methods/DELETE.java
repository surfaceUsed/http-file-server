package org.example.command.methods;

import org.example.command.Command;
import org.example.command.methods.handlers.BaseHandler;
import org.example.enums.RequestHandlerAction;
import org.example.error.FileSystemException;
import org.example.error.HttpRequestParserException;
import org.example.error.HttpRequestURLException;
import org.example.error.HttpResponseParserException;
import org.example.http.request.HttpRequest;
import org.example.http.response.HttpResponse;
import org.example.http.response.ResponseHandler;
import org.example.service.FileService;

/**
 * The DELETE class implements the Command interface and represents a command for handling HTTP DELETE requests related
 * to file operations. It uses the FileService to process file-related operations and delegates the request handling to
 * a specific handler based on the request type. The specific handler is obtained from the BaseHandler class and
 * executed to perform the delete operation.
 */
public class DELETE implements Command {

    private final FileService service;

    /**
     * Constructor that initializes the DELETE command with the provided FileService instance.
     *
     * @param service the FileService to be used for file operations
     */
    public DELETE(FileService service) {
        this.service = service;
    }

    /**
     * Executes the DELETE command. It retrieves the appropriate handler based on the request type action (DELETE) and
     * invokes the handler to perform the delete operation. If an exception occurs during the execution, it catches the
     * error and sends a response with the error message to the client.
     *
     * @param request the HTTP request that contains details of the client request
     * @param response the HTTP response that will be sent back to the client
     */
    @Override
    public void execute(HttpRequest request, HttpResponse response) {

        try {

            BaseHandler.getHandler(RequestHandlerAction.DELETE, request, response, this.service).run();

        } catch (HttpResponseParserException | HttpRequestURLException | HttpRequestParserException | FileSystemException e) {
            ResponseHandler.handleResponseFromServer(response, e.getErrorMessage());
        }
    }
}

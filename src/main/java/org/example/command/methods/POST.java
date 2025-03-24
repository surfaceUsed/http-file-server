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
 * The POST class implements the Command interface and represents a command for handling HTTP POST requests related to
 * file operations. It uses the FileService to process file-related operations and delegates the request handling to a
 * specific handler based on the request type.
 *
 * The specific handler is obtained from the BaseHandler class and executed to perform the post operation.
 */
public class POST implements Command {

    private final FileService service;

    public POST(FileService service) {
        this.service = service;
    }

    /**
     * Executes the POST command by selecting the appropriate handler for the POST action and executing it.
     * If any errors occur during the execution, it catches the exceptions and handles the response
     * with the appropriate error message.
     *
     * @param request The HttpRequest containing the details of the client request.
     * @param response The HttpResponse object to send back the response to the client.
     */
    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        try {

            String action = request.getUrlPath().substring(1); // parses the URL path '/upload'.
            RequestHandlerAction type = RequestHandlerAction.getHandlerType(action);
            BaseHandler.getHandler(type, request, response, this.service).run();

        } catch (HttpResponseParserException | HttpRequestURLException | HttpRequestParserException | FileSystemException e) {
            ResponseHandler.handleResponseFromServer(response, e.getErrorMessage());
        }
    }
}

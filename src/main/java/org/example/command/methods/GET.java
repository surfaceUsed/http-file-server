package org.example.command.methods;

import org.example.command.Command;
import org.example.command.methods.handlers.BaseHandler;
import org.example.enums.RequestHandlerAction;
import org.example.enums.UrlParameters;
import org.example.error.*;
import org.example.http.request.HttpRequest;
import org.example.http.response.HttpResponse;
import org.example.http.response.ResponseHandler;
import org.example.service.FileService;

/**
 * The GET class implements the Command interface and represents a command for handling HTTP GET requests related to
 * file operations. It uses the FileService to process file-related operations and delegates the request handling to
 * a specific handler based on the request type.
 *
 * It processes client requests by extracting the action (such as "view" or "download") from the URL query request,
 * obtains the specific handler from the BaseHandler class, and executes it.
 */
public class GET implements Command {

    private final FileService service;

    public GET(FileService service) {
        this.service = service;
    }

    /**
     * Executes the GET command by extracting the action from the query parameters of the request, determining the
     * appropriate handler type (either VIEW or DOWNLOAD), and executing the handler. If any errors occur during the
     * execution, it catches the exceptions and handles the response with the appropriate error message.
     *
     * @param request The HttpRequest containing the details of the client request.
     * @param response The HttpResponse object to send back the response to the client.
     */
    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        try {
            String action = UrlParameters.mapQueryValues(request.getUrlQuery()).get(UrlParameters.ACTION);
            RequestHandlerAction type = RequestHandlerAction.getHandlerType(action);
            BaseHandler.getHandler(type, request, response, this.service).run();

        } catch (HttpResponseParserException | HttpRequestURLException | HttpRequestParserException | FileSystemException e) {
            ResponseHandler.handleResponseFromServer(response, e.getErrorMessage());
        }
    }
}

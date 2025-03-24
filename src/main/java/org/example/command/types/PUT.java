package org.example.command.types;

import org.example.command.Command;
import org.example.command.types.handlers.BaseHandler;
import org.example.enums.RequestHandlerAction;
import org.example.enums.UrlParameters;
import org.example.error.*;
import org.example.http.request.HttpRequest;
import org.example.http.response.HttpResponse;
import org.example.http.response.ResponseHandler;
import org.example.service.FileService;

/**
 * The PUT class implements the Command interface and represents a command for handling HTTP PUT requests related to
 * file operations. It uses the FileService to process file-related operations and delegates the request handling to
 * a specific handler based on the request type.
 *
 * This class processes client requests by determining the requested action (such as "update" or "override") from the
 * URL query request, obtains the specific handler from the BaseHandler class, and executes it.
 */
public class PUT implements Command {

    private final FileService service;

    public PUT(FileService service) {
        this.service = service;
    }

    /**
     * Executes the PUT command by extracting the action parameter from the request URL (such as "update" or "override")
     * and passing the request to the appropriate handler.
     * If an error occurs, it catches the exception and processes the response with an error message.
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
package org.example.core;

import org.example.enums.ConnectionStatus;
import org.example.enums.Header;
import org.example.enums.LoggerType;
import org.example.error.HttpRequestParserException;
import org.example.http.request.HttpRequest;
import org.example.http.request.HttpRequestParser;
import org.example.http.response.HttpResponse;
import org.example.http.response.ResponseHandler;
import org.example.logs.LogHandler;
import org.example.servlet.HttpServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * Client handler class; handles client sessions in a separate thread, independent of other client connections.
 */

/**
 * Handles the client session in a separate thread, managing communication with the client via socket. The session
 * processes HTTP requests, interacts with appropriate servlets, and sends responses back to the client.
 * The class ensures that each client connection operates independently and can be managed asynchronously.
 */
public class Session implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Session.class);

    private final Socket socket;
    private final Server server;

    private ConnectionStatus sessionConnectionStatus;

    /**
     * Initializes a new client session with a given socket and server. Sets the initial connection status to OPEN and
     * registers the socket with the server.
     *
     * @param socket the socket associated with the client connection
     * @param server the server managing the connection
     */
    Session(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        this.sessionConnectionStatus = ConnectionStatus.OPEN;
        this.server.addConnection(this.socket);
    }

    /**
     * The entry point of the thread that handles the client connection. Listens for incoming client requests and
     * processes them using the `handleClientRequest` method. The loop continues while the session connection status is
     * OPEN. Upon completion or an error, the connection is gracefully shut down.
     */
    @Override
    public void run() {

        try (InputStream input = this.socket.getInputStream();
             OutputStream output = this.socket.getOutputStream()) {

            while (this.sessionConnectionStatus == ConnectionStatus.OPEN) {
                handleClientRequest(input, output);
            }

        } catch (IOException e) {
            LogHandler.handleLog(LOGGER, LoggerType.ERROR, "Client connection error: " + e.getMessage());
        } finally {
            LogHandler.handleLog(LOGGER, LoggerType.INFO, "Client disconnected from server.");
            shutDownConnection();
        }
    }

    /**
     * Processes the client's HTTP request by parsing the input stream and creating an appropriate response.
     * It initializes an `HttpRequest` object, updates the session connection status based on the request headers,
     * and dispatches the request to an appropriate servlet for further handling.
     * If an error occurs while parsing the request, an error message is sent back to the client.
     *
     * @param input the input stream from which the client request is read.
     * @param output the output stream used to send the response to the client.
     * @throws IOException if an I/O error occurs while processing the request.
     */
    private void handleClientRequest(InputStream input, OutputStream output) throws IOException {
        HttpResponse response = new HttpResponse();
        try {
            HttpRequest request = HttpRequestParser.parseRequest(input);
            updateSessionConnectionStatus(request, response);
            initServlet(request, response);
        } catch (HttpRequestParserException e) {
            ResponseHandler.handleResponseFromServer(response, e.getErrorMessage());
        } finally {
            response.sendResponse(output);
        }
    }

    /**
     * Updates the session connection status based on the "Connection" header of the client's request.
     * Sets the appropriate connection status (OPEN or CLOSED) for the session and updates the response accordingly.
     *
     * @param request the parsed HTTP request.
     * @param response the response object to update the connection status.
     */
    private void updateSessionConnectionStatus(HttpRequest request, HttpResponse response) {
        this.sessionConnectionStatus = ConnectionStatus.getStatus(request.getHeaders().get(Header.CONNECTION.getName()));
        response.setConnectionStatus(this.sessionConnectionStatus);
    }

    /**
     * Initializes the appropriate servlet based on the HTTP request's URL endpoint. The servlet is then used to handle
     * the request and generate the corresponding response.
     *
     * @param request the parsed HTTP request.
     * @param response the response object to be populated by the servlet.
     * @throws HttpRequestParserException if the request URL is not valid or cannot be parsed.
     */
    private void initServlet(HttpRequest request, HttpResponse response) throws HttpRequestParserException {
        HttpServlet servlet = this.server.getDispatcher().getServlet(request.getUrlRoot());
        servlet.init();
        servlet.handle(request, response);
    }

    /**
     * Closes the socket connection with the client and removes it from the server's connection pool.
     * Logs relevant information about the socket closure and any errors that occur during the shutdown process.
     */
    private void shutDownConnection() {

        try {
            if (this.socket != null) {
                this.socket.close();
                LogHandler.handleLog(LOGGER, LoggerType.INFO, this.socket + " closed.");
            }
        } catch (IOException e) {
            LogHandler.handleLog(LOGGER, LoggerType.ERROR, "Closing client socket failed: " + e.getMessage());
        } finally {
            if (this.socket != null) {
                this.server.removeConnection(this.socket);
                LogHandler.handleLog(LOGGER, LoggerType.INFO, " socket connection '" + this.socket +
                        "' removed from server.");
            }
        }
    }

    /**
     * Returns a string representation of the session, including the client's IP address and port. This is useful for
     * logging and identifying client connections.
     *
     * @return a string representing the client's connection details
     */
    @Override
    public String toString() {
        return socket.getInetAddress().getHostAddress() + ":" + this.socket.getPort();
    }
}
package org.example.core;

import org.example.config.ConfigurationManager;
import org.example.config.ServerProperties;
import org.example.enums.LoggerType;
import org.example.logs.LogHandler;
import org.example.servlet.ServletDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Server class that handles client connections, dispatches requests to appropriate servlets,
 * and manages server resources like threads and sockets.
 */
public class Server implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private static final ServerProperties PROPERTIES = ConfigurationManager.getInstance().getServerProperties();
    private static final int FIXED_NUMBER_OF_THREADS = 10; // Sets a fixed number of threads that the server can handle at a time.

    private final Set<Socket> activeConnections; // Stores active client connections.
    private final ServerSocket serverSocket;
    private final ServletDispatcher dispatcher; // Dispatcher to handle request routing to servlets.
    private final ExecutorService executor;

    Server(ServerSocket serverSocket) {
        this.activeConnections = ConcurrentHashMap.newKeySet();
        this.serverSocket = serverSocket;
        this.dispatcher = new ServletDispatcher();
        this.executor = Executors.newFixedThreadPool(FIXED_NUMBER_OF_THREADS);
    }

    /**
     * The main method of the server that runs in a separate thread.
     * It listens for client connections and delegates client sessions to the executor.
     */
    @Override
    public void run() {

        LogHandler.handleLog(LOGGER, LoggerType.INFO, "Server is running. Waiting for clients to connect..");

        while (!this.serverSocket.isClosed()) {

            try {

                Socket socket = this.serverSocket.accept();

                LogHandler.handleLog(LOGGER, LoggerType.INFO, "Client connected!");

                this.executor.submit(new Session(socket, this));

            } catch (IOException e) {
                if (this.serverSocket.isClosed()) {
                    LogHandler.handleLog(LOGGER, LoggerType.INFO, "Server shut down successfully.");
                    return;
                }
                LogHandler.handleLog(LOGGER, LoggerType.ERROR, "Server run error: " + e.getMessage());
            }
        }
    }

    /**
     * Starts the server in a new thread.
     * This method is used to run the server asynchronously so that it doesn't block other operations.
     */
    void startServer() {
        Thread serverThread = new Thread(this);
        serverThread.start();
    }

    /**
     * Retrieves the instance of the ServletDispatcher.
     * The dispatcher is responsible for routing HTTP requests to the appropriate servlets.
     */
    ServletDispatcher getDispatcher() {
        return this.dispatcher;
    }

    /**
     * Adds a client socket to the set of active connections.
     * This helps track all currently connected clients.
     *
     * @param socket The client socket to add.
     */
    void addConnection(Socket socket) {
        this.activeConnections.add(socket);
    }

    /**
     * Removes a client socket from the set of active connections.
     * This is called when a client disconnects.
     *
     * @param socket The client socket to remove.
     */
    void removeConnection(Socket socket) {
        this.activeConnections.remove(socket);
    }

    /**
     * Returns a formatted string containing the details of all active client connections.
     * It lists the client IP address and port for each connection.
     *
     * @return A string listing all active client connections.
     */
    String getActiveConnections() {
        synchronized (this.activeConnections) {
            if (this.activeConnections.isEmpty()) {
                return "No current active connections";
            }

            StringBuilder sb = new StringBuilder();
            for (Socket socket : this.activeConnections) {
                sb.append("- ")
                        .append(socket.getInetAddress().getHostAddress())
                        .append(":")
                        .append(socket.getPort())
                        .append("\n");
            }
            sb.setLength(sb.length() - 1);
            return sb.toString();
        }
    }

    /**
     * Returns the current status of the server, including whether it is running, the host and port, the number of
     * active clients, and the number of active threads.
     *
     * @return A formatted string with the server status.
     */
    String getServerStatus() {
        boolean isServerRunning = !this.serverSocket.isClosed();
        String host = PROPERTIES.getHost();
        int port = PROPERTIES.getPort();
        int clientsConnected = this.activeConnections.size(); // Number of clients connected to server.
        int threadsActive = ((ThreadPoolExecutor) this.executor).getActiveCount(); // Number of active threads handling client connections.

        return String.format(
                """
                Server status:
                - Running: %s
                - Host: %s
                - Port: %d
                - Clients connected: %d
                - Active threads: %d""",
                isServerRunning ? "true" : "false",
                host,
                port,
                clientsConnected,
                threadsActive
        );
    }

    /**
     * Shuts down the server by stopping the executor, closing all client connections, and closing the server socket.
     * It ensures that all resources are properly released before the server shuts down.
     */
    void shutDownServer() {
        LogHandler.handleLog(LOGGER, LoggerType.INFO, "Shutting down server.");
        try {
            shutDownExecutor();
            closeSocketConnections();
        } catch (Exception e) {
            LogHandler.handleLog(LOGGER, LoggerType.ERROR, "Something went wrong during shutdown: " + e.getMessage());
        } finally {
            closeServerSocket();
            this.dispatcher.destroyActiveServlets();
        }
    }

    /**
     * Gracefully shuts down the executor service, waiting for ongoing tasks to finish. If tasks don't finish in the
     * specified time, it forces a shutdown and logs any remaining tasks.
     */
    private void shutDownExecutor() {
        boolean isForcedShutdown = false;
        this.executor.shutdown();
        try {
            if (!this.executor.awaitTermination(10, TimeUnit.SECONDS)) {
                LogHandler.handleLog(LOGGER, LoggerType.WARNING, "Executor did not shut down inside of the time " +
                        "limit.. forcing shutdown now.");
                isForcedShutdown = true;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LogHandler.handleLog(LOGGER, LoggerType.WARNING, "Termination interrupted: " + e.getMessage() +
                    ". Initiating forced shutdown.");
            isForcedShutdown = true;
        } finally {
            if (isForcedShutdown) {
                List<Runnable> runningTasks = this.executor.shutdownNow();
                for (Runnable sessions : runningTasks) {
                    LogHandler.handleLog(LOGGER, LoggerType.WARNING, "Client connection '" + sessions.toString() + "' could not be forcefully shut down.");
                }
            }
        }
    }

    /**
     * Closes all active client socket connections and clears the active connections set. Logs any issues encountered
     * while closing the sockets.
     */
    private void closeSocketConnections() {
        synchronized (this.activeConnections) {
            for (Socket socket : this.activeConnections) {
                try {
                    socket.close();
                } catch (IOException e) {
                    LogHandler.handleLog(LOGGER, LoggerType.ERROR, "Failed to close socket '" + socket + ": " + e.getMessage());
                }
            }
            this.activeConnections.clear();
        }
    }

    /**
     * Closes the server socket if it is not already closed. Logs any issues encountered while closing the socket.
     */
    private void closeServerSocket() {
        try {
            if (this.serverSocket != null && !this.serverSocket.isClosed()) {
                this.serverSocket.close();
            }
        } catch (IOException e) {
            LogHandler.handleLog(LOGGER, LoggerType.ERROR, "Failed to close server socket: " + e.getMessage());
        }
    }
}
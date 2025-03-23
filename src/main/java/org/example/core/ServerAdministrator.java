package org.example.core;

import org.example.config.ConfigurationManager;
import org.example.config.ServerProperties;
import org.example.enums.AdministratorCommands;
import org.example.enums.LoggerType;
import org.example.gui.CommandWindow;
import org.example.logs.LogHandler;
import org.example.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Controller class that manages the server operations, including starting, restarting, shutting down, and displaying
 * server status. It provides a GUI-based command interface and processes administrator commands.
 *
 * This class implements the Consumer interface to handle input commands asynchronously.
 */
public class ServerAdministrator implements Consumer<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerAdministrator.class);
    private static final String DEFAULT_LOG_FILE = "logs.txt"; // Default save file for logs.
    private static final String SAVE = "--save";

    private final CommandWindow commandWindow;
    private final ServerProperties properties = ConfigurationManager.getInstance().getServerProperties();
    private final String menu = AdministratorCommands.getCommandMenu(); // List of administrator commands.
    private final AtomicBoolean isServerRunning;
    private final AtomicBoolean isPerformingAction; // Checks if a command is already being processes; if there is, then all other commands have to wait.

    private Server server;

    public ServerAdministrator() {
        this.commandWindow = new CommandWindow(this);
        this.isServerRunning = new AtomicBoolean(false);
        this.isPerformingAction = new AtomicBoolean(false);
    }

    /**
     * Initiates the command window GUI by invoking the `start` method on the `commandWindow` object. This method is
     * executed on the Swing Event Dispatch Thread to ensure the GUI components are created and interacted with in a
     * thread-safe manner. The method allows the administrator to input commands through the GUI interface.
     */
    public void run() {
        SwingUtilities.invokeLater(this.commandWindow::start);
    }

    /**
     * Processes administrator commands entered through the GUI. Parses the input, determines the appropriate action,
     * and executes it unless another action is in progress.
     *
     * @param input The command entered by the administrator.
     */
    @Override
    public void accept(String input) {
        String[] parseInput = input.split(" ", 2);
        AdministratorCommands command = AdministratorCommands.parseCommand(parseInput[0].trim());
        if (!this.isPerformingAction.get()) {
            switch (command) {

                case START -> start();

                case RESTART -> restart();

                case SHUT_DOWN -> shutdown();

                case STATUS -> status();

                case CONNECTIONS -> connections();

                case CLEAR -> clear();

                case LOG -> log(parseInput);

                case HELP -> help();

                case END -> end(parseInput);

                default -> writeMessage("'" + input + "' is not a valid command.\n");
            }
        }
    }

    /**
     * Starts the server by initializing a new instance and binding it to a port. Runs the server in a separate thread
     * to prevent blocking the UI. If the server is already running, it displays a message instead.
     */
    private void start() {
        if (!this.isServerRunning.get()) {
            try {
                server = new Server(new ServerSocket(this.properties.getPort()));
            } catch (IOException e) {
                LogHandler.handleLog(LOGGER, LoggerType.ERROR, "Unexpected error when initiating server socket object: " + e.getMessage());
                return;
            }
            this.server.startServer();
            writeMessage(startMessage());
            this.isServerRunning.set(true);
        } else {
            writeMessage("Server is already running.\n");
        }
    }

    private String startMessage() {
        return (!this.isPerformingAction.get()) ? "Server host name '" + this.properties.getHost() + "',  running on port " + this.properties.getPort() + ".\n" :
                "Server restarted successfully!\n";
    }

    /**
     * Restarts the server by first stopping the current instance and then initializing a new one. This method ensures
     * that the restart process is handled smoothly without blocking the UI.
     */
    private void restart() {
        if (this.isServerRunning.get()) {
            this.isPerformingAction.set(true);
            LogHandler.handleLog(LOGGER, LoggerType.INFO, "Admin restarting server.");
            writeMessage("Server restart initiated.. This might take a moment.\n");
            executeThread(() -> {
                server.shutDownServer(); // Shuts down server instance.
                isServerRunning.set(false);
                start(); // Starts new server instance running on same host and port.
                isPerformingAction.set(false);
            });
        } else {
            writeMessage("The server is not running.. nothing to restart.\n");
        }
    }

    /**
     * Shuts down the server gracefully (as long as server is running), ensuring all connections are closed before
     * updating the status flags. Runs in a separate thread to maintain UI responsiveness.
     */
    private void shutdown() {
        if (this.isServerRunning.get()) {
            this.isPerformingAction.set(true);
            LogHandler.handleLog(LOGGER, LoggerType.INFO, "Admin shutting down server.");
            writeMessage("Server shut down initiated.. This might take a moment.\n");
            executeThread(() -> {
                server.shutDownServer();
                writeMessage("Server shutdown complete.\n");
                isServerRunning.set(false);
                isPerformingAction.set(false);
            });
        } else {
            writeMessage("The server is not running.. nothing to shut down.\n");
        }
    }

    /**
     *
     * Prints the server status.
     */
    private void status() {
        String status = this.server.getServerStatus();
        this.commandWindow.appendMessage(status + "\n");
    }

    /**
     *
     * Prints the ip- and port number of all active connections.
     */
    private void connections() {
        String activeConnections = this.server.getActiveConnections();
        this.commandWindow.appendMessage(activeConnections + "\n");
    }

    /**
     *
     * Prints list of commands.
     */
    private void help() {
        writeMessage(this.menu + "\n");
    }

    /**
     * Retrieves and displays server logs. Logs can be filtered based on types such as INFO, WARNING, and
     * ERROR. If no type is entered, all current logs will be printed.
     */
    private void log(String[] command) {
        if (command.length == 2) {
            LoggerType type = LoggerType.getType(command[1]);
            if (type != LoggerType.INVALID) {
                String logs = LogHandler.getLogsByType(type);
                writeMessage(logs);
            } else {
                writeMessage("'" + command[1] + "' is not a valid log type command.");
            }
            return;
        }
        writeMessage(LogHandler.getAllLogs());
    }

    /**
     *
     * Clears all text from the admin window.
     */
    private void clear() {
        this.commandWindow.clearWindow();
    }

    /**
     * Handles the termination of the application. Ensures that the server is properly shut down before exiting. If logs
     * need to be saved, they are written to a file before closing the application.
     */
    private void end(String[] inputs) {
        if (this.isServerRunning.get()) {
            writeMessage("Shut down server manually before closing application.\n");
        } else {
            if (inputs.length == 2 && inputs[1].equals(SAVE)) {
                try {
                    String allLogs = LogHandler.getAllLogs();
                    FileUtil.writeTextToFile(DEFAULT_LOG_FILE, allLogs);
                    LOGGER.info("[INFO] Logs were saved to file '" + DEFAULT_LOG_FILE + "'.");
                } catch (IOException e) {
                    LOGGER.error("[ERROR] failed to write logs to file: " + e.getMessage());
                    // TODO: Maybe write to console if failed???
                }
            }
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.error("[ERROR] End delay process was interrupted: " + e.getMessage());
            }
            System.exit(0);
        }
    }

    /**
     * Executes a given operation in a separate thread to prevent blocking the main UI thread. Useful for background
     * tasks such as restarting or shutting down the server.
     *
     * @param runnable The runnable task to execute asynchronously.
     */
    private void executeThread(Runnable runnable) {
        new Thread(runnable).start();
    }

    /**
     * Writes a message to the command window. Ensures that messages are appended safely using Swing’s event dispatch
     * thread to prevent UI errors.
     *
     * @param message The message to be displayed in the command window.
     */
    private void writeMessage(String message) {
        SwingUtilities.invokeLater(() -> this.commandWindow.appendMessage(message));
    }
}

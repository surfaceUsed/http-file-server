package org.example.enums;

/**
 * Enum representing the available commands for administering the server. Each command has a string representation
 * (command) and a description of what the command does. This enum is used to manage server administration, including
 * starting, stopping, restarting, and viewing the server's status and logs.
 */
public enum AdministratorCommands {

    START(".start", "Start server."),
    RESTART(".restart", "Restart server."),
    SHUT_DOWN(".shutdown", "Shut down the server."),
    STATUS(".status", "List the server status."),
    CONNECTIONS(".connections", "List all the active client ip addresses."),
    LOG(".log", "Print logs (enter '--info', '--error' or '--warn' for specific logs)."),
    CLEAR(".clear", "Clear text window."),
    HELP(".help", "Print the command inputs."),
    END(".end", "Shut down application (enter '--save' to save logs to local file)."),
    INVALID("", "Invalid command");

    private final String command;
    private final String description;

    /**
     * Initializes an administrator command with a command string and a description.
     *
     * @param command the string representation of the command.
     * @param description a brief explanation of what the command does.
     */
    AdministratorCommands(String command, String description) {
        this.command = command;
        this.description = description;
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Returns a formatted string listing all available administrator commands with their descriptions. This method
     * generates a user-friendly menu of commands for the administrator.
     *
     * @return a string representing a list of all available commands with descriptions.
     */
    public static String getCommandMenu() {
        StringBuilder sb = new StringBuilder("List of admin commands:\n");
        for (AdministratorCommands command : AdministratorCommands.values()) {
            if (command != INVALID) {
                sb.append("\n").append(String.format("%-20s%s", command.getCommand(), command.getDescription()));
            }
        }
        return sb.toString();
    }

    /**
     * Parses the input string and returns the corresponding administrator command. If the input does not match any
     * valid command, it returns the INVALID command.
     *
     * @param input the string input from the user to parse into a command
     * @return the corresponding AdministratorCommands enum, or INVALID if the input does not match any command.
     */
    public static AdministratorCommands parseCommand(String input) {
        for (AdministratorCommands command : AdministratorCommands.values()) {
            if (command.getCommand().equals(input)) {
                return command;
            }
        }
        return INVALID;
    }
}

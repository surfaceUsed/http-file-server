package org.example.error;

/**
 * Exception class for handling errors related to file rollback operations.
 * This exception is thrown when a file rollback fails or encounters an issue.
 */
public class FileRollbackException extends Exception {

    public FileRollbackException(String message) {
        super(message);
    }

}
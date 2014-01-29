/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.exceptions;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class FileManagementException extends Exception {

    /**
     * Creates a new instance of
     * <code>FileManagementException</code> without detail message.
     */
    public FileManagementException() {
    }

    /**
     * Constructs an instance of
     * <code>FileManagementException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public FileManagementException(String msg) {
        super(msg);
    }

    public FileManagementException(Throwable cause) {
        super(cause);
    }

    public FileManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}

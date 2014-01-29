/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.exceptions;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class ExecutionException extends Exception {

    /**
     * Creates a new instance of
     * <code>ExecutionException</code> without detail message.
     */
    public ExecutionException() {
    }

    /**
     * Constructs an instance of
     * <code>ExecutionException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ExecutionException(String msg) {
        super(msg);
    }

    public ExecutionException(Throwable cause) {
        super(cause);
    }

    public ExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}

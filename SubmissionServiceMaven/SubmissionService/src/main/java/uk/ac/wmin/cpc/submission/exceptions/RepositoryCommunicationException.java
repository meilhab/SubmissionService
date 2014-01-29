/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.exceptions;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class RepositoryCommunicationException extends Exception {

    /**
     * Creates a new instance of
     * <code>RepositoryCommunicationException</code> without detail message.
     */
    public RepositoryCommunicationException() {
    }

    /**
     * Constructs an instance of
     * <code>RepositoryCommunicationException</code> with the specified detail
     * message.
     *
     * @param msg the detail message.
     */
    public RepositoryCommunicationException(String msg) {
        super(msg);
    }

    public RepositoryCommunicationException(Throwable cause) {
        super(cause);
    }

    public RepositoryCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}

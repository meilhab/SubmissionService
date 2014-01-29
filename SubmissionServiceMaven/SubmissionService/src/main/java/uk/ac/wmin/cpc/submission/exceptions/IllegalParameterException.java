/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.exceptions;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class IllegalParameterException extends Exception {

    /**
     * Creates a new instance of
     * <code>IllegalParameterException</code> without detail message.
     */
    public IllegalParameterException() {
    }

    /**
     * Constructs an instance of
     * <code>IllegalParameterException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public IllegalParameterException(String msg) {
        super(msg);
    }

    public IllegalParameterException(Throwable cause) {
        super(cause);
    }

    public IllegalParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.cpc.submission.exceptions;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class WrongJSDLException extends Exception {

    /**
     * Creates a new instance of
     * <code>WrongJSDLException</code> without detail message.
     */
    public WrongJSDLException() {
    }

    /**
     * Constructs an instance of
     * <code>WrongJSDLException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public WrongJSDLException(String msg) {
        super(msg);
    }

    public WrongJSDLException(Throwable cause) {
        super(cause);
    }

    public WrongJSDLException(String message, Throwable cause) {
        super(message, cause);
    }
}

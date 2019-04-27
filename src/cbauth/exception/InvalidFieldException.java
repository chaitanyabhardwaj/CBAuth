/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cbauth.exception;

/**
 *
 * @author chaitanyabhardwaj
 */
public class InvalidFieldException extends Exception {

    /**
     * Creates a new instance of <code>InvalidFieldException</code> without detail message.
     */
    public InvalidFieldException() {
    }

    /**
     * Constructs an instance of <code>InvalidFieldException</code> with the specified detail
     * message.
     *
     * @param msg the detail message.
     */
    public InvalidFieldException(String msg) {
        super(msg);
    }
}

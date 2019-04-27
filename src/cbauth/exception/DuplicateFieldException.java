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
public class DuplicateFieldException extends Exception {

    /**
     * Creates a new instance of <code>DuplicateFieldException</code> without detail message.
     */
    public DuplicateFieldException() {
    }

    /**
     * Constructs an instance of <code>DuplicateFieldException</code> with the specified detail
     * message.
     *
     * @param msg the detail message.
     */
    public DuplicateFieldException(String msg) {
        super(msg);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.fsinf.restauth.errors;

/**
 *
 * @author mati
 */
public class InvalidCredentials extends Exception {

    /**
     * Creates a new instance of <code>InvalidCredentials</code> without detail message.
     */
    public InvalidCredentials() {
    }


    /**
     * Constructs an instance of <code>InvalidCredentials</code> with the specified detail message.
     * @param msg the detail message.
     */
    public InvalidCredentials(String msg) {
        super(msg);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.fsinf.restauth.errors;

/**
 *
 * @author mati
 */
public class UnmarshalException extends RestAuthRuntimeException {
    private String body;

    /**
     * Creates a new instance of <code>UnmarshalException</code> without detail message.
     */
    public UnmarshalException() {
    }

    /**
     * Constructs an instance of <code>UnmarshalException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public UnmarshalException(String msg, String body) {
        super(msg);
        this.body = body;
    }

    public String getBody() {
        return this.body;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.fsinf.restauth.errors;

import org.apache.http.HttpRequest;

/**
 * Thrown when actually making a HTTP request failed. This generally means that
 * the RestAuth server does not receive any HTTP packet.
 *
 * @author Mathias Ertl
 */
public class RequestFailed extends RestAuthException {
    private HttpRequest request;
    private Exception cause;

    /**
     * @todo: get around having to implement this constructor.
     */
    public RequestFailed() {}

    /**
     * Used when the library was not even able to assemble the request.
     * @param cause The exception originally thrown
     */
    public RequestFailed( Exception cause ) {
        this.cause = cause;
    }

    /**
     * Used when request was created and send the library was unable to reach
     * the RestAuth server.
     *
     * @param request The request made
     * @param cause The exception originally thrown
     */
    public RequestFailed( HttpRequest request, Exception cause ) {
        this.request = request;
        this.cause = cause;
    }

    /**
     *
     * @return
     */
    public HttpRequest getRequest() {
        return this.request;
    }

    /**
     *
     * @return
     */
    public Exception getException() {
        return this.cause;
    }
}

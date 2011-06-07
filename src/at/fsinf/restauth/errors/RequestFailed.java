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
     * Get the request that was attempted.
     *
     * @return The attempted HTTP request.
     */
    public HttpRequest getRequest() {
        return this.request;
    }

    /**
     * The initial cause of this exception.
     *
     * @return The exception that was originally thrown.
     */
    public Exception getException() {
        return this.cause;
    }
}

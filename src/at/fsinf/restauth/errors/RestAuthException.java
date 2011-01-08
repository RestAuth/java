package at.fsinf.restauth.errors;

import at.fsinf.restauth.common.RestAuthResponse;

/**
 * This class serves as a base class for all RestAuth related errors. If you 
 * just don't care what exactly has gone wrong with your request to the RestAuth
 * server, you can just catch exceptions of this class.
 *
 * If you want to distinguish between exceptions caused by input and
 * implementation related exceptions, you might want to catch
 * {@link RestAuthRuntimeException} instead.
 *
 * @see RestAuthRuntimeException
 * @author Mathias Ertl
 */
public abstract class RestAuthException extends Exception {
    /**
     * The response that triggered this exception.
     */
    protected RestAuthResponse response;

    /**
     * Default no-arg constructor.
     *
     * @todo: Try getting around having to implement this constructor.
     */
    public RestAuthException() {}

    /**
     * Constructor generally used. This constructor is typically called by
     * implementing subclasses to verify that the correct exception for this
     * HTTP response is used.
     *
     * @param resp The response that triggered this exception.
     * @param expectedCode The expected exception code.
     */
    public RestAuthException( RestAuthResponse resp, int expectedCode ) {
        this.response = resp;
        if ( expectedCode != resp.getStatusCode() )
            throw new RuntimeException( "RestAuthException with wrong status!");
    }

    /**
     * Get the HTTP response that that triggered this exception.
     *
     * @return The HTTP response that triggered this exception.
     */
    public RestAuthResponse getResponse() {
        return this.response;
    }

    /**
     * Get the body of the HTTP response that triggered this exception.
     *
     * @return The body of the HTTP response that triggered this exception.
     */
    public String getResponseBody() {
        return this.response.getBody();
    }

    /**
     * Get the body of the HTTP response that triggered this exception.
     *
     * @return The status code of the HTTP response that triggered this
     * exception.
     */
    public int getResponseCode() {
        return this.response.getStatusCode();
    }
}

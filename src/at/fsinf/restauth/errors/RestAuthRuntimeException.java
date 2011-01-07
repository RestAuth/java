package at.fsinf.restauth.errors;

import at.fsinf.restauth.common.RestAuthResponse;

/**
 * This is an abstract base class for all exceptions that are not caused by the
 * user of this library and should not happen if the server implementation
 * conforms to standards and is working correctly.
 *
 * When implementing a client it is generally enough to catch this exception to
 * determine if the RestAuth service is (for whatever reason) currently
 * unavailable.
 *
 * @author Mathias Ertl
 */
public abstract class RestAuthRuntimeException extends RuntimeException {
    RestAuthResponse response;

    /**
     * @todo try getting around having to implement this constructor.
     */
    public RestAuthRuntimeException() {};
    public RestAuthRuntimeException( RestAuthResponse response, int expectedCode ) {
        this.response = response;
        if ( expectedCode != response.getStatusCode() )
            throw new RuntimeException( "RestAuthRuntimeException with wrong status!");
    }
}

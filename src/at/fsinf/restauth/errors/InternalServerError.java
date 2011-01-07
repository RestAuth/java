package at.fsinf.restauth.errors;

import at.fsinf.restauth.common.RestAuthResponse;
import org.apache.http.HttpStatus;

/**
 * Thrown when the RestAuth server suffers from an internal server error.
 *
 * @author Mathias Ertl
 */
public class InternalServerError extends RestAuthException {
    public InternalServerError( RestAuthResponse response ) {
        super( response, HttpStatus.SC_INTERNAL_SERVER_ERROR );
    }
}

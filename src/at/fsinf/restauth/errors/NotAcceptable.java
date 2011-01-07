package at.fsinf.restauth.errors;

import at.fsinf.restauth.common.RestAuthResponse;
import org.apache.http.HttpStatus;

/**
 * Thrown when the RestAuth server responds with HTTP status code 406 Not
 * Acceptable.
 *
 * @author Mathias Ertl
 */
public class NotAcceptable extends RestAuthRuntimeException {
    public NotAcceptable( RestAuthResponse response ) {
        super( response, HttpStatus.SC_NOT_ACCEPTABLE );
    }
}

package at.fsinf.restauth.errors;

import at.fsinf.restauth.common.RestAuthResponse;
import org.apache.http.HttpStatus;

/**
 * Thrown when the RestAuth server responded with HTTP status code 400 Bad
 * Request. This would most probably indicate a bug in the library.
 *
 * @author Mathias Ertl
 */
public class BadRequest extends RestAuthInternalError {
    public BadRequest( RestAuthResponse response ) {
        super( response, HttpStatus.SC_BAD_REQUEST );
    }
}

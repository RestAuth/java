package at.fsinf.restauth.errors;

import at.fsinf.restauth.common.RestAuthResponse;

/**
 * Thrown when the RestAuth server responded with an unknown HTTP status code.
 * @author Mathias Ertl
 */
public class UnknownStatus extends RestAuthInternalError {
    public UnknownStatus( RestAuthResponse response ) {
        super( response, response.getStatusCode() );
    }
}

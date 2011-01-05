package at.fsinf.restauth.errors;

import at.fsinf.restauth.common.RestAuthConnection;
import at.fsinf.restauth.common.RestAuthResponse;
import org.apache.http.HttpStatus;

/**
 * Thrown when the authentication credentials provided are not accepted by the
 * RestAuth server. You may want to use 
 * {@link RestAuthConnection#setCredentials(java.lang.String, java.lang.String) setCredentials}
 * to update the credentials.
 *
 * @author Mathias Ertl
 */
public class Unauthorized extends RestAuthException {
    public Unauthorized( RestAuthResponse response ) {
        super( response, HttpStatus.SC_UNAUTHORIZED );
    }
}

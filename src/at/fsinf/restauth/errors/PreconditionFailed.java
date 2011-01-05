package at.fsinf.restauth.errors;

import at.fsinf.restauth.common.RestAuthResponse;
import org.apache.http.HttpStatus;

/**
 * Thrown when attempting to create/update a username and/or password that
 * doesn't meet quality standards. This includes i.e. passwords or usernames
 * that are too short or include invalid characters.
 *
 * @author Mathias Ertl
 */
public class PreconditionFailed extends RestAuthException {
    public PreconditionFailed( RestAuthResponse response ) {
        super( response, HttpStatus.SC_PRECONDITION_FAILED );
    }
}

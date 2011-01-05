package at.fsinf.restauth.errors;

import at.fsinf.restauth.common.RestAuthResponse;
import org.apache.http.HttpStatus;

/**
 * Base class for errors when trying to create a resource that already exists.
 *
 * @author Mathias Ertl
 */
public abstract class ResourceExists extends RestAuthException {
    public ResourceExists(RestAuthResponse response) {
        super( response, HttpStatus.SC_CONFLICT );
    }
}

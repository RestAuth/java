package at.fsinf.restauth.errors;

import at.fsinf.restauth.common.RestAuthResponse;
import org.apache.http.HttpStatus;

/**
 * 
 *
 * @author Mathias Ertl
 */
public class ResourceExists extends RestAuthException {
    /**
     *
     * @param response
     */
    public ResourceExists(RestAuthResponse response) {
        super( response, HttpStatus.SC_CONFLICT );
    }
}

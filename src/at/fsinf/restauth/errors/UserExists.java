package at.fsinf.restauth.errors;

import at.fsinf.restauth.common.RestAuthResponse;

/**
 * Thrown when a attempting to create a user that already exists.
 *
 * @author Mathias Ertl
 */
public class UserExists extends ResourceExists {
    public UserExists( RestAuthResponse response ) {
        super(response);
    }
}

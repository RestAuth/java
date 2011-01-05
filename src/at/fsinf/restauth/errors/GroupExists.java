package at.fsinf.restauth.errors;

import at.fsinf.restauth.common.RestAuthResponse;

/**
 * Thrown when attempting to create a group that already exists.
 *
 * @author Mathias Ertl
 */
public class GroupExists extends ResourceExists {
    public GroupExists( RestAuthResponse response ) {
        super(response);
    }
}

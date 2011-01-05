package at.fsinf.restauth.errors;

import at.fsinf.restauth.common.RestAuthResponse;

/**
 * Thrown when trying to create a property that already exists.
 *
 * @author Mathias Ertl
 */
public class PropertyExists extends ResourceExists {
    public PropertyExists( RestAuthResponse response ) {
        super(response);
    }
}

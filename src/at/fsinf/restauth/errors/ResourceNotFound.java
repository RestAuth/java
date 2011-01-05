/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.fsinf.restauth.errors;

import at.fsinf.restauth.common.RestAuthResponse;
import org.apache.http.HttpStatus;

/**
 * Thrown when querying a resource that doesn't exist. You can use
 * {@link getType} to find out what resource could not be found.
 *
 * @author Mathias Ertl
 */
public class ResourceNotFound extends RestAuthException {
    public ResourceNotFound( RestAuthResponse response ) {
        super( response, HttpStatus.SC_NOT_FOUND );
    }

    /**
     * Find out what resource could not be found. This returns the value of the
     * <a href="http://fs.fsinf.at/wiki/RestAuth/Specification#Resource-Type_header">
     * Resource-Type header</a>.
     *
     * @return The type of resource that could not be found. Unless the server
     *      behaves incorrectly, this should be one of "user", "group" or
     *      "property".
     */
    public String getType() {
        return this.response.getHeader("Resource-Type");
    }
}

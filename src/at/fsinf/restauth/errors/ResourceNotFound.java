/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.fsinf.restauth.errors;

import at.fsinf.restauth.common.RestAuthResponse;
import org.apache.http.HttpStatus;

/**
 *
 * @author mati
 */
public class ResourceNotFound extends RestAuthException {
    /**
     *
     * @param response
     */
    public ResourceNotFound( RestAuthResponse response ) {
        super( response, HttpStatus.SC_NOT_FOUND );
    }

    /**
     *
     * @return
     */
    public String getType() {
        return this.response.getHeader("Resource-Type");
    }
}

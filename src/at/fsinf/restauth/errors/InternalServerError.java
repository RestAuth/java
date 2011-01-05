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
public class InternalServerError extends RestAuthInternalError {
    /**
     *
     * @param response
     */
    public InternalServerError( RestAuthResponse response ) {
        super( response, HttpStatus.SC_INTERNAL_SERVER_ERROR );
    }
}

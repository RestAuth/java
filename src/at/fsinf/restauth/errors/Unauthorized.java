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
public class Unauthorized extends RestAuthException {
    /**
     *
     * @param response
     */
    public Unauthorized( RestAuthResponse response ) {
        super( response, HttpStatus.SC_UNAUTHORIZED );
    }
}

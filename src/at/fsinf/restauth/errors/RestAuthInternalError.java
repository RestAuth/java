/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.fsinf.restauth.errors;

import at.fsinf.restauth.common.RestAuthResponse;

/**
 *
 * @author mati
 */
public abstract class RestAuthInternalError extends RestAuthException {
    /**
     *
     */
    public RestAuthInternalError() {};
    /**
     *
     * @param response
     * @param expectedCode
     */
    public RestAuthInternalError( RestAuthResponse response, int expectedCode ) {
        super( response, expectedCode );
    }
}

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
public class UserExists extends ResourceExists {
    /**
     *
     * @param response
     */
    public UserExists( RestAuthResponse response ) {
        super(response);
    }
}

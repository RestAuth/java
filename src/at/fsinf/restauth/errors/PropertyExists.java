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
public class PropertyExists extends ResourceExists {
    /**
     *
     * @param response
     */
    public PropertyExists( RestAuthResponse response ) {
        super(response);
    }
}

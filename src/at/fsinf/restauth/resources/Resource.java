/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.fsinf.restauth.resources;

import at.fsinf.restauth.common.RestAuthConnection;
import at.fsinf.restauth.common.RestAuthResponse;
import at.fsinf.restauth.errors.RestAuthException;
import java.util.Map;

/**
 *
 * @author mati
 */
public abstract class Resource {
    protected String name;
    protected RestAuthConnection conn;

    protected Resource( RestAuthConnection connection, String name ) {
        this.name = name;
        this.conn = connection;
    }

    protected abstract RestAuthResponse 
            get( String path, String... args )
            throws RestAuthException;
    protected abstract RestAuthResponse 
            get( String path, Map<String, String> params, String... args )
            throws RestAuthException;
    protected abstract RestAuthResponse 
            post( String path, Map<String, String> params, String... args )
            throws RestAuthException;
    protected abstract RestAuthResponse 
            put( String path, Map<String, String> params, String... args )
            throws RestAuthException;
    protected abstract RestAuthResponse
            delete( String path, String... args )
            throws RestAuthException;

    @Override
    public boolean equals( Object other ) {
        if ( this.getClass() != other.getClass() ) return false;
        Resource o = (Resource) other;
        return o.name.equals( this.name ) && this.conn.equals( o.conn );
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() * 3 * this.conn.hashCode();
    }
}

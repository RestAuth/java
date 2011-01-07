package at.fsinf.restauth.resources;

import at.fsinf.restauth.common.RestAuthConnection;
import at.fsinf.restauth.common.RestAuthResponse;
import at.fsinf.restauth.errors.RestAuthException;
import java.util.Map;

/**
 * Superclass for {@link Group groups} and {@link User users}.
 *
 * @author Mathias Ertl
 */
public abstract class Resource {
    /**
     * The name of the given resource.
     */
    protected String name;

    /**
     * The connection used by this resource.
     */
    protected RestAuthConnection conn;

    /**
     * Default constructor.
     *
     * @param connection The connection to use.
     * @param name The name of the resource.
     */
    protected Resource( RestAuthConnection connection, String name ) {
        this.name = name.toLowerCase();
        this.conn = connection;
    }

    /**
     * Perform a HTTP GET request with the given path prefixed by this resources
     * prefix.
     *
     * @param path The path for the HTTP request.
     * @return The response of the request.
     * @throws RestAuthException
     */
    protected abstract RestAuthResponse 
            get( String path )
            throws RestAuthException;
    /**
     * Perform a HTTP GET request with the given path prefixed by this resources
     * prefix and with the given query parameters attached.
     *
     * @param path The path for the HTTP request.
     * @param params The query parameters.
     * @return The response of the request.
     * @throws RestAuthException
     */
    protected abstract RestAuthResponse 
            get( String path, Map<String, String> params  )
            throws RestAuthException;

    /**
     * Perform a HTTP POST request with the given path prefixed by this
     * resources prefix. The parameters represent a <a
     * href="http://fs.fsinf.at/wiki/RestAuth/Specification#Dictionary">
     * dictionary</a> to be used as the request body.
     *
     * @param path The path for the HTTP request.
     * @param params The query parameters.
     * @return The response of the request.
     * @throws RestAuthException
     */
    protected abstract RestAuthResponse 
            post( String path, Map<String, String> params )
            throws RestAuthException;

    /**
     * Perform a HTTP PUT request with the given path prefixed by this
     * resources prefix. The parameters represent a <a
     * href="http://fs.fsinf.at/wiki/RestAuth/Specification#Dictionary">
     * dictionary</a> to be used as the request body.
     *
     * @param path The path for the HTTP request.
     * @param params The query parameters.
     * @return The response of the request.
     * @throws RestAuthException
     */
    protected abstract RestAuthResponse 
            put( String path, Map<String, String> params )
            throws RestAuthException;

    /**
     * Perform a HTTP DELETE request with the given path prefixed by this
     * resources prefix.
     *
     * @param path The path for the HTTP request.
     * @return The response of the request.
     * @throws RestAuthException
     */
    protected abstract RestAuthResponse
            delete( String path )
            throws RestAuthException;
}

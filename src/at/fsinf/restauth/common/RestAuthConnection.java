/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.fsinf.restauth.common;

import at.fsinf.restauth.errors.InternalServerError;
import at.fsinf.restauth.errors.NotAcceptable;
import at.fsinf.restauth.errors.RequestFailed;
import at.fsinf.restauth.errors.Unauthorized;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import sun.misc.BASE64Encoder;
/**
 * An instance of this class represents a connection to a RestAuth service. It
 * is used to provide basic HTTP messaging needs.
 *
 * @author Mathias Ertl
 */
public class RestAuthConnection extends DefaultHttpClient {
    /**
     * @TODO make this private
     */
    public ContentHandler handler;

    private URI host;
    private String authHeader;

    /**
     * Creates a new connection to a RestAuth service. This method uses the
     * default JSON content handler.
     *
     * @param host The host where the RestAuth server can be reached (i.e.
     *      "https://auth.example.com").
     * @param user The user used to authenticate against the RestAuth server.
     * @param passwd The password used to authenticate against the RestAuth server.
     * @throws MalformedURLException If the URL passed in the host variable does
     *      not contain a valid URL.
     */
    public RestAuthConnection( String host, String user, String passwd )
            throws URISyntaxException {
        this( new URI(host), user, passwd, new JsonHandler() );
    }
    /**
     * Creates a new connection to a RestAuth service. This method uses the
     * default JSON content handler.
     *
     * @param host host The host where the RestAuth server can be reached (i.e.
     *      "https://auth.example.com").
     * @param user The user used to authenticate against the RestAuth server.
     * @param passwd The password used to authenticate against the RestAuth server.
     */
    public RestAuthConnection( URI host, String user, String passwd ) {
        this( host, user, passwd, new JsonHandler() );
    }
    
    /**
     * Creates a new connection to a RestAuth service.
     *
     * @param host host The host where the RestAuth server can be reached (i.e.
     *      "https://auth.example.com").
     * @param user The user used to authenticate against the RestAuth server.
     * @param passwd The password used to authenticate against the RestAuth server.
     * @param handler The content handler to use.
     * @throws MalformedURLException If the URL passed in the host variable does
     *      not contain a valid URL.
     */
    public RestAuthConnection( String host, String user, String passwd, ContentHandler handler )
            throws URISyntaxException {
        this( new URI( host ), user, passwd, handler );
    }

    /**
     * Creates a new connection to a RestAuth service.
     *
     * @param host host The host where the RestAuth server can be reached (i.e.
     *      "https://auth.example.com").
     * @param user The user used to authenticate against the RestAuth server.
     * @param passwd The password used to authenticate against the RestAuth server.
     * @param handler The content handler to use.
     */
    public RestAuthConnection( URI host, String user, String passwd, ContentHandler handler ) {
        this.handler = handler;
        this.setCredentials(user, passwd);
        this.host = host;
    }

    /**
     * Set a different content handler.
     *
     * @param handler The new content handler to use.
     */
    public void setContentHandler( ContentHandler handler ) {
        this.handler = handler;
    }

    /**
     * Get the current content handler.
     *
     * @return The current content handler.
     */
    public ContentHandler getContentHandler() {
        return this.handler;
    }

    /**
     * Set new authentication credentials.
     *
     * @param user The new username to use.
     * @param passwd The new password to use.
     */
    public final void setCredentials( String user, String passwd ) {
        String raw_header = user + ":" + passwd;
        BASE64Encoder enc = new BASE64Encoder();
        this.authHeader = enc.encode( raw_header.getBytes() );
    }

    /**
     * Thin wrapper-method that prefixes the URL set in the request with the
     * host set in the constructor and wraps the HttpResponse into a {@link
     * RestAuthResponse}.
     *
     * @param request The request to execute. This should only include a path
     *      and optionally a query part but NOT a hostname, port or URL scheme.
     * @return The response returned by the RestAuth server.
     * @throws IOException When the connection to the RestAuth server fails.
     */
    public RestAuthResponse execute( HttpRequestBase request, String path, String query )
            throws IOException {
        // sanitize path:
        if ( ! path.endsWith( "/" ) ) path += "/";
        if ( ! path.startsWith( "/" ) ) path = "/" + path;
        path = path.replaceAll( "//", "/");

        // construct URI:
        try {
            URI fullURI = new URI(this.host.getScheme(), null,
                    this.host.getHost(), this.host.getPort(), path, query, null);
            request.setURI( fullURI );
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException( ex );
        }

        // execute request:
        return this.execute( request, new RestAuthResponseHandler() );
    }

    /**
     * Sends an HTTP request to the RestAuth server. This method is used
     * internally for the {@link get}, {@link get}, {@link put} and {@link
     * delete} methods and takes care of setting the Accept header and the 
     * authentication credentials. It also catches various error codes that may
     * be thrown upon every HTTP request.
     *
     * @param request The request to send.
     * @return The response returned by the RestAuth server.
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public RestAuthResponse send(HttpRequestBase request, String path )
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed {
        return this.send( request, path, null );
    }
    
    public RestAuthResponse send(HttpRequestBase request, String path, String query )
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed {
        request.addHeader( "Accept", this.handler.getMimeType() );
        request.addHeader( "Authorization", "Basic " + this.authHeader );


        RestAuthResponse response;
        try {
            response = this.execute(request, path, query );
        } catch (IOException ex) {
            throw new RequestFailed( request, ex );
        }
        int respCode = response.getStatusCode();

        switch( respCode ) {
            case HttpStatus.SC_UNAUTHORIZED:
                throw new Unauthorized( response );
            case HttpStatus.SC_NOT_ACCEPTABLE:
                throw new NotAcceptable( response );
            case HttpStatus.SC_INTERNAL_SERVER_ERROR:
                throw new InternalServerError( response );
            default:
                return response;
        }
    }

    /**
     * Perform a GET request to the RestAuth server.
     *
     * @param path The path to make the request to.
     * @return The response returned by the RestAuth server.
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public RestAuthResponse get( String path )
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed {
        return this.send( new HttpGet(), path );
    }

    /**
     * Perform a GET request with a query string to the RestAuth server.
     *
     * @param path The path to make the request to.
     * @param params The query string represented as a map. Each map key
     *      represents a query string key, each map value a query string value.
     * @return The response returned by the RestAuth server.
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public RestAuthResponse get( String path, Map<String, String> params )
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed {
        String queryString = "";
        Set<String> keys = params.keySet();
        Iterator<String> iter = keys.iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            queryString += key + "=" + params.get(key);
            
            if (iter.hasNext()) {
                queryString += "&";
            } 
        }

        return this.send( new HttpGet(), path, queryString );
    }

    /**
     * Perform a POST request to the RestAuth server.
     *
     * @param path The path to make the request to.
     * @param params The dictionary that should be use as the request body.
     *
     * @return The response returned by the RestAuth server.
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     * @see The <a href="http://fs.fsinf.at/wiki/RestAuth/Specification#Dictionary">
     *      Dictionary</a> in the RestAuth specification.
     */
    public RestAuthResponse post( String path, Map<String, String> params )
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed {
        HttpPost method = new HttpPost();
        String body = this.handler.marshal_dictionary( params );
        try {
            method.setEntity(new StringEntity(body, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new RequestFailed( ex );
        }
        method.addHeader( "Content-Type", this.handler.getMimeType() );

        return this.send( method, path );
    }

    /**
     * Perform a PUT request to the RestAuth server.
     *
     * @param path The path to make the request to.
     * @param params The dictionary that should be use as the request body.
     * @return The response returned by the RestAuth server.
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     * @see The <a href="http://fs.fsinf.at/wiki/RestAuth/Specification#Dictionary">
     *      Dictionary</a> in the RestAuth specification.
     */
    public RestAuthResponse put( String path, Map<String, String> params )
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed {
        HttpPut method = new HttpPut();
        String body = this.handler.marshal_dictionary( params );
        try {
            method.setEntity(new StringEntity(body, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new RequestFailed( ex );
        }
        method.addHeader( "Content-Type", this.handler.getMimeType() );

        return this.send( method, path );
    }

    /**
     * Perform a DELETE request to the RestAuth server.
     *
     * @param path The path to make the request to.
     * @return The response returned by the RestAuth server.
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws Unauthorized
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public RestAuthResponse delete( String path )
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed {
        return this.send( new HttpDelete(), path );
    }

    /**
     *
     * @param other
     * @return
     */
    @Override
    public boolean equals( Object other ) {
        if ( ! (other instanceof RestAuthConnection) ) return false;
        RestAuthConnection o = (RestAuthConnection) other;
        return this.host.equals(o.host) && this.authHeader.equals(o.authHeader);
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 1;
        return this.host.hashCode() * this.authHeader.hashCode();
    }
}

package at.fsinf.restauth.common;

import java.util.List;
import org.apache.http.Header;

/**
 * This class is used for easy access of interesting properties of an HTTP
 * response.
 *
 * @author Mathias Ertl
 */
public class RestAuthResponse {
    private List<Header> headers;
    private int statusCode;
    private String body;
    
    /**
     * Standard constructor.
     *
     * @param statusCode The status code of the response.
     * @param headers The headers of the response.
     * @param body The message body of the response.
     */
    public RestAuthResponse( int statusCode, List<Header> headers, String body ) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    /**
     * A constructor for responses containing no content (i.e. HTTP status code
     * 204 No Content).
     *
     * @param statusCode The status code of the response.
     * @param headers The headers of the response.
     */
    public RestAuthResponse( int statusCode, List<Header> headers ) {
        this.statusCode = statusCode;
        this.headers = headers;
    }

    /**
     * Get the body of this response.
     *
     * @return The body of the response.
     */
    public String getBody() {
        return this.body;
    }

    /**
     * Get the status code of this response.
     *
     * @return The status code of the response.
     */
    public int getStatusCode() {
        return this.statusCode;
    }
    
    /**
     * Get all headers of this response.
     *
     * @return All headers of this response.
     */
    public List<Header> getHeaders() {
        return this.headers;
    }

    /**
     * Get the (first) value of a specific header.
     *
     * @param name The name of the header.
     * @return The value of the header.
     */
    public String getHeader( String name ) {
        for (Header header : this.headers ) {
            if ( header.getName().equals( name ) )
                return header.getValue();
        }
        return null;
    }
}

package at.fsinf.restauth.common;

import java.util.List;
import java.util.Map;

/**
 * Abstract base class for content handlers. If you want to use your own content
 * type for this connection, you should implement a subclass of this class.
 *
 * @author Mathias Ertl
 */
public abstract class ContentHandler {
    /**
     * The MIME type used for this content type (i.e. "application/xml")
     */
    protected String MimeType;

    /**
     * Get the MIME type used by this implementation.
     *
     * @return The MIME type
     */
    public String getMimeType() {
        return this.MimeType;
    }

    /**
     * This method should convert a map into a raw string representation of a
     * dictionary as defined in the RestAuth specification.
     *
     * This representation will be the message body of the HTTP request send to
     * the RestAuth server.
     *
     * @param map The map to unmarshal.
     * @return The raw string representation of a dictionary.
     */
    public abstract String marshal_dictionary( Map<String, String> map );

    /**
     * This method should unmarshal a dictionary received by the RestAuth
     * server.
     *
     * @param raw The raw string representation of the dictionary.
     * @return The parsed map.
     */
    public abstract Map<String, String> unmarshal_dictionary( String raw );

    /**
     * This method should unmarshal a list received by the RestAuth server.
     *
     * @param raw The raw string representation of the list.
     * @return The parsed list.
     */
    public abstract List<String> unmarshal_list( String raw );
    
    /**
     * This method should unmarshal a string received by the RestAuth server.
     *
     * @param raw The raw string representation of the string.
     * @return The parsed string.
     */
    public abstract String unmarshal_string( String raw );
}

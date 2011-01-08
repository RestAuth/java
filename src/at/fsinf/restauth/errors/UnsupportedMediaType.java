package at.fsinf.restauth.errors;

import at.fsinf.restauth.common.RestAuthResponse;
import org.apache.http.HttpStatus;

/**
 * Thrown when the request sent an unsupported Content-Type header (see <a
 * href="http://fs.fsinf.at/wiki/RestAuth/Specification#Content-Type_header">
 * specification</a>, meaning that the RestAuthServer does not understand the
 * content type produced by the current
 * {@link at.fsinf.restauth.common.ContentHandler ContentHandler}.
 *
 * @author Mathias Ertl
 */
public class UnsupportedMediaType extends RestAuthRuntimeException {
    public UnsupportedMediaType( RestAuthResponse response ) {
        super( response, HttpStatus.SC_BAD_REQUEST );
    }
}

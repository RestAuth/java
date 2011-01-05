package at.fsinf.restauth.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

/**
 * This class is responsible for wrapping an HttpResponse into a {@link
 * RestAuthResponse}.
 *
 * @author Mathias Ertl
 */
public class RestAuthResponseHandler implements ResponseHandler<RestAuthResponse> {

    /**
     * Wrap a HttpResponse into a {@link RestAuthResponse}.
     *
     * @param hr The HTTP response received from a request.
     * @return The response wrapped in a convenient RestAuthResponse instance.
     * @throws ClientProtocolException
     * @throws IOException
     */
    @Override
    public RestAuthResponse handleResponse(HttpResponse hr)
            throws ClientProtocolException, IOException {

        int statusCode = hr.getStatusLine().getStatusCode();
        List<Header> headers = new ArrayList<Header>();
        headers.addAll( Arrays.asList( hr.getAllHeaders() ) );

        if ( statusCode != HttpStatus.SC_NO_CONTENT ) {
            String body = EntityUtils.toString( hr.getEntity() );
            return new RestAuthResponse( statusCode, headers, body );
        } else {
            return new RestAuthResponse( statusCode, headers );
        }
    }

}

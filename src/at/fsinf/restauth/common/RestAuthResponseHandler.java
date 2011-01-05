/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.fsinf.restauth.common;

import at.fsinf.restauth.common.RestAuthResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author mati
 */
public class RestAuthResponseHandler implements ResponseHandler<RestAuthResponse> {

    /**
     *
     * @param hr
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
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

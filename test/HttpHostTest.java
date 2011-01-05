/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.http.HttpHost;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mati
 */
public class HttpHostTest {

    public HttpHostTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    protected void test_url( String raw_url, String scheme, String host, int port )
            throws MalformedURLException {
        URL url = new URL( raw_url );
        assertEquals( scheme, url.getProtocol() );
        assertEquals( host, url.getHost() );
        assertEquals( port, url.getPort() );

        HttpHost httpHost = new HttpHost( url.getHost(), url.getPort(), url.getProtocol() );

        assertEquals( scheme, httpHost.getSchemeName() );
        assertEquals( host, httpHost.getHostName() );
        assertEquals( port, httpHost.getPort() );
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void basic_http() throws MalformedURLException {
        this.test_url( "http://example.com", "http", "example.com", -1);
    }

    @Test
    public void basic_https() throws MalformedURLException {
        this.test_url( "https://example.com", "https", "example.com", -1);
    }

    @Test
    public void http_with_port() throws MalformedURLException {
        this.test_url( "http://example.com:8080", "http", "example.com", 8080);
    }

    @Test
    public void https_with_port() throws MalformedURLException {
        this.test_url( "https://example.com:8443", "https", "example.com", 8443);
    }
}
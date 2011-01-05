/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import at.fsinf.restauth.errors.RestAuthException;
import at.fsinf.restauth.errors.RestAuthInternalError;
import at.fsinf.restauth.errors.InternalServerError;
import at.fsinf.restauth.errors.NotAcceptable;
import at.fsinf.restauth.errors.Unauthorized;
import at.fsinf.restauth.errors.UnknownStatus;
import at.fsinf.restauth.resources.User;
import java.net.URISyntaxException;
import java.util.List;
import java.util.HashMap;
import at.fsinf.restauth.common.RestAuthConnection;
import java.io.IOException;
import java.net.MalformedURLException;
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
public class ConnectionTests {
    public RestAuthConnection conn;

    public ConnectionTests() throws MalformedURLException, URISyntaxException {
        this.conn = new RestAuthConnection(
                "http://localhost:8000", "vowi", "vowi" );
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
    
//    @Test
    public void simpleGet() throws IOException, RestAuthException {
        this.conn.get( "/users/" );
    }

//    @Test
    public void queryTest() throws IOException, RestAuthException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("foo", "bar");
        this.conn.get( "users", params );
    }

//    @Test
    public void doubleQueryTest() throws IOException, RestAuthException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("foo", "bar");
        params.put("bla", "hugo");
        this.conn.get( "users", params );
    }

//    @Test
    public void spaceQueryTest() throws IOException, RestAuthException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("full name", "mathias robert benjamin ertl");
        params.put("name", "mathias ertl");
        params.put("nick name", "mati");
        params.put("short", "mat");
        this.conn.get( "users", params );
    }

    @Test
    public void postTest() throws IOException, RestAuthException {
        long now = System.currentTimeMillis();
        for ( int i = 0; i < 1000; i++ ) {
            List<User> users = User.get_all(this.conn);
            assertEquals( 0, users.size() );
        }
        long later = System.currentTimeMillis();

        long secs = (later-now)/1000;
        System.out.println( secs + "seconds (" + 1000/secs + " reqs/s).");
    }
}
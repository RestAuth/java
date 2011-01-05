/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import at.fsinf.restauth.errors.ResourceNotFound;
import at.fsinf.restauth.errors.PropertyExists;
import java.net.URISyntaxException;
import java.util.Map;
import at.fsinf.restauth.errors.RestAuthException;
import at.fsinf.restauth.resources.User;
import java.io.IOException;
import java.util.List;
import at.fsinf.restauth.common.RestAuthConnection;
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
public class UserTest {
    RestAuthConnection conn;

    public UserTest() throws MalformedURLException, URISyntaxException {
        this.conn = new RestAuthConnection(
            "http://localhost:8000", "vowi", "vowi");
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}

    @Test
    public void createUser() throws IOException, RestAuthException {
        String username = "mati";
        String password = "foobar";
        String newpassword = "new password";
        String prop_1_key = "email";
        String prop_1_val = "mati@fsinf.at";
        String prop_2_key = "full name";
        String prop_2_val = "Mathias Ertl";

        List<User> users = User.get_all( this.conn );
        assertEquals( 0, users.size() );

        User mati = User.create( this.conn, username, password );
        assertTrue( mati.verifyPassword( password ) );
        assertFalse( mati.verifyPassword("wrongpass") );
        mati.setPassword( newpassword );
        assertTrue( mati.verifyPassword( newpassword ) );
        assertFalse( mati.verifyPassword( password ) );

        users = User.get_all( this.conn );
        assertEquals( 1, users.size() );
        assertEquals( users.get(0), mati );

        Map<String, String> props = mati.getProperties();
        assertEquals( 0, props.size() );

        mati.createProperty(prop_1_key, prop_1_val);
        props = mati.getProperties();
        assertEquals( 1, props.size() );
        assertTrue( props.containsKey( prop_1_key ) );
        assertEquals( prop_1_val, props.get(prop_1_key));
        assertEquals( prop_1_val, mati.getProperty(prop_1_key));
        try {
            mati.createProperty(prop_1_key, "wrong@fsinf.at" );
            fail(); // this should already exists
        } catch (PropertyExists e) {
            // verify that the one defined property hasn't changed:
            props = mati.getProperties();
            assertEquals( 1, props.size() );
            assertTrue( props.containsKey( prop_1_key ) );
            assertEquals( prop_1_val, props.get(prop_1_key));
            assertEquals( prop_1_val, mati.getProperty(prop_1_key));
        }

        // setting property. second try returns old value:
        assertNull( mati.setProperty(prop_2_key, prop_2_val ) );
        assertEquals( prop_2_val, mati.getProperty(prop_2_key) );
        assertEquals( prop_2_val, mati.setProperty(prop_2_key,
                "Mathias Robert Benjamin Ertl" ));
        assertEquals( "Mathias Robert Benjamin Ertl",
                mati.getProperty(prop_2_key) );
        assertEquals( prop_1_val, mati.getProperty(prop_1_key));
        
        // verify that we now have the correct two properties:
        props = mati.getProperties();
        assertEquals( 2, props.size() );
        assertTrue( props.containsKey( prop_1_key ));
        assertTrue( props.containsKey( prop_2_key ));
        assertEquals( "Mathias Robert Benjamin Ertl", props.get(prop_2_key));
        assertEquals( prop_1_val, props.get(prop_1_key) );
        
        try {
            // try to get a propety that does not exist
            mati.getProperty( "does not exist" );
            fail();
        } catch (ResourceNotFound e) {
            assertEquals( "property", e.getType());
        }

        // remove the prop_2_key property and verify that its gone:
        mati.removeProperty(prop_2_key);
        props = mati.getProperties();
        assertEquals( 1, props.size() );
        assertTrue( props.containsKey( prop_1_key ) );
        assertEquals( prop_1_val, props.get(prop_1_key));
        try {
            mati.getProperty(prop_2_key);
            fail();
        } catch (ResourceNotFound e) {
            assertEquals( "property", e.getType());
        }


        /// remove user and verify that we're gone
        mati.remove();
        users = User.get_all( this.conn );
        assertEquals( 0, users.size() );
        try {
            User.get( this.conn, mati.getName());
        } catch (ResourceNotFound e) {
            assertEquals("user", e.getType() );
        }
    }
}
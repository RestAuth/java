/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.HashMap;
import at.fsinf.restauth.errors.PreconditionFailed;
import at.fsinf.restauth.errors.ResourceNotFound;
import at.fsinf.restauth.errors.PropertyExists;
import java.net.URISyntaxException;
import java.util.Map;
import at.fsinf.restauth.errors.RestAuthException;
import at.fsinf.restauth.resources.User;
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
public class UserTests {
    RestAuthConnection conn = new RestAuthConnection( "http://localhost:8000",
            "vowi", "vowi");
    String username = "user \u611b";
    String password = "password \u611b";
    String prop_1_key = "key \u609b";
    String prop_1_val = "val \u610b";

    public UserTests() throws MalformedURLException, URISyntaxException {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws RestAuthException {
        List<User> users = User.getAll( this.conn );
        assertEquals( 0, users.size() );
    }

    @After
    public void tearDown() throws RestAuthException {
        List<User> users = User.getAll( this.conn );
        for ( User user : users ) {
            user.remove();
        }
    }

    @Test
    public void createUser() throws RestAuthException {
        User user = User.create( this.conn, "createuser", "password" );
        User user_get = User.get( this.conn, "createuser" );
        List<User> users = User.getAll( this.conn );
        assertEquals( user, user_get );
        assertEquals( 1, users.size() );
        assertEquals( user, users.get(0) );
    }

    @Test
    public void createUserWithSlash() throws RestAuthException {
        try {
            User.create( this.conn, "create/user", "pwd" );
            fail();
        } catch (PreconditionFailed e) {
            assertEquals( 0, User.getAll( this.conn ).size() );
        }
    }

    @Test
    public void createUserWithSpace() throws RestAuthException {
        User user = User.create( this.conn, "create user", "password" );
        User user_get = User.get( this.conn, "create user" );
        List<User> users = User.getAll( this.conn );
        assertEquals( user, user_get );
        assertEquals( 1, users.size() );
        assertEquals( user, users.get(0) );
    }

    @Test
    public void createUserWithUnicode() throws RestAuthException {
        User user = User.create( this.conn, this.username, "password" );
        User user_get = User.get( this.conn, this.username );
        List<User> users = User.getAll( this.conn );
        assertEquals( user, user_get );
        assertEquals( 1, users.size() );
        assertEquals( user, users.get(0) );
    }

    @Test
    public void testVerifyPassword() throws RestAuthException {
        User user = User.create( this.conn, this.username, this.password );

        assertTrue( user.verifyPassword( this.password ) );
        assertFalse( user.verifyPassword( "whatever" ) );
    }

    @Test
    public void testSetPassword() throws RestAuthException {
        String newpassword = "new password \u612b";
        User user = User.create( this.conn, this.username, this.password );
        assertFalse( user.verifyPassword( newpassword ) );
        assertTrue( user.verifyPassword( this.password ) );

        user.setPassword( newpassword );
        assertTrue( user.verifyPassword( newpassword ) );
        assertFalse( user.verifyPassword( this.password ) );
    }

    @Test
    public void testPasswordOfInvalidUser() throws RestAuthException {
        User user = new User( this.conn, this.username );
        assertFalse( user.verifyPassword( this.password ) );
    }

    @Test
    public void createProperty() throws RestAuthException {
        Map<String, String> props = new HashMap<String, String>();

        User user = User.create( this.conn, this.username, this.password );
        user.createProperty(prop_1_key, prop_1_val);
        props = user.getProperties();
        assertEquals( 1, props.size() );
        assertTrue( props.containsKey(prop_1_key));
        assertEquals( prop_1_val, props.get(prop_1_key));
        assertEquals( prop_1_val, user.getProperty(prop_1_key));
    }

    @Test
    public void createPropertyTwice() throws RestAuthException {
        Map<String, String> props = new HashMap<String, String>();

        String prop_1_val_new = "val \u611b";

        User user = User.create( this.conn, this.username, this.password );
        try {
            user.createProperty(prop_1_key, prop_1_val);
        } catch (PropertyExists ex ){
            fail();
        }

        try {
            user.createProperty(prop_1_key, prop_1_val_new);
            fail();
        } catch (PropertyExists ex) {
            // assure that the value wasn't overwritten!
            props = user.getProperties();
            assertEquals( 1, props.size() );
            assertTrue( props.containsKey(prop_1_key));
            assertEquals( prop_1_val, props.get(prop_1_key));
            assertEquals( prop_1_val, user.getProperty(prop_1_key));
        }
    }

    @Test
    public void createPropertyWithInvalidUser() throws RestAuthException {
        User user = new User( this.conn, this.username );
        try {
            user.createProperty("foo", "bar");
            fail();
        } catch (ResourceNotFound ex) {
            assertEquals( "user", ex.getType() );
        }
    }

    @Test
    public void setProperty() throws RestAuthException {
        Map<String, String> props;
        User user = User.create( this.conn, this.username, this.password );

        assertEquals( 0, user.getProperties().size() );
        assertNull( user.setProperty(prop_1_key, prop_1_val ) );
        props = user.getProperties();
        assertEquals( 1, props.size() );
        assertTrue( props.containsKey(prop_1_key));
        assertEquals( prop_1_val, props.get(prop_1_key));
        assertEquals( prop_1_val, user.getProperty(prop_1_key));
    }

    @Test
    public void setPropertyTwice() throws RestAuthException {
        Map<String, String> props;
        String prop_1_val_new = "key \u612b";
        User user = User.create( this.conn, this.username, this.password );

        assertNull( user.setProperty(prop_1_key, prop_1_val ) );
        assertEquals( prop_1_val, user.setProperty(prop_1_key, prop_1_val_new));
        props = user.getProperties();
        assertEquals( 1, props.size() );
        assertTrue( props.containsKey(prop_1_key));
        assertEquals( prop_1_val_new, props.get(prop_1_key));
        assertEquals( prop_1_val_new, user.getProperty(prop_1_key));
    }

    @Test
    public void removeProperty() throws RestAuthException {
        User user = User.create( this.conn, this.username, this.password );

        assertNull( user.setProperty(prop_1_key, prop_1_val ) );
        user.removeProperty(prop_1_key);
        assertEquals( 0, user.getProperties().size() );
    }

    @Test
    public void removeInvalidProperty() throws RestAuthException {
        User user = User.create( this.conn, this.username, this.password );

        try {
            user.removeProperty(prop_1_key);
            fail();
        } catch (ResourceNotFound ex) {
            assertEquals( "property", ex.getType() );
        }
    }

    @Test
    public void removePropertyOfInvalidUser() throws RestAuthException {
        User user = new User( this.conn, this.username );

        try {
            user.removeProperty(prop_1_key);
            fail();
        } catch (ResourceNotFound ex) {
            assertEquals( "user", ex.getType() );
        }
    }

    @Test
    public void removeUser() throws RestAuthException {
        User user = User.create( this.conn, this.username, this.password );
        user.remove();
        assertEquals( 0, User.getAll(conn).size() );
    }

    @Test
    public void removeInvalidUser() throws RestAuthException {
        User user = new User( this.conn, this.username );
        try {
            user.remove();
            fail();
        } catch (ResourceNotFound ex ) {
            assertEquals( "user", ex.getType() );
        }
    }
}
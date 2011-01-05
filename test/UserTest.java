/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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

    public UserTest() throws MalformedURLException {
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
        List<User> users = User.get_all( this.conn );
        assertEquals( 0, users.size() );

        User mati = User.create( this.conn, "mati", "foobar" );
        assertTrue( mati.verifyPassword("foobar") );
        assertFalse( mati.verifyPassword("wrongpass") );
        mati.setPassword("new password");
        assertTrue( mati.verifyPassword("new password") );
        assertFalse( mati.verifyPassword("foobar") );

        users = User.get_all( this.conn );
        assertEquals( 1, users.size() );
        assertEquals( users.get(0), mati );

        mati.remove();
        users = User.get_all( this.conn );
        assertEquals( 0, users.size() );
    }
}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import at.fsinf.restauth.errors.ResourceNotFound;
import at.fsinf.restauth.errors.PreconditionFailed;
import at.fsinf.restauth.resources.Group;
import at.fsinf.restauth.errors.RestAuthException;
import at.fsinf.restauth.common.RestAuthConnection;
import at.fsinf.restauth.resources.User;
import java.net.URISyntaxException;
import java.util.List;
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
public class GroupTests {
    RestAuthConnection conn;
    String group_1 = "group \u601a";
    String group_2 = "group \u602a";
    String group_3 = "group \u603a";
    String group_4 = "group \u604a";

    String user_1 = "user \u701a";
    String user_2 = "user \u702a";
    String user_3 = "user \u703a";
    String user_4 = "user \u704a";


    public GroupTests() throws URISyntaxException {
        this.conn = new RestAuthConnection( "http://localhost:8000",
            "vowi", "vowi");
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
        List<Group> groups = Group.getAll( this.conn );
        assertEquals( 0, users.size() );
        assertEquals( 0, groups.size() );
    }

    @After
    public void tearDown() throws RestAuthException {
        List<User> users = User.getAll( this.conn );
        for( User user : users ) user.remove();

        List<Group> groups = Group.getAll(this.conn);
        for ( Group group : groups ) group.remove();
    }

    @Test
    public void createGroup() throws RestAuthException {
        Group group = Group.create(conn, group_1);
        List<Group> groups = Group.getAll(this.conn);
        assertEquals( 1, groups.size() );
        assertEquals( group, groups.get(0));
        assertEquals( group, Group.get(conn, group_1));
    }

    @Test
    public void createGroupWithSlash() throws RestAuthException {
        try {
            Group group = Group.create(conn, "foo/bar");
            fail();
        } catch ( PreconditionFailed ex ) {
            assertEquals( 0, Group.getAll(this.conn).size() );
        }
    }

    @Test
    public void addUser() throws RestAuthException {
        Group group = Group.create(conn, group_1);
        User usr1 = User.create(conn, user_1, "password");
        User usr2 = User.create(conn, user_2, "password");
        User usr3 = User.create(conn, user_3, "password");

        List<User> users = group.getUsers();
        assertEquals( 0, users.size() );

        group.addUser( usr1 );
        users = group.getUsers();
        assertTrue( group.isMember( usr1 ) );
        assertFalse( group.isMember( usr2 ) );
        assertFalse( group.isMember( usr3 ) );
        assertEquals( 1, users.size() );
        assertTrue( users.contains( usr1 ) );

        group.addUser(usr2);
        users = group.getUsers();
        assertEquals( 2, users.size() );
        assertTrue( users.contains( usr1 ) );
        assertTrue( users.contains( usr2 ) );
        assertTrue( group.isMember( usr1 ) );
        assertTrue( group.isMember( usr2 ) );
        assertFalse( group.isMember( usr3 ) );
    }

    @Test
    public void addInvalidUser() throws RestAuthException {
        Group group = Group.create(conn, group_1);
        try {
            group.addUser(user_1);
            fail();
        } catch (ResourceNotFound ex ) {
            assertEquals( "user", ex.getType() );
            assertEquals( 0, group.getUsers().size() );
        }
    }

    @Test
    public void addUserToInvalidGroup() throws RestAuthException {
        Group group = new Group( conn, group_1);
        User usr1 = User.create(conn, user_1, "password");
        try {
            group.addUser( usr1 );
            fail();
        } catch ( ResourceNotFound ex ) {
            assertEquals( "group", ex.getType() );
            assertEquals( 0, Group.getAll( conn ).size() );
        }
    }

    @Test
    public void removeUser() throws RestAuthException {
        Group group = Group.create(conn, group_1);
        User usr1 = User.create(conn, user_1, "password");
        group.addUser(usr1);
        assertTrue( group.isMember(usr1));

        group.removeUser(usr1);
        assertFalse( group.isMember(usr1));
    }

    @Test
    public void removeNotMember() throws RestAuthException {
        Group group = Group.create(conn, group_1);
        User usr1 = User.create(conn, user_1, "password");

        assertFalse( group.isMember(usr1));
        try {
            group.removeUser(usr1);
            fail();
        } catch ( ResourceNotFound ex ) {
            assertEquals( "user", ex.getType() );
        }
    }

    @Test
    public void removeInvalidUser() throws RestAuthException {
        Group group = Group.create(conn, group_1);
        User usr1 = new User(conn, user_1 );

        try {
            group.removeUser(usr1);
            fail();
        } catch ( ResourceNotFound ex ) {
            assertEquals( "user", ex.getType() );
        }
    }

    @Test
    public void removeUserFromInvalidGroup() throws RestAuthException {
        Group group = new Group( conn, group_1 );
        User user = User.create(conn, user_1, "password");

        try {
            group.removeUser(user_1);
        } catch ( ResourceNotFound ex ) {
            assertEquals( "group", ex.getType());
        }
    }

    @Test
    public void addGroup() throws RestAuthException {
        Group group1 = Group.create(conn, group_1);
        Group group2 = Group.create(conn, group_2);
        User user1 = User.create(conn, user_1, "password");
        User user2 = User.create(conn, user_2, "password");

        group1.addUser(user_1);
        group2.addUser(user_2);
        List<User> users1 = group1.getUsers();
        assertEquals( 1, users1.size() );
        assertEquals( user1, users1.get(0));
        assertTrue( group1.isMember(user_1) );
        List<User> users2 = group2.getUsers();
        assertEquals( 1, users2.size() );
        assertEquals( user2, users2.get(0));

        // now the really interesting part:
        group1.addGroup(group2);

        // check if the listed child-groups make sense
        List<Group> groups1 = group1.getGroups();
        assertEquals( 1, groups1.size() );
        assertEquals( group2, groups1.get(0));
        List<Group> groups2 = group2.getGroups();
        assertEquals( 0, groups2.size() );

        // check if the inherited membership works:
        users1 = group1.getUsers();
        assertEquals( 1, users1.size() );
        assertEquals( user1, users1.get(0));
        assertTrue( group1.isMember(user_1) );
        assertFalse( group1.isMember(user_2) );

        users2 = group2.getUsers();
        assertEquals( 2, users2.size() );
        assertTrue( users2.contains(user1) );
        assertTrue( users2.contains(user2) );
        assertTrue( group2.isMember(user1) );
        assertTrue( group2.isMember(user2) );
    }

    @Test
    public void addInvalidGroupToGroup() throws RestAuthException {
        Group group1 = Group.create(conn, group_1);

        try {
            group1.addGroup( group_2 );
            fail();
        } catch ( ResourceNotFound ex ) {
            assertEquals( "group", ex.getType() );
            assertEquals( 0, group1.getGroups().size() );
            assertEquals( 0, Group.getAll(conn).size() );
        }

        try {
            Group.get(conn, group_2);
        } catch ( ResourceNotFound ex ) {
            assertEquals( "group", ex.getType() );
        }
    }
}
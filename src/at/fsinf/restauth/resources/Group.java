package at.fsinf.restauth.resources;

import at.fsinf.restauth.common.RestAuthConnection;
import at.fsinf.restauth.common.RestAuthResponse;
import at.fsinf.restauth.errors.GroupExists;
import at.fsinf.restauth.errors.InternalServerError;
import at.fsinf.restauth.errors.PreconditionFailed;
import at.fsinf.restauth.errors.RequestFailed;
import at.fsinf.restauth.errors.ResourceNotFound;
import at.fsinf.restauth.errors.Unauthorized;
import at.fsinf.restauth.errors.UnknownStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpStatus;

/**
 * An instance of the Group class represents a group in a RestAuth service.
 *
 * @author Mathias Ertl
 */
public class Group extends Resource {
    /**
     * All requests use this prefix.
     */
    protected static String prefix = "/groups/";

    /**
     * A simple constructor.
     *
     * Note that merely instantiating this class via the constructor does not
     * verify that this group actually exists, you can use the {@link #get get
     * method} if you want to be sure that the group exists.
     *
     * @param connection The connection to use when making requests.
     * @param name The name of the group.
     */
    public Group( RestAuthConnection connection, String name ) {
        super( connection, name );
    }

    /**
     * Getter for this groups name.
     *
     * @return The name of this group.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Factory method that creates a new group.
     *
     * @param connection The connection to use when making requests.
     * @param name The name of the group.
     * @return The newly created group.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     * @throws GroupExists
     * @throws PreconditionFailed
     */
    public static Group create( RestAuthConnection connection, String name )
            throws Unauthorized, InternalServerError, RequestFailed, GroupExists, PreconditionFailed {
        Map<String, String> params = new HashMap<String, String>();
        params.put( "group", name );

        RestAuthResponse response = connection.post( Group.prefix, params );
        int respCode = response.getStatusCode();

        if ( respCode == HttpStatus.SC_CREATED ) {
            return new Group( connection, name );
        } else if ( respCode == HttpStatus.SC_CONFLICT ) {
            throw new GroupExists( response );
        } else if ( respCode == HttpStatus.SC_PRECONDITION_FAILED ) {
            throw new PreconditionFailed( response );
        } else {
            throw new UnknownStatus( response );
        }
    }

    /**
     * Factory method that gets all users currently known to RestAuth.
     *
     * @param connection The connection to use when making requests.
     * @return A list of all users known to RestAuth.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public static List<Group> getAll( RestAuthConnection connection )
            throws Unauthorized, InternalServerError, RequestFailed {
        RestAuthResponse response = connection.get( Group.prefix );
        int respCode = response.getStatusCode();

        if ( respCode == HttpStatus.SC_OK ) {
            String raw_body = response.getBody();
            List<String> names = connection.handler.unmarshal_list(raw_body);
            List<Group> groups = new ArrayList<Group>();
            for ( String name : names ) {
                groups.add( new Group( connection, name ) );
            }

            return groups;
        } else {
            throw new UnknownStatus( response );
        }
    }

    /**
     * Factory method that verifies that the user in question actually exists.
     *
     * @param connection The connection to use when making requests.
     * @param name The name of the group.
     * @return The group, guaranteed to exist in this moment.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     * @throws ResourceNotFound If the group in question does not exist.
     */
    public static Group get( RestAuthConnection connection, String name )
            throws Unauthorized, InternalServerError, RequestFailed, ResourceNotFound {
        String path = String.format( "%s%s/", Group.prefix, name );
        RestAuthResponse response = connection.get( path );
        int respCode = response.getStatusCode();

        if ( respCode == HttpStatus.SC_NO_CONTENT ) {
            return new Group( connection, name );
        } else if ( respCode == HttpStatus.SC_NOT_FOUND ) {
            throw new ResourceNotFound( response );
        } else {
            throw new UnknownStatus( response );
        }
    }

    /**
     * Get all users currently in this group. This includes inherited group
     * memberships.
     *
     * @return The users currently in this group.
     * @throws ResourceNotFound If the group in question does not exist.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public List<User> getUsers() 
            throws ResourceNotFound, Unauthorized, InternalServerError, RequestFailed {
        String path = String.format( "%s/users/", this.name );
        RestAuthResponse response = this.get( path );
        int respCode = response.getStatusCode();

        if ( respCode == HttpStatus.SC_OK ) {
            String raw_body = response.getBody();
            List<String> names = this.conn.handler.unmarshal_list(raw_body);
            List<User> users = new ArrayList<User>();
            for ( String username : names ) {
                users.add( new User( this.conn, username ) );
            }

            return users;
        } else if ( respCode == HttpStatus.SC_NOT_FOUND ) {
            throw new ResourceNotFound( response );
        } else {
            throw new UnknownStatus( response );
        }
    }

    /**
     * Add a user to this group.
     *
     * @param user The user to add.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     * @throws ResourceNotFound If the group in question does not exist.
     */
    public void addUser( User user ) 
            throws Unauthorized, InternalServerError, RequestFailed, ResourceNotFound {
        this.addUser( user.name );
    }

    /**
     * Add a user to this group.
     *
     * @param username The name of the user to add.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     * @throws ResourceNotFound If the group in question does not exist.
     */
    public void addUser( String username ) 
            throws Unauthorized, InternalServerError, RequestFailed, ResourceNotFound {
        Map<String, String> params = new HashMap<String, String>();
        params.put( "user", username );

        String path = String.format( "%s/users/", this.name );
        RestAuthResponse response = this.post( path, params );
        int respCode = response.getStatusCode();

        if( respCode == HttpStatus.SC_NO_CONTENT ) {
            return;
        } else if ( respCode == HttpStatus.SC_NOT_FOUND ) {
            throw new ResourceNotFound( response );
        } else {
            throw new UnknownStatus( response );
        }
    }

    /**
     * Check if the given user is a member of this group. This will also return
     * true if the membership is inherited from another group.
     *
     * @param user The user in question.
     * @return true if the user is a member of the group, false otherwise.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     * @throws ResourceNotFound If the group in question does not exist.
     */
    public boolean isMember( User user ) 
            throws Unauthorized, InternalServerError, RequestFailed, ResourceNotFound {
        return this.isMember( user.name );
    }

    /**
     * Check if the user with the given username is a member of this group. This
     * will also return true if the membership is inherited from another group.
     *
     * @param username The username of the user in question.
     * @return true if the user is a member of the group, false otherwise.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     * @throws ResourceNotFound If the group in question does not exist.
     */
    public boolean isMember( String username ) 
            throws Unauthorized, InternalServerError, RequestFailed, ResourceNotFound {
        String path = String.format( "%s/users/%s/", this.name, username );
        RestAuthResponse response = this.get( path );
        int respCode = response.getStatusCode();

        if ( respCode == HttpStatus.SC_NO_CONTENT ) {
            return true;
        } else if ( respCode == HttpStatus.SC_NOT_FOUND ) {
            String type = response.getHeader( "Resource-Type" );
            if ( type.equals( "user") ) {
                return false;
            } else {
                throw new ResourceNotFound( response );
            }
        } else {
            throw new UnknownStatus( response );
        }
    }

    /**
     * Remove a user from this group.
     *
     * Note that if the RestAuth server implements <a
     * href="http://fs.fsinf.at/wiki/RestAuth/Specification#Meta-groups">
     * meta-groups</a> this method might throw a {@link ResourceNotFound} even
     * if {@link #isMember(at.fsinf.restauth.resources.User) isMember} for this
     * particular user returns true, because the user might be a member of a
     * meta-group.
     *
     * If the RestAuth server implements <a
     * href="http://fs.fsinf.at/wiki/RestAuth/Specification#Group_visibility"
     * group visibility</a> it might even not be possible for you to remove the
     * membership of the user in question using the current credentials.
     *
     * @param user The user to be removed from this group.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws ResourceNotFound If the group in question does not exist.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public void removeUser( User user )
            throws Unauthorized, InternalServerError, ResourceNotFound, RequestFailed {
        this.removeUser( user.name );
    }

    /**
     * Remove a user with the given username from this group.
     * 
     * Please also see the documentation of {@link 
     * #removeUser(at.fsinf.restauth.resources.User)} on problems for this
     * method.
     *
     * @param username The username to remove.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws ResourceNotFound If the group in question does not exist.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public void removeUser( String username ) 
            throws Unauthorized, InternalServerError, ResourceNotFound, RequestFailed {
        String path = String.format( "%s/users/%s/", this.name, username );
        RestAuthResponse response = this.delete( path );
        int respCode = response.getStatusCode();

        if ( respCode == HttpStatus.SC_NO_CONTENT ) {
            return;
        } else if ( respCode == HttpStatus.SC_NOT_FOUND ) {
            throw new ResourceNotFound( response );
        } else {
            throw new UnknownStatus( response );
        }
    }

    /**
     * Get all sub-groups of this group.
     *
     * @return The sub-groups of this group.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     * @throws ResourceNotFound If the group in question does not exist.
     */
    public List<Group> getGroups() 
            throws Unauthorized, InternalServerError, RequestFailed, ResourceNotFound {
        String path = String.format( "%s/groups/", this.name );
        RestAuthResponse response = this.get( path );
        int respCode = response.getStatusCode();

        if ( respCode == HttpStatus.SC_OK ) {
            String raw_body = response.getBody();
            List<String> names = this.conn.handler.unmarshal_list(raw_body);
            List<Group> groups = new ArrayList<Group>();
            for ( String groupname : names ) {
                groups.add( new Group( this.conn, groupname ) );
            }

            return groups;
        } else if ( respCode == HttpStatus.SC_NOT_FOUND ) {
            throw new ResourceNotFound( response );
        } else {
            throw new UnknownStatus( response );
        }
    }

    /**
     * Make the subgroup subgroup to a sub-subgroup of this subgroup, making
     * this subgroup a meta-subgroup of the given subgroup.
     *
     * @param subgroup The subgroup that is to become a sub-subgroup of this subgroup.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     * @throws ResourceNotFound If the subgroup in question does not exist.
     */
    public void addGroup( Group subgroup )
            throws Unauthorized, InternalServerError, RequestFailed, ResourceNotFound {
        this.addGroup( subgroup.name );
    }

    /**
     * Make the group with the name subgroupname to a sub-group of this group,
     * making this group a meta-group of the named group.
     *
     * @param subgroupname The name of the group that is to become a sub-group
     *      of this group.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     * @throws ResourceNotFound If the group in question does not exist.
     */
    public void addGroup( String subgroupname )
            throws Unauthorized, InternalServerError, RequestFailed, ResourceNotFound {
        Map<String, String> params = new HashMap<String, String>();
        params.put( "group", subgroupname );

        String path = String.format( "%s/groups/", this.name );
        RestAuthResponse response = this.post( path, params );
        int respCode = response.getStatusCode();

        if( respCode == HttpStatus.SC_NO_CONTENT ) {
            return;
        } else if ( respCode == HttpStatus.SC_NOT_FOUND ) {
            throw new ResourceNotFound( response );
        } else {
            throw new UnknownStatus( response );
        }
    }

    /**
     * Remove a subgroup from this group.
     *
     * @param subgroup The subgroup to be removed.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     * @throws ResourceNotFound If the subgroup in question does not exist.
     */
    public void removeGroup( Group subgroup )
            throws Unauthorized, InternalServerError, RequestFailed, ResourceNotFound {
        this.removeGroup( subgroup.name );
    }

    /**
     * Remove a subgroup from this group.
     *
     * @param subgroupname The name of the subgroup to remove.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     * @throws ResourceNotFound If the group in question does not exist.
     */
    public void removeGroup( String subgroupname )
            throws Unauthorized, InternalServerError, RequestFailed, ResourceNotFound {
        String path = String.format( "%s/groups/%s/", this.name, subgroupname );
        RestAuthResponse response = this.delete( path );
        int respCode = response.getStatusCode();

        if ( respCode == HttpStatus.SC_NO_CONTENT ) {
            return;
        } else if ( respCode == HttpStatus.SC_NOT_FOUND ) {
            throw new ResourceNotFound( response );
        } else {
            throw new UnknownStatus( response );
        }
    }

    /**
     * Remove the entire group.
     *
     * @throws ResourceNotFound If the group in question does not exist.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public void remove()
            throws ResourceNotFound, Unauthorized, InternalServerError, RequestFailed {
        String path = String.format( "%s/", this.name );
        RestAuthResponse response = this.delete( path );
        int respCode = response.getStatusCode();

        if ( respCode == HttpStatus.SC_NO_CONTENT ) {
            return;
        } else if ( respCode == HttpStatus.SC_NOT_FOUND ) {
            throw new ResourceNotFound( response );
        } else {
            throw new UnknownStatus( response );
        }
    }

    /**
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     * @inherit
     */
    @Override
    protected RestAuthResponse get( String path )
            throws Unauthorized, InternalServerError, RequestFailed {
        return this.conn.get( Group.prefix + path );
    }

    /**
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     * @inherit
     */
    @Override
    protected RestAuthResponse get( String path, Map<String, String> params )
            throws Unauthorized, InternalServerError, RequestFailed {
        return this.conn.get( Group.prefix + path, params );
    }

    /**
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     * @inherit
     */
    @Override
    protected RestAuthResponse post( String path, Map<String, String> params )
            throws Unauthorized, InternalServerError, RequestFailed {
        return this.conn.post( Group.prefix + path, params );
    }

    /**
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @inherit
     */
    @Override
    protected RestAuthResponse put( String path, Map<String, String> params )
            throws Unauthorized, InternalServerError, RequestFailed {
        return this.conn.put( Group.prefix + path, params );
    }

    /**
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @inherit
     */
    @Override
    protected RestAuthResponse delete( String path )
            throws Unauthorized, InternalServerError, RequestFailed {
        return this.conn.delete( Group.prefix + path );
    }

    /**
     * Two instances of the class Group evaluate as equal if their name and
     * connection evaluate as equal.
     *
     * @param other The group to compare.
     * @return True if the groups are equal, false otherwise.
     */
    @Override
    public boolean equals( Object other ) {
        if ( ! (other instanceof Group) ) return false;
        Group o = (Group) other;
        return o.name.equals( this.name ) && this.conn.equals( o.conn );
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() * 3 * this.conn.hashCode();
    }
}

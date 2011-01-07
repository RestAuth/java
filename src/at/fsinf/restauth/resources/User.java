/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.fsinf.restauth.resources;

import at.fsinf.restauth.common.RestAuthConnection;
import at.fsinf.restauth.common.RestAuthResponse;
import at.fsinf.restauth.errors.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpStatus;

/**
 * An instance of the User class represents a user in the RestAuth service.
 * 
 * @author Mathias Ertl
 */
public class User extends Resource {
    private static String prefix = "/users/";

    /**
     * Simple constructor.
     *
     * Note that merely instantiating this class via the constructor does not verify
     * that this user actually exists, you can use the {@link #get get method} 
     * if you want to be sure that the user exists.
     *
     * @param connection The connection to use when making request.
     * @param name The name of the user.
     */
    public User( RestAuthConnection connection, String name ) {
        super( connection, name );
    }

    /**
     * Factory method that verifies that the user in question actually exists.
     *
     * @param connection The connection to use when making requests.
     * @param name The name of the user.
     * @return The user, guaranteed to exist in this moment.
     * @throws ResourceNotFound If the user in question does not exist.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws UnknownStatus The RestAuth server responded with an unknown
     *      status.
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public static User get( RestAuthConnection connection, String name )
            throws ResourceNotFound, Unauthorized, UnknownStatus, NotAcceptable, InternalServerError, RequestFailed {
        String path = String.format( "%s%s/", User.prefix, name );
        RestAuthResponse response = connection.get( path );
        int respCode = response.getStatusCode();
        
        if ( respCode == HttpStatus.SC_NO_CONTENT ) {
            return new User( connection, name );
        } else if ( respCode == HttpStatus.SC_NOT_FOUND ) {
            throw new ResourceNotFound( response );
        } else {
            throw new UnknownStatus( response );
        }
    }

    /**
     * Factory method that gets all users currently known to RestAuth.
     *
     * @param connection The connection to use when making requests.
     * @return A list of all users known to RestAuth
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws UnknownStatus The RestAuth server responded with an unknown
     *      status.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public static List<User> getAll( RestAuthConnection connection )
            throws Unauthorized, UnknownStatus, InternalServerError, NotAcceptable, RequestFailed {
        RestAuthResponse response = connection.get( User.prefix );
        int respCode = response.getStatusCode();
        if ( respCode == HttpStatus.SC_OK ) {
            String raw_body = response.getBody();
            List<String> names = connection.handler.unmarshal_list(raw_body);
            List<User> users = new ArrayList<User>();
            for ( String name : names ) {
                users.add( new User( connection, name ) );
            }

            return users;
        } else {
            throw new UnknownStatus( response );
        }
    }

    /**
     * Factory method that creates a new user.
     *
     * @param connection The connection to use when making requests.
     * @param name The name of the user.
     * @param passwd The password of the user.
     * @return The newly created user.
     * @throws UserExists If the user already exists.
     * @throws PreconditionFailed If the username or password are not acceptable
     *      to the RestAuth server.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws UnknownStatus The RestAuth server responded with an unknown
     *      status.
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public static User create( RestAuthConnection connection, String name, String passwd )
            throws UserExists, PreconditionFailed, Unauthorized, UnknownStatus, NotAcceptable, InternalServerError, RequestFailed {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put( "user", name );
        params.put( "password", passwd );
        RestAuthResponse response = connection.post( User.prefix, params );
        int respCode = response.getStatusCode();

        if ( respCode == HttpStatus.SC_CREATED ) {
            return new User( connection, name );
        } else if ( respCode == HttpStatus.SC_CONFLICT ) {
            throw new UserExists( response );
        } else if ( respCode == HttpStatus.SC_PRECONDITION_FAILED ) {
            throw new PreconditionFailed( response );
        } else {
            throw new UnknownStatus( response );
        }
    }
    
    /**
     * Set the password of this user.
     *
     * @param newPassword The new password.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws ResourceNotFound If the user in question does not exist.
     * @throws UnknownStatus The RestAuth server responded with an unknown
     *      status.
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public void setPassword( String newPassword )
            throws Unauthorized, ResourceNotFound, UnknownStatus, NotAcceptable, InternalServerError, RequestFailed {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put( "password", newPassword );

        String path = String.format( "%s/", this.name );
        RestAuthResponse response = this.put( path, params );
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
     * Verify the password for this user.
     *
     * Note that this method does not distinguish between a false password and a
     * user that does not exist.
     *
     * @param password The password to verify.
     * @return true if the password is correct, false otherwise.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws UnknownStatus The RestAuth server responded with an unknown
     *      status.
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public boolean verifyPassword( String password )
            throws Unauthorized, UnknownStatus, NotAcceptable, InternalServerError, RequestFailed {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put( "password", password );

        String path = String.format( "%s/", this.name );
        RestAuthResponse response = this.post( path, params );
        int respCode = response.getStatusCode();

        if ( respCode == HttpStatus.SC_NO_CONTENT ) {
            return true;
        } else if ( respCode == HttpStatus.SC_NOT_FOUND ) {
            return false;
        } else {
            throw new UnknownStatus( response );
        }
    }

    /**
     * Get all properties of this user.
     *
     * @return A map of the properties of this user.
     * @throws ResourceNotFound If the user in question does not exist.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws UnknownStatus The RestAuth server responded with an unknown
     *      status.
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public Map<String, String> getProperties()
            throws ResourceNotFound, Unauthorized, UnknownStatus, NotAcceptable, InternalServerError, RequestFailed {
        String path = String.format( "%s/props/", this.name );
        RestAuthResponse response = this.get( path );
        int respCode = response.getStatusCode();

        if ( respCode == HttpStatus.SC_OK ) {
            String body = response.getBody();
            return conn.handler.unmarshal_dictionary(body);
        } else if ( respCode == HttpStatus.SC_NOT_FOUND ) {
            throw new ResourceNotFound( response );
        } else {
            throw new UnknownStatus( response );
        }
    }

    /**
     * Create a new property of this user. It is an error if this property
     * already exists.
     *
     * @param propName The name of the new property.
     * @param value The value of the new property.
     * @throws PropertyExists If the property already exists.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws ResourceNotFound If the user in question does not exist.
     * @throws UnknownStatus The RestAuth server responded with an unknown
     *      status.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public void createProperty( String propName, String value )
            throws PropertyExists, Unauthorized, ResourceNotFound, UnknownStatus, InternalServerError, NotAcceptable, RequestFailed {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put( "prop", propName );
        params.put( "value", value );

        String path = String.format( "%s/props/", this.name );
        RestAuthResponse response = this.post( path, params );
        int respCode = response.getStatusCode();

        if ( respCode == HttpStatus.SC_CREATED ) {
            return;
        } else if ( respCode == HttpStatus.SC_NOT_FOUND ) {
            throw new ResourceNotFound( response );
        } else if ( respCode == HttpStatus.SC_CONFLICT ) {
            throw new PropertyExists( response );
        } else {
            throw new UnknownStatus( response );
        }
    }

    /**
     * Create/overwrite a property for this user.
     *
     * @param propName The name of the property.
     * @param value The value of the property.
     * @return The previous value of the property if it already existed, null
     *      otherwise.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws ResourceNotFound If the user in question does not exist.
     * @throws UnknownStatus The RestAuth server responded with an unknown
     *      status.
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public String setProperty( String propName, String value )
            throws Unauthorized, ResourceNotFound, UnknownStatus, NotAcceptable, InternalServerError, RequestFailed {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put( "value", value );

        String path = String.format( "%s/props/%s/", this.name, propName );
        RestAuthResponse response = this.put( path, params );
        int respCode = response.getStatusCode();

        if ( respCode == HttpStatus.SC_OK ) {
             return this.conn.handler.unmarshal_string( response.getBody() );
        } else if ( respCode == HttpStatus.SC_CREATED ) {
            return null;
        } else if ( respCode == HttpStatus.SC_NOT_FOUND ) {
            throw new ResourceNotFound( response );
        } else {
            throw new UnknownStatus( response );
        }
    }

    /**
     * Get a specific property of this user.
     *
     * @param propName
     * @return The value of the named property.
     * @throws ResourceNotFound If the user or property in question does not
     *      exist.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws UnknownStatus The RestAuth server responded with an unknown
     *      status.
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public String getProperty( String propName )
            throws ResourceNotFound, Unauthorized, UnknownStatus, NotAcceptable, InternalServerError, RequestFailed {
        String path = String.format( "%s/props/%s/", this.name, propName );
        RestAuthResponse response = this.get( path );
        int respCode = response.getStatusCode();

        if ( respCode == HttpStatus.SC_OK ) {
             return this.conn.handler.unmarshal_string( response.getBody() );
        } else if ( respCode == HttpStatus.SC_NOT_FOUND ) {
            throw new ResourceNotFound( response );
        } else {
            throw new UnknownStatus( response );
        }
    }

    /**
     * Remove a property of this user.
     *
     * @param propName The property to remove.
     * @throws ResourceNotFound If the user or property in question does not
     *      exist.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws UnknownStatus The RestAuth server responded with an unknown
     *      status.
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public void removeProperty( String propName )
            throws ResourceNotFound, Unauthorized, UnknownStatus, NotAcceptable, InternalServerError, RequestFailed {
        String path = String.format( "%s/props/%s/", this.name, propName );
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
     * Get all groups that this user is currently a member of.
     *
     * @return The groups that this user is a member of.
     * @throws ResourceNotFound If the user or property in question does not
     *      exist.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws UnknownStatus The RestAuth server responded with an unknown
     *      status.
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public List<Group> getGroups()
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed, ResourceNotFound, UnknownStatus {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put( "user", this.name );
        RestAuthResponse response = this.conn.get( Group.prefix, params );
        int respCode = response.getStatusCode();

        if ( respCode == HttpStatus.SC_OK ) {
             List<String> groupnames =
                     this.conn.handler.unmarshal_list( response.getBody() );
             List<Group> groups = new ArrayList<Group>();
             for( String groupname : groupnames ) {
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
     * Shortcut for {@link Group#isMember isMember}.
     *
     * @param group
     * @return
     * @throws ResourceNotFound If the user or property in question does not
     *      exist.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws UnknownStatus The RestAuth server responded with an unknown
     *      status.
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public boolean inGroup( Group group )
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed, ResourceNotFound, UnknownStatus {
        return group.isMember( this.name );
    }

    /**
     * Shortcut for {@link Group#isMember isMember}.
     *
     * @param groupname
     * @return
     * @throws ResourceNotFound If the user or property in question does not
     *      exist.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws UnknownStatus The RestAuth server responded with an unknown
     *      status.
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public boolean inGroup( String groupname )
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed, ResourceNotFound, UnknownStatus {
        Group group = new Group( this.conn, groupname );
        return group.isMember(this.name);
    }

    /**
     * Shortcut for {@link Group#addUser addUser}.
     *
     * @param group
     * @throws ResourceNotFound If the user or property in question does not
     *      exist.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws UnknownStatus The RestAuth server responded with an unknown
     *      status.
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public void addGroup( Group group )
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed, ResourceNotFound, UnknownStatus {
        group.addUser( this.name );
    }

    /**
     * Shortcut for {@link Group#addUser addUser}.
     *
     * @param groupname
     * @throws ResourceNotFound If the user or property in question does not
     *      exist.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws UnknownStatus The RestAuth server responded with an unknown
     *      status.
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public void addGroup( String groupname )
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed, ResourceNotFound, UnknownStatus {
        Group group = new Group( this.conn, groupname );
        group.addUser( this.name );
    }

    /**
     * Shortcut for {@link Group#removeUser removeUser}.
     * 
     * @param groupname
     * @throws ResourceNotFound If the user or property in question does not
     *      exist.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws UnknownStatus The RestAuth server responded with an unknown
     *      status.
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public void removeGroup( String groupname )
            throws NotAcceptable, Unauthorized, InternalServerError, ResourceNotFound, UnknownStatus, RequestFailed {
        Group group = new Group( this.conn, groupname );
        group.removeUser( this.name );
    }

    /**
     * Shortcut for {@link Group#removeUser removeUser}.
     *
     * @param group
     * @throws ResourceNotFound If the user or property in question does not
     *      exist.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws UnknownStatus The RestAuth server responded with an unknown
     *      status.
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public void removeGroup( Group group )
            throws NotAcceptable, Unauthorized, InternalServerError, ResourceNotFound, UnknownStatus, RequestFailed {
        group.removeUser( this.name );
    }

    /**
     * Remove this user completely.
     *
     * @throws ResourceNotFound If the user in question does not exist.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @throws UnknownStatus The RestAuth server responded with an unknown
     *      status.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     */
    public void remove()
            throws ResourceNotFound, Unauthorized, NotAcceptable, InternalServerError, UnknownStatus, RequestFailed {
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
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @inherit
     */
    @Override
    protected RestAuthResponse get( String path )
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed {
        return this.conn.get( User.prefix + path );
    }

    /**
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @inherit
     */
    @Override
    protected RestAuthResponse get( String path, Map<String, String> params )
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed {
        return this.conn.get( User.prefix + path, params );
    }

    /**
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @inherit
     */
    @Override
    protected RestAuthResponse post(String path, Map<String, String> params )
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed {
        return this.conn.post( User.prefix + path, params );
    }

    /**
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @inherit
     */
    @Override
    protected RestAuthResponse put(String path, Map<String, String> params )
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed {
        return this.conn.put( User.prefix + path, params );
    }


    /**
     * @throws NotAcceptable If the RestAuth server is not enable to generate a
     *      response a format that is understood by the content handler.
     * @throws Unauthorized If the authentication credentials are wrong.
     * @throws RequestFailed If making the request failed (that is, never
     *      reached the RestAuth server).
     * @throws InternalServerError If the RestAuth server suffered from an
     *      internal error.
     * @inherit
     */
    @Override
    public RestAuthResponse delete( String path )
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed {
        return this.conn.delete( User.prefix + path );
    }

    /**
     * Getter for this users name.
     *
     * @return The name of this user.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Two instances of the class User evaluate as equal if their name and
     * connection evaluate as equal.
     *
     * @param other The user to compare.
     * @return True if the users are equal, false otherwise.
     */
    @Override
    public boolean equals( Object other ) {
        if ( ! (other instanceof User) ) return false;
        User o = (User) other;
        return o.name.equals( this.name ) && this.conn.equals( o.conn );
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() * 3 * this.conn.hashCode();
    }
}

package at.fsinf.restauth.resources;

import at.fsinf.restauth.common.RestAuthConnection;
import at.fsinf.restauth.common.RestAuthResponse;
import at.fsinf.restauth.errors.GroupExists;
import at.fsinf.restauth.errors.InternalServerError;
import at.fsinf.restauth.errors.NotAcceptable;
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
 *
 * @author mati
 */
public class Group extends Resource {
    private static String prefix = "/groups/";

    public Group( RestAuthConnection connection, String name ) {
        super( connection, name );
    }

    public static Group create( RestAuthConnection connection, String name )
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed, UnknownStatus, GroupExists, PreconditionFailed {
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

    public static List<Group> getAll( RestAuthConnection connection ) 
            throws UnknownStatus, NotAcceptable, Unauthorized, InternalServerError, RequestFailed {
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

    public static Group get( RestAuthConnection connection, String name )
            throws UnknownStatus, NotAcceptable, Unauthorized, InternalServerError, RequestFailed {
        String path = String.format( "%s%s/", Group.prefix, name );
        RestAuthResponse response = connection.get( path );
        int respCode = response.getStatusCode();

        if ( respCode == HttpStatus.SC_NO_CONTENT ) {
            return new Group( connection, name );
        } else {
            throw new UnknownStatus( response );
        }
    }
    
    public List<User> getUsers() 
            throws ResourceNotFound, UnknownStatus, NotAcceptable, Unauthorized, InternalServerError, RequestFailed {
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
        } else if ( respCode == HttpStatus.SC_OK ) {
            throw new ResourceNotFound( response );
        } else {
            throw new UnknownStatus( response );
        }
    }

    public void addUser( User user ) 
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed, ResourceNotFound, UnknownStatus {
        this.addUser( user.name );
    }

    public void addUser( String username ) 
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed, ResourceNotFound, UnknownStatus {
        Map<String, String> params = new HashMap<String, String>();
        params.put( "user", username );

        String path = String.format( "%s/users/", this.name );
        RestAuthResponse response = this.post( path, params );
        int respCode = response.getStatusCode();

        if( respCode == HttpStatus.SC_CREATED ) {
            return;
        } else if ( respCode == HttpStatus.SC_NOT_FOUND ) {
            throw new ResourceNotFound( response );
        } else {
            throw new UnknownStatus( response );
        }
    }

    public boolean isMember( User user ) 
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed, ResourceNotFound, UnknownStatus {
        return this.isMember( user.name );
    }

    public boolean isMember( String username ) 
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed, ResourceNotFound, UnknownStatus {
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

    public void removeUser( User user ) 
            throws NotAcceptable, Unauthorized, InternalServerError, ResourceNotFound, UnknownStatus, RequestFailed {
        this.removeUser( user.name );
    }

    public void removeUser( String username ) 
            throws NotAcceptable, Unauthorized, InternalServerError, ResourceNotFound, UnknownStatus, RequestFailed {
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

    public List<Group> getGroups() 
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed, ResourceNotFound, UnknownStatus {
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
        } else if ( respCode == HttpStatus.SC_OK ) {
            throw new ResourceNotFound( response );
        } else {
            throw new UnknownStatus( response );
        }
    }

    public void addGroup( Group group ) 
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed, ResourceNotFound, UnknownStatus {
        this.addGroup( group.name );
    }

    public void addGroup( String groupname ) 
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed, ResourceNotFound, UnknownStatus {
        Map<String, String> params = new HashMap<String, String>();
        params.put( "user", groupname );

        String path = String.format( "%s/groups/", this.name );
        RestAuthResponse response = this.post( path, params );
        int respCode = response.getStatusCode();

        if( respCode == HttpStatus.SC_CREATED ) {
            return;
        } else if ( respCode == HttpStatus.SC_NOT_FOUND ) {
            throw new ResourceNotFound( response );
        } else {
            throw new UnknownStatus( response );
        }
    }
    
    public void removeGroup( Group group ) 
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed, ResourceNotFound, UnknownStatus {
        this.removeGroup( group.name );
    }

    public void removeGroup( String groupname ) 
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed, ResourceNotFound, UnknownStatus {
        String path = String.format( "%s/groups/%s/", this.name, groupname );
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

    public void remove()
            throws ResourceNotFound, UnknownStatus, NotAcceptable, Unauthorized, InternalServerError, RequestFailed {
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

    @Override
    protected RestAuthResponse get( String path )
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed {
        return this.conn.get( Group.prefix + path );
    }

    @Override
    protected RestAuthResponse get( String path, Map<String, String> params )
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed {
        return this.conn.get( Group.prefix + path, params );
    }

    @Override
    protected RestAuthResponse post( String path, Map<String, String> params )
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed {
        return this.conn.get( Group.prefix + path, params );
    }

    @Override
    protected RestAuthResponse put( String path, Map<String, String> params )
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed {
        return this.conn.get( Group.prefix + path, params );
    }

    @Override
    protected RestAuthResponse delete( String path )
            throws NotAcceptable, Unauthorized, InternalServerError, RequestFailed {
        return this.conn.get( Group.prefix + path );
    }

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

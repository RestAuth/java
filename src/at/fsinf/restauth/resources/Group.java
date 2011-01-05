package at.fsinf.restauth.resources;

import at.fsinf.restauth.common.RestAuthConnection;

/**
 *
 * @author mati
 */
public class Group {
    private static String prefix = "/groups/";
    private RestAuthConnection conn;
    private String name;

    /**
     *
     * @param other
     * @return
     */
    @Override
    public boolean equals( Object other ) {
        if ( ! ( other instanceof Group ) ) return false;
        Group o = (Group) other;
        return o.name.equals( this.name ) && this.conn.equals( o.conn );
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        return this.name.hashCode() * 3 * this.conn.hashCode();
    }
}

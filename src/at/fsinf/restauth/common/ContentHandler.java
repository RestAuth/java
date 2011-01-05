/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.fsinf.restauth.common;

import java.util.List;
import java.util.Map;

/**
 *
 * @author mati
 */
public abstract class ContentHandler {
    /**
     *
     */
    protected String MimeType;

    /**
     *
     * @return
     */
    public String getMimeType() {
        return this.MimeType;
    }

    /**
     *
     * @param map
     * @return
     */
    public abstract String marshal_dictionary( Map<String, String> map );
    /**
     *
     * @param raw
     * @return
     */
    public abstract Map<String, String> unmarshal_dictionary( String raw );
    /**
     *
     * @param raw
     * @return
     */
    public abstract List<String> unmarshal_list( String raw );
    /**
     *
     * @param raw
     * @return
     */
    public abstract String unmarshal_string( String raw );
}

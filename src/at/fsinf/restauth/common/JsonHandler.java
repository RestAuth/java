package at.fsinf.restauth.common;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A handler capable of marshalling/unmarshalling Content from/to the JSON data
 * format.
 *
 * @author Mathias Ertl
 */
public class JsonHandler extends ContentHandler{
    private final Gson handler;
    private Type dictType = new TypeToken<Map<String, String>>() {}.getType();
    private Type listType = new TypeToken<List<String>>() {}.getType();

    /**
     * Default no-arg constructor.
     *
     * @Todo this.MimeType should be static
     */
    public JsonHandler() {
        this.MimeType = "application/json";
        this.handler = new Gson();
    }

    /**
     * Marshal a Map into a JSON dictionary.
     *
     * @param map The map to marshal
     * @return The JSON string representation of the map.
     */
    @Override
    public String marshal_dictionary( Map<String, String> map ) {
        return this.handler.toJson(map);
    }

    /**
     * Unmarshal a JSON dictionary into a map.
     *
     * @param raw The raw JSON representation of the dictionary.
     * @return The unmarshalled map.
     */
    @Override
    public HashMap<String, String> unmarshal_dictionary(String raw) {
        return this.handler.fromJson(raw, this.dictType );
    }

    /**
     * Unmarshal a JSON string into a Java string.
     *
     * @param raw The raw JSON representation of the string
     * @return The unmarshalled string.
     */
    @Override
    public String unmarshal_string(String raw) {
        return this.handler.fromJson(raw, String.class );
    }

    /**
     * Unmarshal a JSON list into a Java list
     *
     * @param raw The raw JSON representation of the list
     * @return The unmarshalled list.
     */
    @Override
    public List<String> unmarshal_list(String raw) {
        return this.handler.fromJson( raw, this.listType );
    }
}

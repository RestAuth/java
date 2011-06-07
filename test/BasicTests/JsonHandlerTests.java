package BasicTests;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import at.fsinf.restauth.common.JsonHandler;
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
public class JsonHandlerTests {
    private final JsonHandler handler;

    public JsonHandlerTests() {
        this.handler = new JsonHandler();
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

    @Test
    public void unmarshal_string() {
        String unmarshalled = this.handler.unmarshal_string( "\"foobar\"" );
        assertEquals( "foobar", unmarshalled );

        unmarshalled = this.handler.unmarshal_string( "\"foo bar\"" );
        assertEquals( "foo bar", unmarshalled );
    }

    public void unmarshal_list() {
        List<String> unmarshalled =
                this.handler.unmarshal_list( "[\"foo\", \"bar hugo\"]" );
        assertEquals( 2, unmarshalled.size() );
        assertEquals( "foo", unmarshalled.get(0));
        assertEquals( "bar hugo", unmarshalled.get(1));
    }

    @Test
    public void unmarshal_map() {
        String raw_map = "{\"foo\":\"bar\",\"key space\":\"whatever\"}";
        HashMap<String, String> unmarshalled =
                this.handler.unmarshal_dictionary(raw_map);
        assertEquals( 2, unmarshalled.size() );
        assertTrue( unmarshalled.containsKey("foo") );
        assertTrue( unmarshalled.containsKey("key space") );

        assertEquals( "bar", unmarshalled.get("foo") );
        assertEquals( "whatever", unmarshalled.get("key space") );
    }

    @Test
    public void marshal_map() {
        Map<String, String> map = new HashMap<String, String>();
        map.put( "foo", "bar" );
        map.put( "key space", "whatever" );
        String unmarshalled = this.handler.marshal_dictionary( map );
        assertEquals( "{\"foo\":\"bar\",\"key space\":\"whatever\"}",
                unmarshalled );
    }
}
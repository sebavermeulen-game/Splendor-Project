package be.howest.ti.game.web.views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonViewTest {

    @Test
    void testJsonView() throws JsonProcessingException {
        JsonView<String> jsonView = new JsonView<>("test", Map.of(
                "word", x->x,
                "length", String::length));

        ObjectMapper json = new ObjectMapper();

        String txt = json.writeValueAsString(jsonView);
        JsonNode node = json.readTree(txt);

        assertEquals("test", node.get("word").asText());
        assertEquals(4, node.get("length").asInt());
    }

}
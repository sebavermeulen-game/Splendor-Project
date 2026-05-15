package be.howest.ti.game.web.views;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JsonView<T> {

    private final T source;
    public final Map<String, Function<T,Object>> mapper;

    public JsonView(T source, Map<String, Function<T, Object>> mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    @JsonValue
    private Map<String, Object> asMap() {
        return mapper.keySet().stream().collect(
                Collectors.toMap(
                        key -> key,
                        this::map
                )
        );
    }

    private Object map(String key) {
        return mapper.get(key).apply(source);
    }
}

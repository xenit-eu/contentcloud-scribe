package eu.xenit.contentcloud.scribe.generator.properties;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ApplicationProperties {

    private final Map<String, String> properties = new LinkedHashMap<>();

    public ApplicationProperties put(String key, Object value) {
        this.properties.put(key, String.valueOf(value));
        return this;
    }

    public void forEach(BiConsumer<String, String> action) {
        this.properties.forEach(action);
    }

}

package eu.xenit.contentcloud.scribe.generator.openapi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OpenApiObjectDataType extends OpenApiDataType {

    @Getter
    private final Map<String, OpenApiSchema> properties = new LinkedHashMap<>();

    @Getter @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<String> required = new LinkedList<>();

    public OpenApiObjectDataType() {
        super("object", null);
    }

    public OpenApiObjectDataType(Map<String, OpenApiSchema> properties) {
        this();
        this.properties.putAll(properties);
    }
}

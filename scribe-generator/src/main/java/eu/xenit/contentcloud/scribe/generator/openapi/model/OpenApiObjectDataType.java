package eu.xenit.contentcloud.scribe.generator.openapi.model;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

public class OpenApiObjectDataType extends OpenApiDataType {
    @Getter
    private final Map<String, OpenApiSchema> properties = new LinkedHashMap<>();

    public OpenApiObjectDataType() {
        super("object");
    }

    public OpenApiObjectDataType(Map<String, OpenApiSchema> properties) {
        this();
        this.properties.putAll(properties);
    }
}

package eu.xenit.contentcloud.scribe.generator.openapi.model;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter @Setter
public class OpenApiComponents {
    private final Map<String, OpenApiSchema> schemas = new LinkedHashMap<>();
}

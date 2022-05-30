package eu.xenit.contentcloud.scribe.generator.openapi.model;

import lombok.Data;

@Data
public class OpenApiMediaTypeObject {

    private OpenApiSchema schema;
    private Object example;

    public OpenApiMediaTypeObject(OpenApiSchema schema) {
        this.schema = schema;
    }
}

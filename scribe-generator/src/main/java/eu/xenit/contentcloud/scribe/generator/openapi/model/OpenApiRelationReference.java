package eu.xenit.contentcloud.scribe.generator.openapi.model;

import lombok.Getter;

public class OpenApiRelationReference extends OpenApiDataType {
    @Getter
    private String example;

    public OpenApiRelationReference() {
        super("string", null);
    }

    public OpenApiRelationReference(String example) {
        this();
        this.example = example;
    }
}

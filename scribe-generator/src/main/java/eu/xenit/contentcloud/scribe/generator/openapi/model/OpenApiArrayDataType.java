package eu.xenit.contentcloud.scribe.generator.openapi.model;

import lombok.Getter;

public class OpenApiArrayDataType extends OpenApiDataType {
    @Getter
    private OpenApiReferenceObject items;

    public OpenApiArrayDataType() {
        super("array");
    }

    public OpenApiArrayDataType(OpenApiReferenceObject items) {
        this();
        this.items = items;
    }

}

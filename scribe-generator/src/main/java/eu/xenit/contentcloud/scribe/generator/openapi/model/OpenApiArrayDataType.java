package eu.xenit.contentcloud.scribe.generator.openapi.model;

import lombok.Getter;

public class OpenApiArrayDataType extends OpenApiDataType {
    @Getter
    private OpenApiSchema items;

    public OpenApiArrayDataType() {
        super("array");
    }

    public OpenApiArrayDataType(OpenApiSchema items) {
        this();
        this.items = items;
    }

}

package eu.xenit.contentcloud.scribe.generator.openapi.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class OpenApiAllOfReference extends OpenApiSchema {
    @Getter
    private final List<OpenApiReferenceObject> allOf = new ArrayList<>();

}

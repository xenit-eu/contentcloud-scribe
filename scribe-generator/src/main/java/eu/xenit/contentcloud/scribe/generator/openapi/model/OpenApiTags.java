package eu.xenit.contentcloud.scribe.generator.openapi.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OpenApiTags {
    private String name;

    public OpenApiTags(String name) {
        this.name = name;
    }
}

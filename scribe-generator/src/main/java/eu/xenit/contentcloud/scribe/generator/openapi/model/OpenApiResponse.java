package eu.xenit.contentcloud.scribe.generator.openapi.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OpenApiResponse {

    private String description;
    private Object content;

    public OpenApiResponse(String description) {
        this.description = description;
    }

    public OpenApiResponse(String description, Object content) {
        this.description = description;
        this.content = content;
    }
}

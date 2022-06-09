package eu.xenit.contentcloud.scribe.generator.openapi.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OpenApiInfo {
    private String description;
    private String version;
    private String title;

    public OpenApiInfo(String description, String version, String title) {
        this.description = description;
        this.version = version;
        this.title = title;
    }
}

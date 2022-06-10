package eu.xenit.contentcloud.scribe.generator.openapi.model;

import lombok.Getter;
import lombok.Setter;

public class OpenApiServers {
    @Getter @Setter
    private String url;

    public OpenApiServers(String url) {
        this.url = url;
    }
}

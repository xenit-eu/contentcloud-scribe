package eu.xenit.contentcloud.scribe.generator.openapi.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

public class OpenApiModel {

    @Getter
    private final String openapi;

    @Getter
    private final OpenApiInfo info;

    @Getter
    private final List<OpenApiServers> servers = new ArrayList<>();

    @Getter
    private final List<OpenApiTags> tags = new ArrayList<>();

    @Getter
    private final Map<String, Map<String, OpenApiModelPath>> paths = new LinkedHashMap<>();

    @Getter
    private final OpenApiComponents components;

    public OpenApiModel(String openapi, OpenApiInfo info, OpenApiComponents components) {
        this.openapi = openapi;
        this.info = info;
        this.components = components;
    }
}
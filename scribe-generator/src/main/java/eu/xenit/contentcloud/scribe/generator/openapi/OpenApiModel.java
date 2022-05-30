package eu.xenit.contentcloud.scribe.generator.openapi;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

public class OpenApiModel {

    @Getter
    private final String openapi;

    @Getter
    private final OpenApiWriterInfo info;

    @Getter
    private final List<OpenApiWriterTags> tags = new ArrayList<>();

    @Getter
    private final Map<String, Map<String, OpenApiModelPaths>> paths = new LinkedHashMap<>();

    public OpenApiModel(String openapi, OpenApiWriterInfo info) {
        this.openapi = openapi;
        this.info = info;
    }
}

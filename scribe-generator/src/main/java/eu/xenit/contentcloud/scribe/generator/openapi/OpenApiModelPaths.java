package eu.xenit.contentcloud.scribe.generator.openapi;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
public class OpenApiModelPaths {

    private final List<String> tags;
//    private String operationId;

    private final List<OpenApiWriterParameters> parameters = new ArrayList<>();
    private final Map<String, OpenApiWriterResponse> responses = new LinkedHashMap<>();

    public OpenApiModelPaths(List<String> tags) {
        this.tags = tags;
//        this.operationId = operationId;
    }
}
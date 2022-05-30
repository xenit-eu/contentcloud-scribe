package eu.xenit.contentcloud.scribe.generator.openapi.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
public class OpenApiModelPath {

    private final List<String> tags;
//    private String operationId;

    @Setter
    private OpenApiRequestBody requestBody = null;

    private final List<OpenApiWriterParameters> parameters = new ArrayList<>();
    private final Map<String, OpenApiWriterResponse> responses = new LinkedHashMap<>();

    public OpenApiModelPath(List<String> tags) {
        this.tags = tags;
//        this.operationId = operationId;
    }
}
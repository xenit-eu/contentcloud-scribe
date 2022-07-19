package eu.xenit.contentcloud.scribe.generator.openapi.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
public class OpenApiModelPath {

    private final List<String> tags;

    private final String operationId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<OpenApiParameters> parameters = new ArrayList<>();

    @Setter
    private OpenApiRequestBody requestBody = null;

    private final Map<String, OpenApiResponse> responses = new LinkedHashMap<>();

    public OpenApiModelPath(List<String> tags, String operationId) {
        this.tags = tags;
        this.operationId = operationId;
    }
}
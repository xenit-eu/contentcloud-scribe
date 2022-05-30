package eu.xenit.contentcloud.scribe.generator.openapi;

import java.util.List;
import java.util.Map;

public class OpenApiWriterPaths {
    private List<String> tags;
//    private String operationId;
    private List<OpenApiWriterParameters> parameters;
    private Map<String, OpenApiWriterResponses> responses;

    public OpenApiWriterPaths(List<String> tags, List<OpenApiWriterParameters> parameters, Map<String, OpenApiWriterResponses> responses) {
        this.tags = tags;
//        this.operationId = operationId;
        this.parameters = parameters;
        this.responses = responses;
    }


    public List<String> getTags() {
        return tags;
    }

//    public String getOperationId() {
//        return operationId;
//    }

    public List<OpenApiWriterParameters> getParameters() {
        return parameters;
    }

    public Map<String, OpenApiWriterResponses> getResponses() {
        return responses;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

//    public void setOperationId(String operationId) {
//        this.operationId = operationId;
//    }

    public void setParameters(List<OpenApiWriterParameters> parameters) {
        this.parameters = parameters;
    }

    public void setResponses(Map<String, OpenApiWriterResponses> responses) {
        this.responses = responses;
    }
}
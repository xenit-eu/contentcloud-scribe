package eu.xenit.contentcloud.scribe.generator.openapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OpenApiParameters {

    public enum ParameterType {
        @JsonProperty("path")
        PATH,
        @JsonProperty("query")
        QUERY
    }

    private String name;
    private ParameterType in;
    private boolean required;
    private OpenApiDataType schema;

    public OpenApiParameters(String name, ParameterType in, boolean required, OpenApiDataType schema) {
        this.name = name;
        this.in = in;
        this.required = required;
        this.schema = schema;
    }
}

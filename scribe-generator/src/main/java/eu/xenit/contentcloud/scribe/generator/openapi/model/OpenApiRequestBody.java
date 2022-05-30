package eu.xenit.contentcloud.scribe.generator.openapi.model;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OpenApiRequestBody {

    private String description = null;
    private Boolean required = null;
    private Map<String, OpenApiMediaTypeObject> content = new LinkedHashMap<>();

}

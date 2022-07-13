package eu.xenit.contentcloud.scribe.generator.openapi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class OpenApiDataType extends OpenApiSchema {

    @Getter @Setter
    private String type;

    @Getter @Setter
    private String format;

}

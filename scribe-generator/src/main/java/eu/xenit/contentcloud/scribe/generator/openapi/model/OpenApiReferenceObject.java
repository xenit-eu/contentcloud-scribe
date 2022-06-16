package eu.xenit.contentcloud.scribe.generator.openapi.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class OpenApiReferenceObject extends OpenApiSchema {

    public OpenApiReferenceObject(String ref) {
        this.$ref = ref;
    }

    @Getter @Setter
    private String $ref;

}

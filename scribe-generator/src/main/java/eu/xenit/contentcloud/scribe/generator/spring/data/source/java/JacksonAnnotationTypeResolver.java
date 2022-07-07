package eu.xenit.contentcloud.scribe.generator.spring.data.source.java;

import eu.xenit.contentcloud.bard.ClassName;
import eu.xenit.contentcloud.scribe.generator.language.SemanticTypeResolver;
import eu.xenit.contentcloud.scribe.generator.language.java.JavaTypeName;
import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JacksonAnnotations.JacksonAnnotation;

public class JacksonAnnotationTypeResolver implements SemanticTypeResolver<JavaTypeName> {

    @Override
    public boolean supports(SemanticType type) {
        return type instanceof JacksonAnnotation;
    }

    @Override
    public JavaTypeName resolve(SemanticType type) throws TypeResolutionException {
        var annotation = (JacksonAnnotation) type;
        return new JavaTypeName(ClassName.get("com.fasterxml.jackson.annotation", annotation.getName()));
    }
}

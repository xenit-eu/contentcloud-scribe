package eu.xenit.contentcloud.scribe.generator.spring.data.source.java;

import eu.xenit.contentcloud.bard.ClassName;
import eu.xenit.contentcloud.scribe.generator.language.SemanticTypeResolver;
import eu.xenit.contentcloud.scribe.generator.language.java.JavaTypeName;
import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.SpringDataRestAnnotations.SpringDataRestAnnotation;

public class SpringDataAnnotationTypeResolver implements SemanticTypeResolver<JavaTypeName> {

    @Override
    public boolean supports(SemanticType type) {
        return type instanceof SpringDataRestAnnotation;
    }

    @Override
    public JavaTypeName resolve(SemanticType type) throws TypeResolutionException {
        var annotation = (SpringDataRestAnnotation) type;
        return new JavaTypeName(ClassName.get("org.springframework.data.rest.core.annotation", annotation.getName()));
    }
}

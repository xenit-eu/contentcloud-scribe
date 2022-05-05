package eu.xenit.contentcloud.scribe.generator.spring.content.source.java;

import eu.xenit.contentcloud.bard.ClassName;
import eu.xenit.contentcloud.scribe.generator.language.SemanticTypeResolver;
import eu.xenit.contentcloud.scribe.generator.language.java.JavaTypeName;
import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import eu.xenit.contentcloud.scribe.generator.spring.content.model.SpringContentAnnotations.SpringContentAnnotation;

public class SpringContentAnnotationDataTypeResolver implements SemanticTypeResolver<JavaTypeName> {

    @Override
    public boolean supports(SemanticType type) {
        return type instanceof SpringContentAnnotation;
    }

    @Override
    public JavaTypeName resolve(SemanticType type) throws TypeResolutionException {
        var annotation = (SpringContentAnnotation) type;
        var annotationType = ClassName.get("org.springframework.content.commons.annotations", annotation.getName());
        return new JavaTypeName(annotationType);
    }
}

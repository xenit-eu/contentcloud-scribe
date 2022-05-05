package eu.xenit.contentcloud.scribe.generator.spring.data.source.java;

import eu.xenit.contentcloud.bard.ClassName;
import eu.xenit.contentcloud.scribe.generator.language.SemanticTypeResolver;
import eu.xenit.contentcloud.scribe.generator.language.java.JavaTypeName;
import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaAnnotations.JpaAnnotation;

public class JpaAnnotationTypeResolver implements SemanticTypeResolver<JavaTypeName> {

    @Override
    public boolean supports(SemanticType type) {
        return type instanceof JpaAnnotation;
    }

    @Override
    public JavaTypeName resolve(SemanticType type) throws TypeResolutionException {
        var annotation = (JpaAnnotation) type;
        return new JavaTypeName(ClassName.get("javax.persistence", annotation.getName()));
    }
}

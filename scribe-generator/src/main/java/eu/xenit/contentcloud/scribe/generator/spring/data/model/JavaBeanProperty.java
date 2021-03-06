package eu.xenit.contentcloud.scribe.generator.spring.data.model;

import eu.xenit.contentcloud.scribe.generator.source.types.Annotation;
import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import java.util.stream.Stream;

public interface JavaBeanProperty {

    String name();
    String normalizedName();
    String fieldName();

    SemanticType type();

    JavaBeanProperty addAnnotation(Annotation annotation);
    default JavaBeanProperty addAnnotation(SemanticType type) {
        return this.addAnnotation(Annotation.withType(type));
    }

    Stream<Annotation> annotations();
}



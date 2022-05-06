package eu.xenit.contentcloud.scribe.generator.spring.data.model;

import eu.xenit.contentcloud.scribe.generator.source.types.Annotation;
import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import java.util.Collection;

public interface JavaBeanProperty {

    String name();
    JavaBeanProperty name(String name);

    SemanticType type();
    JavaBeanProperty type(SemanticType type);


    JavaBeanProperty addAnnotation(Annotation annotation);
    default JavaBeanProperty addAnnotation(SemanticType type) {
        return this.addAnnotation(Annotation.builder(type).build());
    }

    Collection<Annotation> annotations();
}



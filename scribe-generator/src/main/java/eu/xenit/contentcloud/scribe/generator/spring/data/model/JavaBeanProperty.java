package eu.xenit.contentcloud.scribe.generator.spring.data.model;

import eu.xenit.contentcloud.bard.ClassName;
import eu.xenit.contentcloud.bard.TypeName;
import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Set;

public interface JavaBeanProperty {

    String name();
    JavaBeanProperty name(String name);

    SemanticType type();
    JavaBeanProperty type(SemanticType type);

    JavaBeanProperty addAnnotation(Type annotationType);
    Collection<Type> annotations();
}



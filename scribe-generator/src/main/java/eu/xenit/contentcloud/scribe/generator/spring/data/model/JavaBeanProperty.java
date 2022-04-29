package eu.xenit.contentcloud.scribe.generator.spring.data.model;

import eu.xenit.contentcloud.bard.ClassName;
import eu.xenit.contentcloud.bard.TypeName;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Set;

public interface JavaBeanProperty {

    String name();
    JavaBeanProperty name(String name);

    Type type();
    JavaBeanProperty type(Type type);

    JavaBeanProperty addAnnotation(Type annotationType);
    Collection<Type> annotations();
}



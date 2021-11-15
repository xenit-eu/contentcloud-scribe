package eu.xenit.contentcloud.scribe.generator.source;

import eu.xenit.contentcloud.bard.TypeName;

import java.lang.reflect.Type;
import java.util.stream.Stream;

public interface JavaBean extends TypeBuilder {

    JavaBean addProperty(TypeName fieldType, String name);

    default JavaBean addProperty(Type type, String name) {
        return this.addProperty(TypeName.get(type), name);
    }

    Stream<? extends JavaBeanProperty> fields();
}


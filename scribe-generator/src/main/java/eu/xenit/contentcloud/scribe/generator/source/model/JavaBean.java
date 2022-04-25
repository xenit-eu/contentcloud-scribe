package eu.xenit.contentcloud.scribe.generator.source.model;

import eu.xenit.contentcloud.bard.TypeName;

import eu.xenit.contentcloud.scribe.generator.source.model.lombok.LombokTypeAnnotationsConfig;
import eu.xenit.contentcloud.scribe.generator.source.model.lombok.LombokTypeAnnotationsCustomizer;
import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface JavaBean extends TypeModel {

    JavaBean addProperty(TypeName fieldType, String name);

    default JavaBean addProperty(Type type, String name) {
        return this.addProperty(TypeName.get(type), name);
    }

    Stream<? extends JavaBeanProperty> fields();

    JavaBean lombokTypeAnnotations(Consumer<LombokTypeAnnotationsCustomizer> lombok);

    LombokTypeAnnotationsConfig lombok();
}


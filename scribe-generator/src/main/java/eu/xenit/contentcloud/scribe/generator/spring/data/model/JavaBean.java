package eu.xenit.contentcloud.scribe.generator.spring.data.model;

import eu.xenit.contentcloud.bard.TypeName;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.lombok.LombokTypeAnnotationsConfig;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.lombok.LombokTypeAnnotationsCustomizer;
import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface JavaBean extends TypeModel {


    JavaBean addProperty(TypeName fieldType, String name, Consumer<JavaBeanProperty> callback);
    default JavaBean addProperty(Type fieldType, String name, Consumer<JavaBeanProperty> callback) {
        return this.addProperty(TypeName.get(fieldType), name, callback);
    }

    default JavaBean addProperty(TypeName type, String name) {
        return this.addProperty(type, name, property -> {});
    }

    Stream<? extends JavaBeanProperty> fields();

    JavaBean lombokTypeAnnotations(Consumer<LombokTypeAnnotationsCustomizer> lombok);

    LombokTypeAnnotationsConfig lombok();
}


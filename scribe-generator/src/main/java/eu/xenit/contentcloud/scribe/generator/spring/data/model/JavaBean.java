package eu.xenit.contentcloud.scribe.generator.spring.data.model;

import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.lombok.LombokTypeAnnotationsConfig;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.lombok.LombokTypeAnnotationsCustomizer;
import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface JavaBean extends TypeDeclaration {

    JavaBean addProperty(SemanticType fieldType, String name, Consumer<JavaBeanProperty> callback);
    default JavaBean addProperty(SemanticType type, String name) {
        return this.addProperty(type, name, property -> {});
    }

    JavaBean removeProperty(String name);


    Stream<? extends JavaBeanProperty> fields();

    JavaBean lombokTypeAnnotations(Consumer<LombokTypeAnnotationsCustomizer> lombok);
    LombokTypeAnnotationsConfig lombok();
}


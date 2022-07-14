package eu.xenit.contentcloud.scribe.generator.spring.data.model;

import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.lombok.LombokTypeAnnotationsCustomizer;
import java.util.function.Consumer;

public interface JavaBeanBuilder<T extends JavaBeanBuilder> extends JavaBean {

    T addProperty(SemanticType fieldType, String name, Consumer<JavaBeanProperty> callback);

    default T addProperty(SemanticType type, String name) {
        return this.addProperty(type, name, property -> {});
    }

    T removeProperty(String name);

    T lombokTypeAnnotations(Consumer<LombokTypeAnnotationsCustomizer> lombok);
}

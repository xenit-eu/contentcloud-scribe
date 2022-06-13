package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.scribe.generator.source.types.Annotation;
import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.JavaBeanProperty;

import java.beans.Introspector;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Stream;
import javax.lang.model.element.Modifier;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.lang.Nullable;

public interface JpaEntityProperty extends JavaBeanProperty {

    JpaEntityProperty name(String name);

    JpaEntityProperty type(SemanticType type);


    static JpaEntityProperty create(SemanticType type, String name) {
        return new JpaEntityFieldImpl(type, name);
    }
}

@Getter
@Setter
@Accessors(fluent = true, chain = true)
class JpaEntityFieldImpl implements JpaEntityProperty {

    @NonNull
    private SemanticType type;

    @Getter
    @NonNull
    private String name;

    private Modifier modifiers = Modifier.DEFAULT;

    private Collection<Annotation> annotations = new HashSet<>();

    @Nullable
    private String column;

    JpaEntityFieldImpl(SemanticType fieldType, String name) {
        this.type = fieldType;
        this.name = Introspector.decapitalize(name);
    }

    @Override
    public JavaBeanProperty addAnnotation(Annotation annotation) {
        this.annotations.add(annotation);
        return this;
    }

    public Stream<Annotation> annotations() {
        return this.annotations.stream();
    }


}

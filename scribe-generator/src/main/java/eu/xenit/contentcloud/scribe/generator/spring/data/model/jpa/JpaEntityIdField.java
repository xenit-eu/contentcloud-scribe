package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.scribe.generator.source.types.Annotation;
import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.JavaBeanProperty;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

public interface JpaEntityIdField extends JpaEntityProperty {

    enum GenerationStrategy {
        AUTO
    }

    static JpaEntityIdField named(String fieldName) {
        return new JpaEntityIdFieldImpl(fieldName);
    }

    JpaEntityIdField name(String name);
    JpaEntityIdField type(SemanticType type);

    GenerationStrategy generationStrategy();
    JpaEntityIdField generationStrategy(GenerationStrategy strategy);
}

@Accessors(fluent = true, chain = true)
class JpaEntityIdFieldImpl implements JpaEntityIdField {

    @NonNull
    @Getter @Setter
    private String name;

    @NonNull
    @Getter @Setter
    private SemanticType type = SemanticType.UUID;

    @NonNull
    @Getter @Setter
    private GenerationStrategy generationStrategy = GenerationStrategy.AUTO;

    private Collection<Annotation> annotations = new HashSet<>();

    public Stream<Annotation> annotations() {
        return this.annotations.stream();
    }

    JpaEntityIdFieldImpl(String name) {
        this.name = name;
    }

    @Override
    public JavaBeanProperty addAnnotation(Annotation annotation) {
        this.annotations.add(annotation);
        return this;
    }
}

package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.scribe.generator.source.types.Annotation;
import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.JavaBeanProperty;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntityFieldImpl.JpaFieldNaming;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.stream.Stream;
import javax.lang.model.element.Modifier;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.lang.Nullable;

public interface JpaEntityRelationship extends JavaBeanProperty {

    default boolean isBidirectional() {
        return false;
    }
    boolean manyTargets();
    boolean manySources();

}

interface JpaEntityRelationshipBuilder {

}


@Getter
@Setter
@Accessors(fluent = true, chain = true)
abstract class JpaEntityRelationshipImpl implements JpaEntityRelationship, JpaEntityRelationshipBuilder {

    @NonNull
    private final SemanticType type;

    @NonNull
    JpaFieldNaming naming;

    private Modifier modifiers = Modifier.DEFAULT;

    private Collection<Annotation> annotations = new LinkedHashSet<>();

    @Nullable
    private String column;

    JpaEntityRelationshipImpl(SemanticType fieldType, String name) {
        this.type = fieldType;
        this.naming = JpaFieldNaming.from(name);
    }

    @Override
    public String name() {
        return this.naming.original();
    }

    @Override
    public String normalizedName() {
        return this.naming.normalized();
    }

    @Override
    public String fieldName() {
        return this.naming.fieldName();
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
package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.bard.TypeName;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

public interface JpaEntityIdField extends JpaEntityField {

    enum GenerationStrategy {
        AUTO
    }

    static JpaEntityIdField named(String fieldName) {
        return new JpaEntityIdFieldImpl(fieldName);
    }

    JpaEntityIdField name(String name);
    JpaEntityIdField type(TypeName type);

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
    private TypeName type = TypeName.get(UUID.class);

    @NonNull
    @Getter @Setter
    private GenerationStrategy generationStrategy = GenerationStrategy.AUTO;

    JpaEntityIdFieldImpl(String name) {
        this.name = name;
    }

}

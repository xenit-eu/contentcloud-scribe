package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.bard.TypeName;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.JavaBean;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.JavaBeanProperty;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.lombok.LombokTypeAnnotations;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.lombok.LombokTypeAnnotationsConfig;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.lombok.LombokTypeAnnotationsCustomizer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.lang.Nullable;

public interface JpaEntity extends JavaBean {
    
    static JpaEntity withClassName(String className) {
        return new JpaEntityImpl(className);
    }

    String entityName();
    JpaEntity entityName(String name);

    String tableName();
    JpaEntity tableName(String name);

    JpaEntityIdField id();
    JpaEntity id(Consumer<JpaEntityIdField> customizer);

    @Override
    Stream<JpaEntityProperty> fields();

    JpaEntity addOneToOneRelation(String fieldName, TypeName targetClass, Consumer<OneToOneRelation> customizer);
}


@Accessors(fluent = true, chain = true)
@RequiredArgsConstructor
class JpaEntityImpl implements JpaEntity {

    @Getter
    private final String className;

    @Nullable
    @Getter @Setter
    private String entityName;

    @Nullable
    @Getter @Setter
    private String tableName;

    @Getter
    private final JpaEntityIdField id = JpaEntityIdField.named("id");

    private final List<JpaEntityProperty> fields = new ArrayList<>();

    private final LombokTypeAnnotations lombok = new LombokTypeAnnotations();

    @Override
    public JpaEntity id(Consumer<JpaEntityIdField> customizer) {
        customizer.accept(this.id);
        return this;
    }

    @Override
    public JpaEntity addProperty(TypeName fieldType, String name, Consumer<JavaBeanProperty> customizer) {
        var property = JpaEntityProperty.create(fieldType, name);
        customizer.accept(property);
        this.fields.add(property);
        return this;
    }

    @Override
    public Stream<JpaEntityProperty> fields() {
        return this.fields.stream();
    }

    @Override
    public JpaEntity addOneToOneRelation(String fieldName, TypeName targetClass,
            Consumer<OneToOneRelation> customizer) {
        var relation = new OneToOneRelationImpl(targetClass, fieldName);
        customizer.accept(relation);
        this.fields.add(relation);
        return this;
    }

    @Override
    public JavaBean lombokTypeAnnotations(Consumer<LombokTypeAnnotationsCustomizer> customizer) {
        customizer.accept(this.lombok);
        return this;
    }

    @Override
    public LombokTypeAnnotationsConfig lombok() {
        return this.lombok;
    }


}

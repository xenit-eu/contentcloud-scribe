package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.JavaBean;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.JavaBeanProperty;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.lombok.LombokTypeAnnotations;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.lombok.LombokTypeAnnotationsConfig;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.lombok.LombokTypeAnnotationsCustomizer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

public interface JpaEntity extends JavaBean {
    
    static JpaEntity withName(String entityName) {
        return new JpaEntityImpl(entityName);
    }

    String entityName();

    JpaEntityIdField id();
    JpaEntity id(Consumer<JpaEntityIdField> customizer);

    @Override
    Stream<JpaEntityProperty> fields();

    JpaEntity addOneToOneRelation(String fieldName, SemanticType targetClass, Consumer<OneToOneRelation> customizer);
    JpaEntity addManyToOneRelation(String fieldName, SemanticType targetClass, Consumer<ManyToOneRelation> customizer);
}


@Accessors(fluent = true, chain = true)
@RequiredArgsConstructor
class JpaEntityImpl implements JpaEntity {

    @Getter
    private final String entityName;

    @Getter
    private final JpaEntityIdField id = JpaEntityIdField.named("id");

    private final Map<String, JpaEntityProperty> fields = new LinkedHashMap<>();

    private final LombokTypeAnnotations lombok = new LombokTypeAnnotations();

    @Override
    public String className() {
        return this.entityName;
    }

    @Override
    public JpaEntity id(Consumer<JpaEntityIdField> customizer) {
        customizer.accept(this.id);
        return this;
    }

    @Override
    public JpaEntity addProperty(SemanticType fieldType, String name, Consumer<JavaBeanProperty> customizer) {
        var property = JpaEntityProperty.create(fieldType, name);
        customizer.accept(property);
        var old = this.fields.putIfAbsent(property.name(), property);
        if (old != null) {
            throw new IllegalArgumentException("Entity "+entityName+" already contains field '"+name+"'");
        }

        return this;
    }

    @Override
    public JavaBean removeProperty(String name) {
        var field = this.fields.remove(name);
        if (field == null) {
            throw new IllegalArgumentException("Entity "+entityName+" does not contain a field '"+name+"'");
        }
        return this;
    }

    @Override
    public Stream<JpaEntityProperty> fields() {
        return this.fields.values().stream();
    }

    @Override
    public JpaEntity addOneToOneRelation(String fieldName, SemanticType targetClass,
            Consumer<OneToOneRelation> customizer) {
        var relation = new OneToOneRelationImpl(targetClass, fieldName);
        customizer.accept(relation);
        this.fields.put(fieldName, relation);
        return this;
    }

    @Override
    public JpaEntity addManyToOneRelation(String fieldName, SemanticType targetClass,
            Consumer<ManyToOneRelation> customizer) {
        var relation = new ManyToOneRelationImpl(targetClass, fieldName);
        customizer.accept(relation);
        this.fields.put(fieldName, relation);
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

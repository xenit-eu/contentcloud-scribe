package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.JavaBean;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.JavaBeanProperty;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntityFieldImpl.JpaFieldNaming;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.lombok.LombokTypeAnnotations;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.lombok.LombokTypeAnnotationsConfig;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.lombok.LombokTypeAnnotationsCustomizer;
import eu.xenit.contentcloud.scribe.generator.spring.data.rest.RestResourceEntity;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.lang.model.SourceVersion;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;

public interface JpaEntity extends JavaBean {

    static JpaEntityBuilder withName(String name) {
        return new JpaEntityImpl(name);
    }

    default RestResourceEntity defaultRestResource() {
        return RestResourceEntity.forSpringDefaults(this);
    }

    RestResourceEntity restResource();


    String entityName();

    JpaEntityIdField id();
}


@Accessors(fluent = true, chain = true)
class JpaEntityImpl implements JpaEntity, JpaEntityBuilder {

    private final JpaEntityNaming naming;

    @Getter
    @Setter
    private RestResourceEntity restResource;

    @Getter
    private final JpaEntityIdField id = JpaEntityIdField.named("id");

    private final Map<String, JavaBeanProperty> fields = new LinkedHashMap<>();

    private final LombokTypeAnnotations lombok = new LombokTypeAnnotations();

    JpaEntityImpl(String name) {
        this.naming = JpaEntityNaming.from(name);
    }

    public String entityName() {
        return this.naming.name();
    }

    @Override
    public String className() {
        return this.naming.className();
    }

    @Override
    public JpaEntityBuilder id(Consumer<JpaEntityIdField> customizer) {
        customizer.accept(this.id);
        return this;
    }

    @Override
    public JpaEntityBuilder addProperty(SemanticType fieldType, String name, Consumer<JavaBeanProperty> customizer) {
        var property = JpaEntityProperty.create(fieldType, name);
        customizer.accept(property);
        var old = this.fields.putIfAbsent(property.normalizedName(), property);
        if (old != null) {
            var msg = "Entity %s already contains field '%s'".formatted(this.entityName(), old.normalizedName());
            throw new IllegalArgumentException(msg);
        }

        return this;
    }

    @Override
    public JpaEntityBuilder removeProperty(@NonNull String name) {
        var field = this.fields.remove(JpaFieldNaming.normalized(name));
        if (field == null) {
            var msg = "Entity %s does not contain a field named '%s'".formatted(this.entityName(), name);
            throw new IllegalArgumentException(msg);
        }
        return this;
    }

    @Override
    public Stream<JavaBeanProperty> fields() {
        return this.fields.values().stream();
    }

    @Override
    public JpaEntityBuilder addOneToOneRelation(String fieldName, SemanticType targetClass,
            Consumer<OneToOneRelation> customizer) {
        var relation = new OneToOneRelationImpl(targetClass, fieldName);
        customizer.accept(relation);
        this.fields.put(relation.normalizedName(), relation);
        return this;
    }

    @Override
    public JpaEntityBuilder addOneToManyRelation(String fieldName, SemanticType targetClass,
            Consumer<OneToManyRelation> customizer) {
        var relation = new OneToManyWithJoinColumnRelationImpl(this::entityName, targetClass, fieldName);
        customizer.accept(relation);
        this.fields.put(relation.normalizedName(), relation);
        return this;
    }

    @Override
    public JpaEntityBuilder addManyToOneRelation(String fieldName, SemanticType targetClass,
            Consumer<ManyToOneRelation> customizer) {
        var relation = new ManyToOneRelationImpl(targetClass, fieldName);
        customizer.accept(relation);
        this.fields.put(relation.normalizedName(), relation);
        return this;
    }

    @Override
    public JpaEntityBuilder addManyToManyRelation(String fieldName, SemanticType targetType,
            Consumer<ManyToManyRelation> customizer) {
        var relation = new ManyToManyRelationImpl(targetType, fieldName);
        customizer.accept(relation);
        this.fields.put(relation.normalizedName(), relation);
        return this;
    }

    @Override
    public JpaEntityBuilder lombokTypeAnnotations(Consumer<LombokTypeAnnotationsCustomizer> customizer) {
        customizer.accept(this.lombok);
        return this;
    }

    @Override
    public LombokTypeAnnotationsConfig lombok() {
        return this.lombok;
    }

    /**
     * JpaEntityNaming is a class that encapsulate naming conventions around JPA entities
     */
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class JpaEntityNaming {

        private String name;
        private String className;

        static JpaEntityNaming from(@NonNull String name) {
            validate(name);

            return new JpaEntityNaming(name, deriveClassName(name));
        }

        static String deriveClassName(String name) {
            var className = Stream.of(name
                            // first use regex to find number- and letter-segments
                            // and insert a hyphen after every segment
                            .replaceAll("\\d+|\\D+", "$0-")

                            // split kebab case
                            // there can be consecutive hyphens, resulting in empty-string parts
                            .split("-")
                    )
                    // re-concatenate all the segments as TitleCase
                    // empty string segments are not a problem
                    .reduce("", (buffer, segment) -> buffer + StringUtils.capitalize(segment));

            if (!SourceVersion.isName(className)) {
                throw new IllegalArgumentException("Cannot derive valid classname from '" + name + "'");
            }

            return className;
        }

        private static void validate(String name) {
            if (name.length() < 3 || name.length() > 50) {
                var msg = "Expected name '%s' to be 3 to 50 characters long, but was %d".formatted(name, name.length());
                throw new IllegalArgumentException(msg);
            }

            if (!name.matches("^[A-Za-z0-9\\-]{3,50}$")) {
                throw new IllegalArgumentException("Only alphanumeric characters and single hyphens are allowed");
            }

            if (name.startsWith("-") || name.endsWith("-")) {
                throw new IllegalArgumentException("Cannot start or end with a hyphen");
            }

            if (name.indexOf("--") > 0) {
                throw new IllegalArgumentException("Only alphanumeric characters and single hyphens are allowed");
            }
        }

    }

}

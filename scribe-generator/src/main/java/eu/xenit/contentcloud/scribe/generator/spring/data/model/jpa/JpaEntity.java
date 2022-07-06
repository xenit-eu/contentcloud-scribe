package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.JavaBean;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.JavaBeanProperty;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa.JpaEntityFieldImpl.JpaFieldNaming;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.lombok.LombokTypeAnnotations;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.lombok.LombokTypeAnnotationsConfig;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.lombok.LombokTypeAnnotationsCustomizer;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.lang.model.SourceVersion;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;

public interface JpaEntity extends JavaBean {

    static JpaEntity withName(String name) {
        return new JpaEntityImpl(name);
    }

    String entityName();

    JpaEntityIdField id();

    JpaEntity id(Consumer<JpaEntityIdField> customizer);

    @Override
    Stream<JpaEntityProperty> fields();

    JpaEntity addOneToOneRelation(String fieldName, SemanticType targetClass, Consumer<OneToOneRelation> customizer);

    JpaEntity addOneToManyRelation(String fieldName, SemanticType targetClass, Consumer<OneToManyRelation> customizer);

    JpaEntity addManyToOneRelation(String fieldName, SemanticType targetClass, Consumer<ManyToOneRelation> customizer);
}


@Accessors(fluent = true, chain = true)
class JpaEntityImpl implements JpaEntity {

    private final JpaEntityNaming naming;

    @Getter
    private final JpaEntityIdField id = JpaEntityIdField.named("id");

    private final Map<String, JpaEntityProperty> fields = new LinkedHashMap<>();

    private final LombokTypeAnnotations lombok = new LombokTypeAnnotations();

    public JpaEntityImpl(String name) {
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
    public JpaEntity id(Consumer<JpaEntityIdField> customizer) {
        customizer.accept(this.id);
        return this;
    }

    @Override
    public JpaEntity addProperty(SemanticType fieldType, String name, Consumer<JavaBeanProperty> customizer) {
        var property = JpaEntityProperty.create(fieldType, name);
        customizer.accept(property);
        var old = this.fields.putIfAbsent(property.canonicalName(), property);
        if (old != null) {
            var msg = "Entity %s already contains field '%s'".formatted(this.entityName(), old.canonicalName());
            throw new IllegalArgumentException(msg);
        }

        return this;
    }

    @Override
    public JavaBean removeProperty(@NonNull String name) {
        var field = this.fields.remove(JpaFieldNaming.normalized(name));
        if (field == null) {
            var msg = "Entity %s does not contain a field named '%s'".formatted(this.entityName(), name);
            throw new IllegalArgumentException(msg);
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
        this.fields.put(relation.canonicalName(), relation);
        return this;
    }

    @Override
    public JpaEntity addOneToManyRelation(String fieldName, SemanticType targetClass,
            Consumer<OneToManyRelation> customizer) {
        var relation = new OneToManyWithJoinColumnRelationImpl(this::entityName, targetClass, fieldName);
        customizer.accept(relation);
        this.fields.put(relation.canonicalName(), relation);
        return this;
    }

    @Override
    public JpaEntity addManyToOneRelation(String fieldName, SemanticType targetClass,
            Consumer<ManyToOneRelation> customizer) {
        var relation = new ManyToOneRelationImpl(targetClass, fieldName);
        customizer.accept(relation);
        this.fields.put(relation.canonicalName(), relation);
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

        public static String deriveClassName(String name) {
            var className =
                    // split kebab case
                    // note that there are no consecutive hyphens, and name does not lead or end with a hyphen
                    // which means that every part is a non-empty string
                    Arrays.stream(name.split("-"))

                            // split each part in letter- and number-segments
                            .flatMap(part -> {
                                var segments = part
                                        // first use regex to find letter- and number-segments
                                        .replaceAll("\\d+|\\D+", "$0-")

                                        // split by hyphen again
                                        .split("-");

                                return Stream.of(segments);
                            })

                    // reconcatenate all the segments as TitleCase
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

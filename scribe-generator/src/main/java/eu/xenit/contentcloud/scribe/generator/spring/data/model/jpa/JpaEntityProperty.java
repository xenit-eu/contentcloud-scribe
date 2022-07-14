package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.scribe.generator.source.types.Annotation;
import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.JavaBeanProperty;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public interface JpaEntityProperty extends JavaBeanProperty {

    static JpaEntityProperty create(SemanticType type, String name) {
        return new JpaEntityFieldImpl(type, name);
    }
}

@Getter
@Setter
@Accessors(fluent = true, chain = true)
class JpaEntityFieldImpl implements JpaEntityProperty {

    @NonNull
    private final SemanticType type;

    @NonNull
    JpaFieldNaming naming;

    private Modifier modifiers = Modifier.DEFAULT;

    private Collection<Annotation> annotations = new LinkedHashSet<>();

    @Nullable
    private String column;

    JpaEntityFieldImpl(SemanticType fieldType, String name) {
        this.type = fieldType;
        this.naming = JpaFieldNaming.from(name);

        // if the field has been renamed, add a `@JsonProperty` annotation
        if (!Objects.equals(name, this.naming.fieldName())) {
            this.addAnnotation(Annotation.withType(JacksonAnnotations.JsonProperty)
                    .withMembers(members -> {
                        members.put("value", name);
                    }));
        }
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

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static class JpaFieldNaming {

        private static final int MIN_LENGTH = 1;
        private static final int MAX_LENGTH = 50;

        private String original;

        private String fieldName;
        private String normalized;

        static JpaFieldNaming from(@NonNull String name) {
            validate(name);

            return new JpaFieldNaming(name, deriveFieldName(name), normalized(name));
        }

        static String deriveFieldName(String source) {
            // convert kebab-case to TitleCase
            var fieldName = Arrays.stream(source.split("-"))
                    .reduce("", (buffer, part) -> buffer + StringUtils.capitalize(part));

            // and TitleCase to camelCase
            fieldName = StringUtils.uncapitalize(fieldName);

            // if the field is a (java) keyword, we escape by prefixing with a '_'
            if (SourceVersion.isKeyword(fieldName)) {
                fieldName = "_" + fieldName;
            }

            return fieldName;
        }

        static String normalized(String source) {
            return source.replaceAll("-", "").toLowerCase(Locale.ROOT);
        }

        private static void validate(String name) {
            if (name.length() < MIN_LENGTH || name.length() > MAX_LENGTH) {
                var msg = "Expected field name '%s' to be %d to %d characters long, but was %d".formatted(
                        name, MIN_LENGTH, MAX_LENGTH, name.length());
                throw new IllegalArgumentException(msg);
            }

            if (!name.matches("^[A-Za-z0-9\\-]+$")) {
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

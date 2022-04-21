package eu.xenit.contentcloud.scribe.generator.source.jpa;

import eu.xenit.contentcloud.bard.TypeName;
import eu.xenit.contentcloud.scribe.generator.source.JavaBean;
import eu.xenit.contentcloud.scribe.generator.source.LombokTypeAnnotations;
import eu.xenit.contentcloud.scribe.generator.source.LombokTypeAnnotationsConfig;
import eu.xenit.contentcloud.scribe.generator.source.LombokTypeAnnotationsCustomizer;
import eu.xenit.contentcloud.scribe.generator.source.SourceFile;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface JpaEntity extends JavaBean {
    
    static JpaEntityBuilder withClassName(String className) {
        return new JpaEntityBuilder(className);
    }

    String entityName();
    JpaEntity entityName(String name);

    String tableName();
    JpaEntity tableName(String name);

    JpaEntityIdField id();
    JpaEntity id(Consumer<JpaEntityIdField> customizer);

    @Override
    Stream<JpaEntityField> fields();

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    class JpaEntityBuilder {
        @NonNull
        private String className;

        public JpaEntity withGenerator(JpaEntitySourceCodeGenerator generator) {
            return new JpaEntityImpl(generator, className);
        }
    }
}


@Accessors(fluent = true, chain = true)
@RequiredArgsConstructor
class JpaEntityImpl implements JpaEntity {

    private final JpaEntitySourceCodeGenerator generator;

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

    private final List<JpaEntityField> fields = new ArrayList<>();

    private final LombokTypeAnnotations lombok = new LombokTypeAnnotations();

    @Override
    public SourceFile generate() {
        return this.generator.createSourceFile(this);
    }

    @Override
    public JpaEntity id(Consumer<JpaEntityIdField> customizer) {
        customizer.accept(this.id);
        return this;
    }

    @Override
    public JpaEntity addProperty(TypeName fieldType, String name) {
        this.fields.add(JpaEntityField.create(fieldType, name));
        return this;
    }

    @Override
    public Stream<JpaEntityField> fields() {
        return this.fields.stream();
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

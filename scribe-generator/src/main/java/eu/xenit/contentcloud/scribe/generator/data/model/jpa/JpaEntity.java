package eu.xenit.contentcloud.scribe.generator.data.model.jpa;

import eu.xenit.contentcloud.bard.TypeName;
import eu.xenit.contentcloud.scribe.generator.data.model.JavaBean;
import eu.xenit.contentcloud.scribe.generator.data.model.lombok.LombokTypeAnnotations;
import eu.xenit.contentcloud.scribe.generator.data.model.lombok.LombokTypeAnnotationsConfig;
import eu.xenit.contentcloud.scribe.generator.data.model.lombok.LombokTypeAnnotationsCustomizer;
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
    Stream<JpaEntityField> fields();
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

    private final List<JpaEntityField> fields = new ArrayList<>();

    private final LombokTypeAnnotations lombok = new LombokTypeAnnotations();

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

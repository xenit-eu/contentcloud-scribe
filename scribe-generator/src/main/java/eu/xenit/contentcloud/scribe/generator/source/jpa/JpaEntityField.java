package eu.xenit.contentcloud.scribe.generator.source.jpa;

import eu.xenit.contentcloud.bard.TypeName;
import eu.xenit.contentcloud.scribe.generator.source.JavaBeanProperty;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.lang.Nullable;

import javax.lang.model.element.Modifier;
import java.beans.Introspector;

public interface JpaEntityField extends JavaBeanProperty {

    JpaEntityField name(String name);
    JpaEntityField type(TypeName type);

//    String column();
//    JpaEntityField column(String column);

    static JpaEntityField create(TypeName type, String name) {
        return new JpaEntityFieldImpl(type, name);
    }
}

@Getter @Setter
@Accessors(fluent = true, chain = true)
class JpaEntityFieldImpl implements JpaEntityField {

    @NonNull
    private TypeName type;

    @NonNull
    private String name;

    private Modifier modifiers = Modifier.DEFAULT;

    @Nullable
    private String column;

    JpaEntityFieldImpl(TypeName fieldType, String name) {
        this.type = fieldType;
        this.name = name;
    }

    public String name() {
        return Introspector.decapitalize(this.name);
    }


}

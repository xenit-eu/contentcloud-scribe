package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.bard.AnnotationSpec;
import eu.xenit.contentcloud.bard.TypeName;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.JavaBeanProperty;
import java.beans.Introspector;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import javax.lang.model.element.Modifier;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.lang.Nullable;

public interface JpaEntityProperty extends JavaBeanProperty {

    JpaEntityProperty name(String name);
    JpaEntityProperty type(Type type);


    static JpaEntityProperty create(Type type, String name) {
        return new JpaEntityFieldImpl(type, name);
    }
}

@Getter @Setter
@Accessors(fluent = true, chain = true)
class JpaEntityFieldImpl implements JpaEntityProperty {

    @NonNull
    private Type type;

    @NonNull
    private String name;

    private Modifier modifiers = Modifier.DEFAULT;

    private Collection<Type> annotations = new HashSet<>();

    @Nullable
    private String column;

    JpaEntityFieldImpl(Type fieldType, String name) {
        this.type = fieldType;
        this.name = name;
    }

    public String name() {
        return Introspector.decapitalize(this.name);
    }

    @Override
    public JavaBeanProperty addAnnotation(Type annotationType) {
        this.annotations.add(annotationType);
        return this;
    }


}

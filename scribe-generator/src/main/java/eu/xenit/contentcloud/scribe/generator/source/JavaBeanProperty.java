package eu.xenit.contentcloud.scribe.generator.source;

import eu.xenit.contentcloud.bard.TypeName;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.lang.model.element.Modifier;

public interface JavaBeanProperty {

    String name();
    JavaBeanProperty name(String name);

    TypeName type();
    JavaBeanProperty type(TypeName type);

    static JavaBeanProperty create(TypeName type, String name) {
        return new JavaBeanPropertyImpl(type, name);
    }
}

@Getter
@Accessors(fluent = true)
class JavaBeanPropertyImpl implements JavaBeanProperty {

    private TypeName type;
    private String name;

    JavaBeanPropertyImpl(TypeName fieldType, String name) {
        this.type = fieldType;
        this.name = name;
    }

    private Modifier modifiers = Modifier.DEFAULT;

    @Override
    public JavaBeanProperty name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public JavaBeanProperty type(TypeName type) {
        this.type = type;
        return this;
    }
}



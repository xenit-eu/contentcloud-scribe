package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.scribe.generator.source.types.Annotation;
import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import java.util.Objects;
import java.util.stream.Stream;

public interface ManyToOneRelation extends JpaEntityProperty {

    ManyToOneRelation required(boolean isRequired);
}

class ManyToOneRelationImpl extends JpaEntityFieldImpl implements ManyToOneRelation {

    private boolean isRequired = false;

    ManyToOneRelationImpl(SemanticType fieldType, String name) {
        super(fieldType, name);

        // if the field has been renamed, add a `@RestResource` annotation
        if (!Objects.equals(name, this.naming.fieldName())) {
            this.addAnnotation(Annotation.withType(SpringDataRestAnnotations.RestResource)
                    .withMembers(members -> {
                        members.put("rel", name);
                        members.put("path", name);
                    }));
        }
    }

    @Override
    public ManyToOneRelation required(boolean isRequired) {
        this.isRequired = isRequired;
        return this;
    }

    @Override
    public Stream<Annotation> annotations() {
        var annotation = Annotation.withType(JpaAnnotations.ManyToOne);
        if (isRequired) {
            annotation.getMembers().put("optional", !isRequired);
        }
        return Stream.concat(
                Stream.of(annotation),
                super.annotations()
        );
    }
}

package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.scribe.generator.source.types.Annotation;
import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import java.util.Objects;
import java.util.stream.Stream;
import org.atteo.evo.inflector.English;
import org.springframework.util.StringUtils;

public interface OneToOneRelation extends JpaEntityRelationship {

    OneToOneRelation required(boolean isRequired);

}

class OneToOneRelationImpl extends JpaEntityRelationshipImpl implements OneToOneRelation {

    private boolean required = false;

    OneToOneRelationImpl(SemanticType fieldType, String name) {
        super(fieldType, name);
    }

    @Override
    public Stream<Annotation> annotations() {
        return Stream.concat(
            Stream.of(Annotation.withType(JpaAnnotations.OneToOne)
                    .withMembers(member -> member.put("optional", !this.required))),
            super.annotations()
        );
    }

    @Override
    public OneToOneRelation required(boolean isRequired) {
        this.required = isRequired;
        return this;
    }

    @Override
    public boolean manyTargets() {
        return false;
    }

    @Override
    public boolean manySources() {
        return false;
    }
}

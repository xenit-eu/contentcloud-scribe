package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.scribe.generator.source.types.Annotation;
import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import java.util.stream.Stream;

public interface OneToOneRelation extends JpaEntityProperty {

    OneToOneRelation required(boolean isRequired);

}

class OneToOneRelationImpl extends JpaEntityFieldImpl implements OneToOneRelation {

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
}

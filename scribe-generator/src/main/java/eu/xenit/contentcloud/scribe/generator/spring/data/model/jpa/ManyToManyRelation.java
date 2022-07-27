package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.scribe.generator.source.types.Annotation;
import eu.xenit.contentcloud.scribe.generator.source.types.CollectionType;
import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import java.util.stream.Stream;

public interface ManyToManyRelation extends JpaEntityRelationship {

}

class ManyToManyRelationImpl extends JpaEntityRelationshipImpl implements ManyToManyRelation {

    private boolean isRequired = false;

    ManyToManyRelationImpl(SemanticType targetType, String name) {
        super(CollectionType.listOf(targetType), name);
    }

    @Override
    public Stream<Annotation> annotations() {
        var annotation = Annotation.withType(JpaAnnotations.ManyToMany);

        return Stream.concat(
                Stream.of(annotation),
                super.annotations()
        );
    }

    @Override
    public boolean manyTargets() {
        return true;
    }

    @Override
    public boolean manySources() {
        return true;
    }
}
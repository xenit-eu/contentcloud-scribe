package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.bard.TypeName;
import eu.xenit.contentcloud.scribe.generator.source.types.Annotation;
import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.SimpleType;

public interface OneToOneRelation extends JpaEntityProperty {

}

class OneToOneRelationImpl extends JpaEntityFieldImpl implements OneToOneRelation {

    OneToOneRelationImpl(SemanticType fieldType, String name) {
        super(fieldType, name);

        this.addAnnotation(Annotation.builder(JpaAnnotations.OneToOne).build());
    }
}

package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.bard.TypeName;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.SimpleType;

public interface OneToOneRelation extends JpaEntityProperty {

}

class OneToOneRelationImpl extends JpaEntityFieldImpl implements OneToOneRelation {

    OneToOneRelationImpl(TypeName fieldType, String name) {
        super(fieldType, name);

        this.addAnnotation(SimpleType.get("javax.persistence", "OneToOne"));
    }
}

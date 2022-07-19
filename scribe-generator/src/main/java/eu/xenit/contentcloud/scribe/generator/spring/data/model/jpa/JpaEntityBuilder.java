package eu.xenit.contentcloud.scribe.generator.spring.data.model.jpa;

import eu.xenit.contentcloud.scribe.generator.source.types.SemanticType;
import eu.xenit.contentcloud.scribe.generator.spring.data.model.JavaBeanBuilder;
import eu.xenit.contentcloud.scribe.generator.spring.data.rest.RestResourceEntity;
import java.util.function.Consumer;

public interface JpaEntityBuilder extends JavaBeanBuilder<JpaEntityBuilder>, JpaEntity {

    JpaEntityBuilder id(Consumer<JpaEntityIdField> customizer);

    JpaEntityBuilder restResource(RestResourceEntity restResource);

    JpaEntityBuilder addOneToOneRelation(String fieldsName, SemanticType targetClass,
            Consumer<OneToOneRelation> customizer);

    JpaEntityBuilder addOneToManyRelation(String fieldName, SemanticType targetClass,
            Consumer<OneToManyRelation> customizer);

    JpaEntityBuilder addManyToOneRelation(String fieldName, SemanticType targetClass,
            Consumer<ManyToOneRelation> customizer);
}
